// XSD4J - XML Schema library for Java
//
// Copyright (C) 2006, 2007 Josef Spillner <spillner@rn.inf.tu-dresden.de>
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// This file is part of the XSD4J library.
// It has been created as part of Project Dynvocation, a research project
// at the Chair of Computer Networks, Faculty for Computer Sciences,
// Dresden University of Technology.
// See http://dynvocation.selfip.net/xsd4j/ for more information.

package org.dynvocation.lib.xsd4j;

import java.util.*;

import javax.xml.namespace.*;

import org.w3c.dom.*;

/**
 * \brief Base class for common XML operations.
 *
 * This class is a thin wrapper around the DOM and namespace functionality
 * provided by the Java class libraries. It is used only internally by
 * XSD4J and therefore not part of its API.
 * However, all of the 'active' classes (not those forming the object
 * tree) of XSD4J are inherited from XMLBase, therefore sharing some
 * methods with it. In particular, \ref getDebug() is a useful method
 * to find out what went wrong on errors.
 *
 * @author Josef Spillner <spillner@rn.inf.tu-dresden.de>
 */
public class XMLBase
{
	// Preferred language for xml:lang-separated content
	private String language = null;
	// Log messages identifier (usually name of the class)
	private String logname = null;
	// The cumulative log, filled by debug() calls
	private String log = new String();
	// Namespace (String) to prefix (String) mapping
	private HashMap global_namespaces;
	// Prefix (String) to namespace (String) reverse mapping
	private HashMap global_prefixes;

	/**
	 * Default constructor.
	 *
	 * Creates an XML base object with some namespaces being initialised
	 * (for XML, Schema and Schema-Instance). Also, the debugging is
	 * initialised to use the derived class' name as a prefix.
	 */
	public XMLBase()
	{
		Class c = this.getClass();
		String n = c.getName();
		initDebug(n);
		initNamespaces();
	}

	/**
	 * Sets the preferred language.
	 *
	 * The language set here affects the first choice of selecting
	 * text when several text nodes of different languages (\b xml:lang)
	 * are available.
	 *
	 * @param language Preferred language for XML content
	 */
	public void setLanguage(String language)
	{
		this.language = language;
	}

	protected String getLanguage()
	{
		return this.language;
	}

	protected void debug(String s)
	{
		this.log += "<<" + this.logname + ">>: " + s + "\n";
	}

	private void initDebug(String logname)
	{
		this.logname = logname;
	}

	/**
	 * Returns the debug messages.
	 *
	 * All debug messages ever created by inheriting objects
	 * are stored as a single string and can be read from here.
	 *
	 * @return Cumulative debug log of the inheriting object
	 */
	public String getDebug()
	{
		return this.log;
	}

	protected String textvalueTr(Node node, String preferred)
	{
		String value = textvalue(node);
		String ret = preferred;

		Element el = (Element)node;
		String lang = el.getAttribute("lang");

		if(preferred == null)
		{
			ret = value;
		}
		else if(lang != null)
		{
			if(lang.equals(language))
			{
				ret = value;
			}
		}

		return ret;
	}

	protected String textvalue(Node node)
	{
		NodeList list = node.getChildNodes();
		for(int i = 0; i < list.getLength(); i++)
		{
			Node node2 = list.item(i);
			if(node2.getNodeType() == Node.TEXT_NODE)
			{
				String value = node2.getNodeValue();
				/*value = value.replace('\n', ' ');*/ // we called normalize()
				return value;
			}
		}
		return "";
	}

	private void initNamespaces()
	{
		String ns_xsd = XSDCommon.NAMESPACE_XSD;
		String ns_xsi = XSDCommon.NAMESPACE_XSI;
		String ns_xml = XSDCommon.NAMESPACE_XML;
		//String ns_xmlns = XSDCommon.NAMESPACE_XMLNS;

		this.global_namespaces = new HashMap();
		this.global_prefixes = new HashMap();

		// FIXME: in order to not let these prefixes always show up,
		// we need a suggestNamespace() in addition to declareNamespace()
		declareNamespace("xsd", ns_xsd);
		declareNamespace("xsi", ns_xsi);
		declareNamespace("xml", ns_xml);
		//declareNamespace("xmlns", ns_xmlns);
	}

	/**
	 * Declares a namespace to be known using a certain prefix.
	 *
	 * Namespace URIs are often attached to elements. Such a namespace can
	 * either be made known here, or it will exist anonymously, so that
	 * generating the XML text representation will result in auto-generated
	 * prefixes for elements under those namespaces.
	 *
	 * This method also accepts a \b null prefix to already schedule
	 * the auto-generation of a prefix name in advance. It is not necessary to
	 * do this however.
	 *
	 * @param prefix Prefix to use for the namespace, or \b null for auto-generation
	 * @param namespace The namespace to be made known
	 */
	public void declareNamespace(String prefix, String namespace)
	{
		String oldprefix = (String)this.global_namespaces.get(namespace);
		if(oldprefix != null)
		{
			if((prefix != null) && (!oldprefix.equals(prefix)))
			{
				debug("(xmlbase) Warning: would have overwritten prefix " + oldprefix + " with " + prefix + " for " + namespace);
			}
			debug("(xmlbase) Info: skip prefix " + prefix + " for " + namespace + " (already found)");
			return;
		}

		if(prefix != null)
		{
			String oldnamespace = (String)this.global_prefixes.get(prefix);
			if((oldnamespace != null) && (!oldnamespace.equals(namespace)))
			{
				debug("(xmlbase) Warning: would have changed prefix " + prefix + " from " + oldnamespace + " to " + namespace);
				prefix = null;
			}
		}

		if(prefix == null)
		{
			prefix = autoprefix();
		}

		debug("(xmlbase) Info: declaring prefix " + prefix + " for " + namespace);
		this.global_namespaces.put(namespace, prefix);
		this.global_prefixes.put(prefix, namespace);
	}

	private String autoprefix()
	{
		//System.out.println("==namespace==: missing for " + nsuri);
		//while(true)
		//{
		Integer num = new Integer(this.global_namespaces.size() + 1);
		String number = num.toString();
		String prefix = "ns" + number;
		//if(this.global_prefixes.get(prefix) == null) break;
		//}
		// FIXME: what is 'nsX' is already present?
		return prefix;
	}

	/**
	 * Sets the namespace information.
	 *
	 * This method takes a mapping from prefixes to
	 * namespace URIs, both represented as strings,
	 * and installs those as the preferred prefixes for the
	 * associated namespace. This information is then used by
	 * \ref XSDDumper and others to serialise the XML.
	 */
	public void declareNamespaces(HashMap namespaces)
	{
		if(namespaces == null)
		{
			debug("(xmlbase) CAUTION: null-ref in declareNamespaces()");
			return;
		}

		debug("(xmlbase) Info: declaring several prefixes...");
		Iterator nsit = namespaces.entrySet().iterator();
		for(int i = 0; i < namespaces.size(); i++)
		{
			Map.Entry entry = (Map.Entry)nsit.next();
			String prefix = (String)entry.getKey();
			String uri = (String)entry.getValue();

			declareNamespace(prefix, uri);
		}
	}

	protected String typename(QName qtype)
	{
		String name;

		if(qtype.getNamespaceURI().length() > 0)
		{
			String nsuri = qtype.getNamespaceURI();

			// Special handling for 'xmlns'
			//if(nsuri == XSDCommon.NAMESPACE_XMLNS)
			if(qtype.getLocalPart().equals("xmlns"))
			{
				return qtype.getLocalPart();
			}

			String prefix = namespaceDeclarationInject(nsuri);

			name = "";
			if(!prefix.equals(""))
			{
				name += prefix;
				name += ":";
			}
			name += qtype.getLocalPart();
		}
		else
		{
			// FIXME: no unqualified name possible!
			//name = qtype.getNodeName();
			name = qtype.getLocalPart();
		}

		debug("(xmlbase) Info: returning typename " + name + " for " + qtype);
		return name;
	}

	protected String namespaceDeclarationInject(String namespace)
	{
		String prefix = (String)this.global_namespaces.get(namespace);
		if(prefix == null)
		{
			prefix = autoprefix();
			debug("(xmlbase) Info: inject prefix " + prefix + " for " + namespace);
			this.global_namespaces.put(namespace, prefix);
			this.global_prefixes.put(prefix, namespace);
		}
		return prefix;
	}

	protected String namespaceDeclarations()
	{
		String s = new String();

		Iterator nsit = this.global_namespaces.entrySet().iterator();
		for(int i = 0; i < this.global_namespaces.size(); i++)
		{
			Map.Entry entry = (Map.Entry)nsit.next();
			String uri = (String)entry.getKey();
			String prefix = (String)entry.getValue();

			if(uri.equals(XSDCommon.NAMESPACE_XML)) continue;
			if(uri.equals(XSDCommon.NAMESPACE_XMLNS)) continue;

			s += " ";
			s += "xmlns";
			if(!prefix.equals(""))
			{
				s += ":" + prefix;
			}
			s += "='";
			s += uri;
			s += "'";
		}

		debug("(xml-base) namespace declarations: " + s);

		return s;
	}

	protected HashMap namespaceDeclarationsMap()
	{
		return this.global_prefixes;
	}

	protected String namespaceDeclaration(String prefix)
	{
		return (String)this.global_prefixes.get(prefix);
	}
}

