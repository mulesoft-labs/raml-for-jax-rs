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
import java.math.*;

import javax.xml.namespace.*;

import org.w3c.dom.*;

/**
 * \brief Converter for \ref XSDSchema objects to XML data.
 *
 * Objects of this type can be used to take an existing schema tree and
 * convert it back to either a XML DOM tree or furthermore a plain
 * text describing the schema according to the XML Schema specification.
 * Only in \ref XSDParser::PARSER_FLAT mode the original structure
 * is preserved.
 *
 * @author Josef Spillner <spillner@rn.inf.tu-dresden.de>
 */
public class XSDDumper extends XMLBase
{
	// accumulated string of serialised XML output
	private String outputstring;
	// contains the next suffix number for auto-generated type names
	//private int typecounter;

	/**
	 * Default constructor.
	 *
	 * Creates a XSDDumper object which can then be used to convert
	 * schema trees.
	 * Known namespaces are XML Schema (xsd) and XML Schema instance
	 * (xsi), and others might need to be added via \ref
	 * XMLBase::declareNamespaces.
	 * In particular, \ref XSDParser objects make it possible to get
	 * all namespace definitions by calling \ref XSDParser::getNamespaces.
	 */
	public XSDDumper()
	{
		super();

		//this.typecounter = 0; // FIXME: unused now?
	}

	// Adds a string part to the XML serialisation string
	private void output(String s)
	{
		this.outputstring += s;
		this.outputstring += "\n";
	}

	/**
	 * Dumps an XML Schema object.
	 *
	 * This method takes an object of type \ref XSDSchema and converts it to
	 * text which is then returned as a string and can be written to standard
	 * output.
	 * This is a convenience method for calling convert followed by dumpXML.
	 *
	 * @param schema The schema tree to convert to text
	 * @return String representation of the XML Schema tree
	 * @see convert
	 */
	public String dump(XSDSchema schema)
	{
		Element el = convert(schema);
		return dumpXML(el);
	}
	
	/**
	 * Converts an XML Schema object to an XML DOM element.
	 *
	 * This method takes an object of type \ref XSDSchema and converts it to
	 * an XML element. The element can further be output via \ref
	 * dumpXML, which can be combined to \ref dump.
	 *
	 * @param xsdschema The schema tree to convert to XML
	 * @return XML DOM element representing the schema, or \b null on error
	 */
	public Element convert(XSDSchema xsdschema)
	{
		Element el;

		if(xsdschema == null)
		{
			debug("ERROR: schema is null!");
			return null;
		}

		Document doc = XSDCommon.createDocument();
		
		if(doc != null)
		{
			String ns_xsd = XSDCommon.NAMESPACE_XSD;
			el = doc.createElementNS(ns_xsd, "schema");

			if(xsdschema.getTargetNamespace() != null)
			{
				el.setAttribute("targetNamespace", xsdschema.getTargetNamespace());
			}

			definitions(doc, el, xsdschema);

			return el;
		}
		else
		{
			debug("## Failure!");
		}

		return null;
	}

	/**
	 * Dumps an XML schema element.
	 *
	 * This method is similar to \ref dump, but instead only converts a single
	 * element which is suitable for e.g. element-based messages.
	 * Only the parts of the schema necessary for the declaration of the
	 * element are included in the output.
	 * This is a convenience method for calling convertElement followed by dumpXML.
	 *
	 * @param xsdelement XML Schema element which is part of a schema tree
	 * @param xsdschema Parent XML Schema to the element
	 * @return String representation of the XML Schema element
	 * @see convertElement
	 */
	public String dumpElement(XSDElement xsdelement, XSDSchema xsdschema)
	{
		Element el = convertElement(xsdelement, xsdschema);
		return dumpXML(el);
	}

	/**
	 * Converts an XML Schema element object to an XML DOM element.
	 *
	 * This method takes an object of type \ref XSDElement and converts it to
	 * an XML element.
	 * The schema's namespace will become the target namespace.
	 *
	 * @param xsdelement XML Schema element to convert to XML
	 * @param xsdschema Parent schema for the element
	 * @return XML DOM element representing the element, or \b null on error
	 */
	public Element convertElement(XSDElement xsdelement, XSDSchema xsdschema)
	{
		Element el;

		Document doc = XSDCommon.createDocument();

		if(doc != null)
		{
			String ns_xsd = XSDCommon.NAMESPACE_XSD;
			el = doc.createElementNS(ns_xsd, "schema");

			// FIXME: parent schema?
			//String ns = xsdschema.getTargetNamespace();
			String ns = xsdelement.getName().getNamespaceURI();
			if(ns != null)
			{
				el.setAttribute("targetNamespace", ns);
			}

			//xsddump_element(xsdelement, doc, el);
			// FIXME: recursive type inclusion -> "dirty" marker during xsddump_*?
			// FIXME: use XSDTransformer to get "partial" schema

			XSDTransformer transformer = new XSDTransformer();
			XSDSchema xsdpartial = transformer.copyPartial(xsdelement, xsdschema);
			definitions(doc, el, xsdpartial);

			return el;
		}
		else
		{
			debug("## Failure!");
		}

		return null;
	}

	// Dump all the top-level entries of a schema
	private void definitions(Document doc, Element parent, XSDSchema xsdschema)
	{
		ArrayList elements = xsdschema.getElements();
		for(int i = 0; i < elements.size(); i++)
		{
			XSDElement xsdelement = (XSDElement)elements.get(i);
			xsddump_element(xsdelement, doc, parent);
		}

		ArrayList attributes = xsdschema.getAttributes();
		for(int i = 0; i < attributes.size(); i++)
		{
			XSDAttribute xsdattribute = (XSDAttribute)attributes.get(i);
			xsddump_attribute(xsdattribute, doc, parent);
		}

		ArrayList types = xsdschema.getTypes();
		for(int i = 0; i < types.size(); i++)
		{
			XSDType xsdtype = (XSDType)types.get(i);
			xsddump_type(xsdtype, doc, parent);
		}

		ArrayList groups = xsdschema.getGroups();
		for(int i = 0; i < groups.size(); i++)
		{
			XSDSequence xsdgroup = (XSDSequence)groups.get(i);
			xsddump_group(xsdgroup, doc, parent);
		}
	}

	/**
	 * XML serialisation method.
	 *
	 * This method takes an XML DOM element and transforms it to plain
	 * text in XML format, which is then returned as a string suitable for
	 * writing to standard output or passing to other objects.
	 * This is particularly useful with XML elements describing XSD
	 * as returned by \ref convert or \ref convertElement.
	 *
	 * @param el XML DOM Element object
	 *
	 * @return String of the the element's XML representation
	 */
	public String dumpXML(Element el)
	{
		this.outputstring = new String();

		if(el != null)
		{
			dumpdom(el, true);
		}

		return this.outputstring;
	}

	// Return the string to use as the name for a tag
	private String tagname(Node n)
	{
		String name;
		QName qtype;

		if(n.getNamespaceURI() == null)
		{
			qtype = null;
			name = n.getNodeName();
		}
		else
		{
			qtype = new QName(n.getNamespaceURI(), n.getLocalName());
			name = typename(qtype);
		}

		debug("+ tagname: " + name + " for " + n + " (" + qtype + ")");

		return name;
	}

	private void scannamespaces(Element el)
	{
		//debug("-- scan " + el + " (" + el.getLocalName() + ") {" + el.getNamespaceURI() + "}");
		if(el.getNamespaceURI() != null)
		{
			// FIXME: only if no prefix associated yet!
			String namespace = el.getNamespaceURI();
			String prefix = el.getPrefix();
			declareNamespace(prefix, namespace);

			String newprefix = namespaceDeclarationInject(namespace);
			debug("found namespace: " + namespace);
			debug(" -> prefix: " + prefix);
			debug(" -> new prefix: " + newprefix);
		}

		NamedNodeMap map = el.getAttributes();
		for(int i = 0; i < map.getLength(); i++)
		{
			Attr a = (Attr)map.item(i);

			if(a.getNamespaceURI() != null)
			{
				// FIXME: only if no prefix associated yet!
				String namespace = a.getNamespaceURI();
				String prefix = a.getPrefix();
				declareNamespace(prefix, namespace);

				String newprefix = namespaceDeclarationInject(namespace);
				debug("found att-namespace: " + namespace);
				debug(" -> prefix: " + prefix);
				debug(" -> new prefix: " + newprefix);
			}
		}

		NodeList list = el.getChildNodes();

		for(int i = 0; i < list.getLength(); i++)
		{
			Node node = list.item(i);
			if(node.getNodeType() == Node.ELEMENT_NODE)
			{
				scannamespaces((Element)node);
			}
		}
	}

	// FIXME: namespace of attributes
	private void dumpdom(Element el, boolean outermost)
	{
		String s;
		//String tagname;

		if(outermost)
		{
			scannamespaces(el);
		}

		s = "<";
		s += tagname(el);

		if(outermost)
		{
			s += namespaceDeclarations();
		}

		NamedNodeMap map = el.getAttributes();
		for(int i = 0; i < map.getLength(); i++)
		{
			Attr a = (Attr)map.item(i);
			String nsuri = a.getNamespaceURI();

			if((nsuri == null) || (!nsuri.equals(XSDCommon.NAMESPACE_XMLNS)))
			{
				String value = a.getValue();
				value = value.replaceAll("'", "&apos;");
				value = value.replaceAll("<", "&lt;");
				value = value.replaceAll(">", "&gt;");

				s += " ";
				s += tagname(a);
				s += "='";
				s += value;
				s += "'";
			}
		}

		NodeList list = el.getChildNodes();
		if(list.getLength() > 0)
		{
			s += ">";

			// Find out whether it's a simple value or not
			boolean simple = false;
			if(list.getLength() == 1)
			{
				Node n = list.item(0);
				if(n.getNodeType() == Node.TEXT_NODE)
				{
					simple = true;
				}
			}

			if(!simple)
			{
				output(s);
			}

			for(int i = 0; i < list.getLength(); i++)
			{
				Node node = list.item(i);
				if(node.getNodeType() == Node.ELEMENT_NODE)
				{
					dumpdom((Element)node, false);
				}
				else if(node.getNodeType() == Node.TEXT_NODE)
				{
					Text tn = (Text)node;
					//output(tn.getWholeText());
					if(!simple)
					{
						output(tn.getData());
					}
					else
					{
						s += tn.getData();
					}
				}
				else if(node.getNodeType() == Node.CDATA_SECTION_NODE)
				{
					CDATASection cdn = (CDATASection)node;
					String cdata = "<![CDATA[" + cdn.getData() + "]]>";
					output(cdata);
				}
			}

			if(!simple)
			{
				output("</" + tagname(el) + ">");
			}
			else
			{
				s += "</" + tagname(el) + ">";
				output(s);
			}
		}
		else
		{
			s += " />";
			output(s);
		}
	}

	private void xsddump_element(XSDElement xsdelement, Document doc, Element parent)
	{
		String ns_xsd = XSDCommon.NAMESPACE_XSD;

		// Element groups are hidden behind pseudo-elements, as are choices
		if(xsdelement.getType() != null)
		{
			XSDType xsdtype = xsdelement.getType();

			if(xsdtype.getType() == XSDType.TYPE_GROUP)
			{
				xsddump_group(xsdtype.getSequence(), doc, parent);
				return;
			}
			else if(xsdtype.getType() == XSDType.TYPE_CHOICE)
			{
				xsddump_choice(xsdtype.getChoice(), doc, parent);
				return;
			}
		}

		Element xsd_element = doc.createElementNS(ns_xsd, "element");
		parent.appendChild(xsd_element);

		if(xsdelement.getMinOccurs() != 1)
		{
			String numstr = new Integer(xsdelement.getMinOccurs()).toString();
			xsd_element.setAttribute("minOccurs", numstr);
		}

		if(xsdelement.getMaxOccurs() != 1)
		{
			String numstr = new Integer(xsdelement.getMaxOccurs()).toString();
			if(xsdelement.getMaxOccurs() == XSDElement.OCCURS_UNBOUNDED)
			{
				numstr = "unbounded";
			}
			xsd_element.setAttribute("maxOccurs", numstr);
		}

		if(xsdelement.getRef() != null)
		{
			xsd_element.setAttribute("ref", typename(xsdelement.getRef()));

			return;
		}

		// Below here, only non-reference information

		if(xsdelement.getName() != null)
		{
			xsd_element.setAttribute("name", xsdelement.getName().getLocalPart());
		}

		if(xsdelement.getTypeRef() != null)
		{
			String xtype = typename(xsdelement.getTypeRef());
			xsd_element.setAttribute("type", xtype);
		}

		if(xsdelement.getType() != null)
		{
			XSDType t = xsdelement.getType();

			if(t.getType() == XSDType.TYPE_COMPLEX)
			{
				if(t.getName() == null)
				{
					xsddump_complextype(t, doc, xsd_element);
				}
			}
			else
			{
				if(t.getName() == null)
				{
					xsddump_simpletype(t, doc, xsd_element);
				}
			}
		}

		if(xsdelement.getQualified())
		{
			xsd_element.setAttribute("form", "qualified");
		}

		if(xsdelement.getNillable())
		{
			xsd_element.setAttribute("nillable", "true");
		}

		String defaultvalue = xsdelement.getDefaultValue();
		String fixedvalue = xsdelement.getFixedValue();

		if(defaultvalue != null)
		{
			xsd_element.setAttribute("default", defaultvalue);
		}

		if(fixedvalue != null)
		{
			xsd_element.setAttribute("fixed", fixedvalue);
		}
	}

	private void xsddump_type(XSDType xsdtype, Document doc, Element parent)
	{
		if(xsdtype.getType() == XSDType.TYPE_COMPLEX)
		{
			xsddump_complextype(xsdtype, doc, parent);
		}
		else
		{
			xsddump_simpletype(xsdtype, doc, parent);
		}
	}

	private void xsddump_group(XSDSequence xsdgroup, Document doc, Element parent)
	{
		if(xsdgroup.getGroupAttribute())
		{
			xsddump_attributegroup(xsdgroup, doc, parent);
		}
		else
		{
			xsddump_elementgroup(xsdgroup, doc, parent);
		}
	}

	private void xsddump_choice(XSDChoice xsdchoice, Document doc, Element parent)
	{
		String ns_xsd = XSDCommon.NAMESPACE_XSD;

		Element xsd_choice = doc.createElementNS(ns_xsd, "choice");
		parent.appendChild(xsd_choice);

		ArrayList elements = xsdchoice.getElements();
		for(int i = 0; i < elements.size(); i++)
		{
			XSDElement xsdchild = (XSDElement)elements.get(i);
			xsddump_element(xsdchild, doc, xsd_choice);
		}

		ArrayList groups = xsdchoice.getGroups();
		for(int i = 0; i < groups.size(); i++)
		{
			XSDSequence xsdgroup = (XSDSequence)groups.get(i);
			xsddump_group(xsdgroup, doc, xsd_choice);
		}
	}

	private void xsddump_complextype(XSDType xsdtype, Document doc, Element parent)
	{
		String ns_xsd = XSDCommon.NAMESPACE_XSD;

		Element xsd_complextype = doc.createElementNS(ns_xsd, "complexType");
		parent.appendChild(xsd_complextype);

		if(xsdtype.getName() != null)
		{
			xsd_complextype.setAttribute("name", xsdtype.getName().getLocalPart());
		}

		Element attparent = xsd_complextype;
		XSDType basetype = xsdtype.getBaseType();

		// Simple content or complex extension, or complex restriction
		if(basetype != null)
		{
			Element xsd_simplecontent;
			if(basetype.getType() == XSDType.TYPE_COMPLEX)
			{
				xsd_simplecontent = doc.createElementNS(ns_xsd, "complexContent");
			}
			else
			{
				xsd_simplecontent = doc.createElementNS(ns_xsd, "simpleContent");
			}
			xsd_complextype.appendChild(xsd_simplecontent);

			Element xsd_extension;

			if(xsdtype.getRestricted())
			{
				xsd_extension = doc.createElementNS(ns_xsd, "restriction");
			}
			else
			{
				xsd_extension = doc.createElementNS(ns_xsd, "extension");
			}
			xsd_simplecontent.appendChild(xsd_extension);
			String basetypename = typename(basetype.getName());
			xsd_extension.setAttribute("base", basetypename);

			attparent = xsd_extension;
		}

		XSDSequence xsdseq = xsdtype.getSequence();
		if(xsdseq != null)
		{
			Element xsd_sequence = doc.createElementNS(ns_xsd, "sequence");
			attparent.appendChild(xsd_sequence);

			ArrayList list = xsdseq.getElements();
			for(int i = 0; i < list.size(); i++)
			{
				XSDElement xsdchild = (XSDElement)list.get(i);
				xsddump_element(xsdchild, doc, xsd_sequence);
			}
		}

		ArrayList attributes = xsdtype.getAttributes();
		for(int i = 0; i < attributes.size(); i++)
		{
			XSDAttribute xsdatt = (XSDAttribute)attributes.get(i);
			xsddump_attribute(xsdatt, doc, attparent);
		}
	}

	private void xsddump_simpletype(XSDType xsdtype, Document doc, Element parent)
	{
		String ns_xsd = XSDCommon.NAMESPACE_XSD;

		// FIXME: ???
		if((xsdtype.getType() == XSDType.TYPE_CHOICE)
		|| (xsdtype.getType() == XSDType.TYPE_ATTRIBUTEGROUP))
		{
			return;
		}

		Element xsd_st = doc.createElementNS(ns_xsd, "simpleType");
		parent.appendChild(xsd_st);

		if(xsdtype.getName() != null)
		{
			xsd_st.setAttribute("name", xsdtype.getName().getLocalPart());
		}

		if(xsdtype.getType() == XSDType.TYPE_LIST)
		{
			Element xsd_res = doc.createElementNS(ns_xsd, "list");
			xsd_st.appendChild(xsd_res);

			if(xsdtype.getBaseType() != null)
			{
				if(xsdtype.getBaseType().getName() != null)
				{
					xsd_res.setAttribute("itemType", typename(xsdtype.getBaseType().getName()));
					// FIXME: trigger only in MERGED mode?
					//xsddump_simpletype(xsdtype.getBaseType(), doc, parent);
				}
				else
				{
					// inline type
					xsddump_simpletype(xsdtype.getBaseType(), doc, xsd_st);
				}
			}
			else
			{
				// reference, not resolved yet
				xsd_res.setAttribute("itemType", typename(xsdtype.getBaseRef()));
			}

			return;
		}

		if(xsdtype.getType() == XSDType.TYPE_UNION)
		{
			ArrayList memberrefs = xsdtype.getMemberRefs();
			String membertypes = new String();
			for(int i = 0; i < memberrefs.size(); i++)
			{
				QName memberref = (QName)memberrefs.get(i);
				if(membertypes.length() > 0) membertypes += " ";
				membertypes += typename(memberref);

				//xsddump_simpletype(member, doc, parent);
			}

			Element xsd_res = doc.createElementNS(ns_xsd, "union");
			xsd_res.setAttribute("memberTypes", membertypes);
			xsd_st.appendChild(xsd_res);
			return;
		}

		XSDRestriction xsdres = xsdtype.getRestriction();
		if(xsdres != null)
		{
			BitSet res = xsdres.getRestrictions();

			Element xsd_res = doc.createElementNS(ns_xsd, "restriction");
			xsd_st.appendChild(xsd_res);

			if(xsdtype.getBaseType() != null)
			{
				if(xsdtype.getBaseType().getName() != null)
				{
					String basetype = typename(xsdtype.getBaseType().getName());
					xsd_res.setAttribute("base", basetype);
					// FIXME: trigger xsddump_simpletype in MERGED mode?
					//xsddump_simpletype(xsdtype.getBaseType(), doc, xsd_st);
				}
				else
				{
					// inline type, no name
					xsddump_simpletype(xsdtype.getBaseType(), doc, xsd_res);
				}
			}
			else
			{
				// reference, not resolved yet
				String basetype = typename(xsdtype.getBaseRef());
				xsd_res.setAttribute("base", basetype);
			}

			if(res.get(XSDRestriction.RESTRICTION_MIN_INCLUSIVE))
			{
				Element xsd_in = doc.createElementNS(ns_xsd, "minInclusive");
				BigInteger min = xsdres.getMinInclusive();
				xsd_in.setAttribute("value", min.toString());
				xsd_res.appendChild(xsd_in);
			}
			if(res.get(XSDRestriction.RESTRICTION_MAX_INCLUSIVE))
			{
				Element xsd_in = doc.createElementNS(ns_xsd, "maxInclusive");
				BigInteger max = xsdres.getMaxInclusive();
				xsd_in.setAttribute("value", max.toString());
				xsd_res.appendChild(xsd_in);
			}
			if(res.get(XSDRestriction.RESTRICTION_MIN_EXCLUSIVE))
			{
				Element xsd_ex = doc.createElementNS(ns_xsd, "minExclusive");
				BigInteger min = xsdres.getMinExclusive();
				xsd_ex.setAttribute("value", min.toString());
				xsd_res.appendChild(xsd_ex);
			}
			if(res.get(XSDRestriction.RESTRICTION_MAX_EXCLUSIVE))
			{
				Element xsd_ex = doc.createElementNS(ns_xsd, "maxExclusive");
				BigInteger max = xsdres.getMaxExclusive();
				xsd_ex.setAttribute("value", max.toString());
				xsd_res.appendChild(xsd_ex);
			}
			if(res.get(XSDRestriction.RESTRICTION_TOTAL_DIGITS))
			{
				Element xsd_td = doc.createElementNS(ns_xsd, "totalDigits");
				BigInteger digits = xsdres.getTotalDigits();
				xsd_td.setAttribute("value", digits.toString());
				xsd_res.appendChild(xsd_td);
			}
			if(res.get(XSDRestriction.RESTRICTION_FRACTION_DIGITS))
			{
				Element xsd_fd = doc.createElementNS(ns_xsd, "fractionDigits");
				BigInteger digits = xsdres.getFractionDigits();
				xsd_fd.setAttribute("value", digits.toString());
				xsd_res.appendChild(xsd_fd);
			}
			if(res.get(XSDRestriction.RESTRICTION_PATTERN))
			{
				Element xsd_pattern = doc.createElementNS(ns_xsd, "pattern");
				xsd_pattern.setAttribute("value", xsdres.getPattern());
				xsd_res.appendChild(xsd_pattern);
			}
			if(res.get(XSDRestriction.RESTRICTION_LENGTH))
			{
				Element xsd_length = doc.createElementNS(ns_xsd, "length");
				BigInteger length = xsdres.getLength();
				xsd_length.setAttribute("value", length.toString());
				xsd_res.appendChild(xsd_length);
			}
			if(res.get(XSDRestriction.RESTRICTION_MIN_LENGTH))
			{
				Element xsd_ml = doc.createElementNS(ns_xsd, "minLength");
				BigInteger length = xsdres.getMinLength();
				xsd_ml.setAttribute("value", length.toString());
				xsd_res.appendChild(xsd_ml);
			}
			if(res.get(XSDRestriction.RESTRICTION_MAX_LENGTH))
			{
				Element xsd_ml = doc.createElementNS(ns_xsd, "maxLength");
				BigInteger length = xsdres.getMaxLength();
				xsd_ml.setAttribute("value", length.toString());
				xsd_res.appendChild(xsd_ml);
			}
			if(res.get(XSDRestriction.RESTRICTION_WHITE_SPACE))
			{
				Element xsd_ws = doc.createElementNS(ns_xsd, "whiteSpace");
				int policy = xsdres.getWhiteSpace();
				String value = null;
				if(policy == XSDRestriction.WHITESPACE_COLLAPSE)
					value = "collapse";
				else if(policy == XSDRestriction.WHITESPACE_COLLATE)
					value = "collate";
				else if(policy == XSDRestriction.WHITESPACE_REPLACE)
					value = "replace";
				else if(policy == XSDRestriction.WHITESPACE_PRESERVE)
					value = "preserve";
				xsd_ws.setAttribute("value", value);
				xsd_res.appendChild(xsd_ws);
			}
			if(res.get(XSDRestriction.RESTRICTION_ENUMERATION))
			{
				ArrayList enumerations = xsdres.getEnumerations();
				for(int i = 0; i < enumerations.size(); i++)
				{
					String enumeration = (String)enumerations.get(i);
					Element xsd_e = doc.createElementNS(ns_xsd, "enumeration");
					xsd_e.setAttribute("value", enumeration);
					xsd_res.appendChild(xsd_e);
				}
			}
		}
	}

	private void xsddump_attribute(XSDAttribute xsdatt, Document doc, Element parent)
	{
		String ns_xsd = XSDCommon.NAMESPACE_XSD;

		// Attribute groups are hidden behind pseudo-attributes
		if(xsdatt.getType() != null)
		{
			XSDType xsdtype = xsdatt.getType();

			if(xsdtype.getType() == XSDType.TYPE_ATTRIBUTEGROUP)
			{
				xsddump_group(xsdtype.getSequence(), doc, parent);
				return;
			}
		}

		Element xsd_att = doc.createElementNS(ns_xsd, "attribute");
		parent.appendChild(xsd_att);

		int use = xsdatt.getUse();
		if(use == XSDAttribute.USE_OPTIONAL)
		{
			xsd_att.setAttribute("use", "optional");
		}
		else if(use == XSDAttribute.USE_REQUIRED)
		{
			// default
		}
		else if(use == XSDAttribute.USE_PROHIBITED)
		{
			xsd_att.setAttribute("use", "prohibited");
		}

		if(xsdatt.getRef() != null)
		{
			xsd_att.setAttribute("ref", typename(xsdatt.getRef()));

			return;
		}

		// Below here, only non-reference information

		if(xsdatt.getName() != null)
		{
			xsd_att.setAttribute("name", xsdatt.getName().getLocalPart());
		}

		String defaultvalue = xsdatt.getDefaultValue();
		String fixedvalue = xsdatt.getFixedValue();

		if(defaultvalue != null)
		{
			xsd_att.setAttribute("default", defaultvalue);
		}

		if(fixedvalue != null)
		{
			xsd_att.setAttribute("fixed", fixedvalue);
		}

		if(xsdatt.getTypeRef() != null)
		{
			xsd_att.setAttribute("type", typename(xsdatt.getTypeRef()));
		}

		if(xsdatt.getType() != null)
		{
			XSDType xsdtype = xsdatt.getType();

			if(xsdtype.getName() == null)
			{
				xsddump_simpletype(xsdtype, doc, xsd_att);
			}
		}
	}

	private void xsddump_attributegroup(XSDSequence xsdgroup, Document doc, Element parent)
	{
		String ns_xsd = XSDCommon.NAMESPACE_XSD;

		Element xsd_ag = doc.createElementNS(ns_xsd, "attributeGroup");
		parent.appendChild(xsd_ag);

		if(xsdgroup.getGroupRef() != null)
		{
			xsd_ag.setAttribute("ref", typename(xsdgroup.getGroupRef()));

			return;
		}

		// Below here, only non-reference information

		if(xsdgroup.getGroupName() != null)
		{
			xsd_ag.setAttribute("name", xsdgroup.getGroupName().getLocalPart());
		}

		ArrayList attributes = xsdgroup.getAttributes();
		for(int i = 0; i < attributes.size(); i++)
		{
			XSDAttribute xsdatt = (XSDAttribute)attributes.get(i);
			xsddump_attribute(xsdatt, doc, xsd_ag);
		}
	}

	private void xsddump_elementgroup(XSDSequence xsdgroup, Document doc, Element parent)
	{
		String ns_xsd = XSDCommon.NAMESPACE_XSD;

		Element xsd_eg = doc.createElementNS(ns_xsd, "group");
		parent.appendChild(xsd_eg);

		if(xsdgroup.getGroupRef() != null)
		{
			xsd_eg.setAttribute("ref", typename(xsdgroup.getGroupRef()));

			return;
		}

		// Below here, only non-reference information

		if(xsdgroup.getGroupName() != null)
		{
			xsd_eg.setAttribute("name", xsdgroup.getGroupName().getLocalPart());
		}

		ArrayList elements = xsdgroup.getElements();
		for(int i = 0; i < elements.size(); i++)
		{
			XSDElement xsdelement = (XSDElement)elements.get(i);
			xsddump_element(xsdelement, doc, xsd_eg);
		}
	}
}

