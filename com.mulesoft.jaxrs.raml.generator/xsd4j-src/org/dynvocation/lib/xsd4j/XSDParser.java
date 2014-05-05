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
import java.io.IOException;

import javax.xml.parsers.*;
import javax.xml.namespace.*;

import org.w3c.dom.*;
import org.xml.sax.*;

/**
 * \mainpage
 *
 * XSD4J is a library to handle XML Schema Definitions (XSD).
 * Using it is very straight-forward, when orienting to the following
 * introduction.
 *
 * \section Parsing
 *
 * Parsing a file or embedded schema is done via a \ref XSDParser object.
 * The method parseSchemaFile (or parseSchemaElement) returns either a
 * XSDSchema object for further use, or \b null, in which case the
 * getDebug method can be used to write out the error messages.
 * The access level should be chosen appropriately; they are documented
 * in detail in the XSD4J design document.
 *
 * \section Using
 *
 * In the "flat" access level, a XSDSchema object contains lists of all
 * \ref XSDElement, \ref XSDAttribute and \ref XSDType definitions
 * found in it (and possibly in include files in the "flat-includes" level).
 *
 * The XSDSchema object presents a tree in the "tree" access level. It can
 * be used like any other \ref XSDSequence object to get the list of
 * all elements via getElements, which in turn provide their
 * \ref XSDType with the getType method. The type is either complex,
 * in which case a new sequence is attached to it and attributes may
 * be found, or simple, so that one starts to check for possible
 * value restrictions.
 *
 * Since a lot of special types to denote attribute groups or choice
 * elements or even base types need to be considered when working with
 * the XSDSchema, the "merged" mode makes work easier by merging in all
 * referenced elements and attributes.
 *
 * The \ref XSDDumper class finally is available to rebuild the XML which
 * describes the schema, and print it out somewhere. Similarly, \ref
 * XSDInstantiator, \ref XSDTransformer and \ref XSDValidator can further
 * process XSDSchema objects.
 *
 * \section Information
 *
 * Additional information about XSD4J can be found on its home page:
 * http://dynvocation.selfip.net/xsd4j/
 */

/**
 * \brief Parser class for the XSD format.
 *
 * The parser creates an XSDSchema object, which in turn contains all the
 * definitions for elements and complex types needed to evaluate the schema.
 * The XML schema source can either be an XSD file, in which case
 * \ref parseSchemaFile is to be used, or an XML element, then \ref
 * parseSchemaElement is to be used.
 * If other XML fragments contain additional information for the
 * schema, call \ref addSchemaElement for each of those before the
 * parse call to \ref parseSchemaElement with a \b null schema.
 * For XSD embedded into other XML files, \ref addNamespace must
 * probably be called before parsing also for each namespace definition
 * which might occur in \b type or \b ref attributes.
 *
 * The schema access level is specified with the parse call, but can
 * be augmented later by using a \ref XSDTransformer object.
 *
 * Note that parsing schema files might trigger file downloads over
 * the internet unless the \ref PARSER_FLAT level is chosen.
 *
 * @author Josef Spillner <spillner@rn.inf.tu-dresden.de>
 */
public class XSDParser extends XMLBase
{
	// Namespace list: String->String
	// FIXME: merge with XMLBase declareNamespace method?
	private HashMap namespaces = null;
	// List of (possibly cross-referenced) schemas (XML Element)
	private ArrayList schemas = null;
	// List of pre-loaded schemas (XML Element) and URLs (String)
	private ArrayList preloadschemas = null;
	private ArrayList preloadfiles = null;
	// List of pre-loaded and pre-parsed schema
	private XSDSchema preloadxsd = null;
	// Namespace defaults
	private String tns = null;
	private boolean attributeformdefault = false;
	private boolean elementformdefault = false;

	// Schema access level
	private int level = -1;

	/** Flat access to a schema: no includes, no object tree. */
	public static final int PARSER_FLAT = 1;
	/** Flat access, parsing also all referenced include files. */
	public static final int PARSER_FLAT_INCLUDES = 2;
	/** Single-linked object tree in addition to flat access. */
	public static final int PARSER_TREE = 3;
	/** Merged access, altering the schema structure as needed. */
	public static final int PARSER_MERGED = 4;

	/**
	 * Default constructor.
	 * 
	 * By calling it, an object of type XSDParser is created, which can then
	 * be used for further method calls.
	 */
	public XSDParser()
	{
		this.namespaces = new HashMap();
		this.schemas = new ArrayList();
		this.preloadfiles = new ArrayList();
		this.preloadschemas = new ArrayList();
		this.preloadxsd = new XSDSchema();

		addNamespace("xml", XSDCommon.NAMESPACE_XML);
	}

	/**
	 * Adds an external namespace declaration.
	 *
	 * In some cases, when XML Schema information is embedded into other
	 * host documents, target namespace information is not contained
	 * in the schema element itself.
	 * This method makes such namespaces known to the parser.
	 *
	 * @param prefix The namespace prefix to declare
	 * @param namespace Full namespace URL to associate with the prefix
	 */
	public void addNamespace(String prefix, String namespace)
	{
		this.namespaces.put(prefix, namespace);
	}

	/**
	 * Returns all declared namespaces.
	 *
	 * In order to get all the namespaces together with their
	 * prefix information, this method can be used to get them
	 * all at once.
	 *
	 * @return A map containing a namespace (value) for each prefix (key)
	 *
	 * @see addNamespace
	 */
	public HashMap getNamespaces()
	{
		return this.namespaces;
	}

	/**
	 * Adds an additional XML element containing a schema.
	 *
	 * If several XML document fragments contain schema information referencing
	 * each other, all of those fragments must be given first before any
	 * parsing can take place. Using \ref PARSER_FLAT is an exception since
	 * it doesn't check for self-contained schemas.
	 * This method is useful for schema fragments in WSDL files, but is also
	 * used internally when \b include or \b import elements are found
	 * within the schema.
	 *
	 * @param schema XML element containing schema
	 *
	 * @see parseSchemaElement
	 * @see parseSchemaFile
	 */
	public void addSchemaElement(Element schema)
	{
		this.schemas.add(schema);
	}

	/**
	 * Preloads a file with Schema type definitions.
	 *
	 * Sometimes, references to types, elements and attributes are
	 * encountered in XSD files without them being included or
	 * imported correctly. Furthermore, the XSD built-in type definitions
	 * are not available by default.
	 * Preloading those files (also called bootstrapping in the case
	 * of XSD definitions) leads to a complete derivation tree.
	 * This method doesn't actually load the file, but instead will
	 * resolve it whenever the real parsing is done.
	 * It can be considered a forced include statement, and will
	 * be preserved (and cached) across parser calls.
	 *
	 * @param schemafile XSD file with definitions to preload
	 *
	 * @see parseSchemaFile
	 * @see parseSchemaElement
	 */
	public void preloadSchemaFile(String schemafile)
	{
		this.preloadfiles.add(schemafile);
	}

	// FIXME: there can only be one such schema for now, since preloadschemas is ArrayList<Element>!
	public void preloadSchema(XSDSchema xsdschema)
	{
		//this.preloadschemas.add(xsdschema);
		this.preloadxsd = xsdschema;
	}

	/**
	 * Parses an XML Schema file and creates an \ref XSDSchema object.
	 *
	 * This method is used for parsing freestanding XSD files. Use
	 * \ref parseSchemaElement instead to parse XSD information embedded
	 * in other XML files such as WSDL.
	 *
	 * @param schemafile The file containing XML Schema data
	 * @param level Schema access level, e.g. \ref PARSER_FLAT
	 * @return \ref XSDSchema object, or \b null in case of invalid data
	 */
	public XSDSchema parseSchemaFile(String schemafile, int level)
	{
		debug("Parsing XSD file " + schemafile);

		Element schema = load(schemafile);
		if(schema == null)
		{
			debug("Error: file could not be loaded");
			return null;
		}

		return parseSchemaElement(schema, level);
	}

	private boolean scanIncludes(Element schema)
	{
		debug("Scanning for included schema files...");
		XSDSchema xsdschema = new XSDSchema();
		xsdschema = parseSchemaInternal(schema, xsdschema, true, true);
		debug("Scanning done.");

		if(xsdschema == null)
		{
			return false;
		}
		return loadIncludes(xsdschema);
	}

	private boolean loadIncludes(XSDSchema xsdschema)
	{
		debug("Handling include files.");

		ArrayList includes = xsdschema.getIncludes();
		ArrayList imports = xsdschema.getImports();
		ArrayList importnamespaces = xsdschema.getImportNamespaces();
		ArrayList newschemas = new ArrayList();

		for(int i = 0; i < includes.size(); i++)
		{
			String location = (String)includes.get(i);
			debug("- include: " + location);

			Element incschema = load(location);
			if(incschema != null)
			{
				addSchemaElement(incschema);
				newschemas.add(incschema);
			}
			else
			{
				debug("Error: include file error");
				return false;
			}
		}

		for(int i = 0; i < imports.size(); i++)
		{
			String location = (String)imports.get(i);
			String namespace = (String)importnamespaces.get(i);
			debug("- import: " + location + " with namespace " + namespace);

			if(namespace.equals("http://www.w3.org/XML/1998/namespace"))
			{
				debug("- special handling for XML namespace");
				// FIXME: how to do this right - bootstrapping of XML XSD?
				continue;
			}

			if(location != null)
			{
				Element impschema = load(location);
				if(impschema != null)
				{
					addSchemaElement(impschema);
					newschemas.add(impschema);
				}
				else
				{
					debug("Error: import file error");
					return false;
				}
			}
			else
			{
				debug("Error: import without location");
				return false;
			}
		}

		debug("Include file handling done.");

		if(newschemas.size() > 0)
		{
			debug("Additional nested includes found!");
			for(int i = 0; i < newschemas.size(); i++)
			{
				Element schema = (Element)newschemas.get(i);
				if(!scanIncludes(schema))
				{
					return false;
				}
			}
		}

		return true;
	}

	private Element load(String schemafile)
	{
		DocumentBuilder builder = null;

		debug("-> loading file " + schemafile);

		// FIXME: use something like XSDCommon.createDocument()?
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		try
		{
			builder = factory.newDocumentBuilder();
		}
		catch(ParserConfigurationException ex)
		{
			debug("Error: could not create XML parser");
			debug(ex.toString());
			return null;
		}

		Document doc = null;
		try
		{
			doc = builder.parse(schemafile);
		}
		catch(SAXException ex)
		{
			debug("Error: could not parse XML document");
			debug(ex.toString());
			return null;
		}
		catch(IOException ex)
		{
			debug("Error: could not load XML document");
			debug(ex.toString());
			return null;
		}
		catch(Exception ex)
		{
			/* FIXME: GNU SAX implementation throws DomLSException */
			debug("Error: could really not parse XML document");
			debug(ex.toString());
			return null;
		}

		Element schema = doc.getDocumentElement();
		schema.normalize();

		return schema;
	}

	/**
	 * Parses missing definitions from includes and imports.
	 *
	 * If a schema was parsed in \ref PARSER_FLAT mode, no includes and
	 * imports are resolved yet. Instead, a list of them is stored inside
	 * the schema. This method parses all the stored files and then
	 * clears the list.
	 * Warning: It should not be used by anyone, as it is only called
	 * from within \ref XSDTransformer.
	 *
	 * @param xsdschema The schema object to check for pending includes
	 * @return \ref XSDSchema object, or \b null in case of loading errors
	 *
	 * @internal
	 */
	public XSDSchema parseIncludes(XSDSchema xsdschema)
	{
		boolean handled = loadIncludes(xsdschema);
		if(!handled)
		{
			debug("Error: resolving include files failed");
			return null;
		}

		debug("-- extra-schema parser begin");
		for(int i = 0; i < this.schemas.size(); i++)
		{
			Element schema = (Element)this.schemas.get(i);
			debug("-- parse additional schema");
			xsdschema = parseSchemaInternal(schema, xsdschema, false, false);
			if(xsdschema == null)
			{
				return null;
			}
		}
		debug("-- extra-schema parser end");

		xsdschema.clearIncludes();
		xsdschema.clearImports();

		// FIXME: what if those in turn included other files?

		return xsdschema;
	}

	private XSDSchema parseSchemaElements(boolean scanincludes)
	{
		//XSDSchema xsdschema = new XSDSchema();
		XSDSchema xsdschema = this.preloadxsd;
		ArrayList schemanamespaces = new ArrayList();

		debug("-- multi-schema parser begin");
		for(int i = 0; i < this.schemas.size(); i++)
		{
			Element schema = (Element)this.schemas.get(i);
			debug("-- parse additional schema");
			xsdschema = parseSchemaInternal(schema, xsdschema, scanincludes, false);
			if(xsdschema == null)
			{
				return null;
			}
			schemanamespaces.add(xsdschema.getTargetNamespace());
		}
		debug("-- multi-schema parser end");

		debug("-- multi-schema namespace resolver begin");
		for(int i = 0; i < schemanamespaces.size(); i++)
		{
			String mtns = (String)schemanamespaces.get(i);
			if(mtns != null)
			{
				ArrayList oldnamespaces = xsdschema.getImportNamespaces();
				ArrayList oldlocations = xsdschema.getImports();
				xsdschema.clearImports();

				for(int j = 0; j < oldnamespaces.size(); j++)
				{
					String namespace = (String)oldnamespaces.get(j);
					String location = (String)oldlocations.get(j);

					debug("??? " + mtns + "=" + namespace);

					if(mtns.equals(namespace))
					{
						debug("!!! resolved internally");
					}
					else
					{
						xsdschema.addImport(location, namespace);
					}
				}
			}
		}
		debug("-- multi-schema namespace resolver end");

		return transform(xsdschema);
	}

	private XSDSchema transform(XSDSchema xsdschema)
	{
		//return verified(xsdschema);
		// FIXME: split verify from object tree building etc.

		XSDTransformer transformer = new XSDTransformer();
		boolean ret = transformer.augment(xsdschema, this.level);
		if(ret == false)
		{
			debug("Error: parser failed due to failed transformation");
			debug(transformer.getDebug());
			return null;
		}
		debug(transformer.getDebug());

		return xsdschema;
	}

	// if includescan is true, return list of includes/imports
	// if includesonly is true, do not parse other elements beside include/import
	private XSDSchema parseSchemaInternal(Element schema, XSDSchema xsdschema, boolean includescan, boolean includesonly)
	{
		resetLocal();

		// Check if we really have a XSD definition
		String name = schema.getLocalName();
		if(!name.equals("schema"))
		{
			debug("Error: XSD has wrong toplevel tag " + name);
			return null;
		}

		String ns_xsd = XSDCommon.NAMESPACE_XSD;
		String namespace = schema.getNamespaceURI();
		if(!ns_xsd.equals(namespace))
		{
			debug("Error: XSD toplevel tag namespace is " + namespace);
			return null;
		}

		// Extract all namespace definitions and their prefixes
		NamedNodeMap attrlist = schema.getAttributes();
		for(int i = 0; i < attrlist.getLength(); i++)
		{
			Node n = attrlist.item(i);
			Attr a = (Attr)n;

			String aname = a.getName();
			if(aname.startsWith("xmlns:"))
			{
				aname = aname.substring(6);
				this.namespaces.put(aname, a.getValue());
			}
			else if(aname.equals("xmlns"))
			{
				this.namespaces.put("", a.getValue());
			}
			else if(aname.equals("targetNamespace"))
			{
				this.tns = a.getValue();
				xsdschema.setTargetNamespace(this.tns);
			}
			else if(aname.equals("attributeFormDefault"))
			{
				String afd = a.getValue();
				if(afd.equals("qualified")) this.attributeformdefault = true;
			}
			else if(aname.equals("elementFormDefault"))
			{
				String efd = a.getValue();
				if(efd.equals("qualified")) this.elementformdefault = true;
			}
			else
			{
				// we're not interested in others
			}
		}

		// Find all global XML elements representing schema information
		NodeList list = schema.getChildNodes();
		for(int i = 0; i < list.getLength(); i++)
		{
			Node node = list.item(i);
			if(node.getNodeType() != Node.ELEMENT_NODE)
			{
				continue;
			}
			name = node.getLocalName();

			debug("node: " + name);

			if(name.equals("element"))
			{
				if(!includesonly)
				{
					XSDElement xsdelement = parseXSDElement((Element)node);
					xsdschema.addElement(xsdelement);
				}
			}
			else if(name.equals("attribute"))
			{
				if(!includesonly)
				{
					XSDAttribute xsdattribute = parseXSDAttribute((Element)node);
					xsdschema.addAttribute(xsdattribute);
				}
			}
			else if(name.equals("complexType"))
			{
				if(!includesonly)
				{
					XSDType xsdtype = parseXSDComplexType((Element)node);
					xsdschema.addType(xsdtype);
				}
			}
			else if(name.equals("simpleType"))
			{
				if(!includesonly)
				{
					XSDType xsdtype = parseXSDSimpleType((Element)node);
					xsdschema.addType(xsdtype);
				}
			}
			else if(name.equals("group"))
			{
				if(!includesonly)
				{
					XSDSequence xsdgroup = parseXSDGroup((Element)node);
					xsdschema.addGroup(xsdgroup);
				}
			}
			else if(name.equals("attributeGroup"))
			{
				if(!includesonly)
				{
					XSDSequence xsdgroup = parseXSDAttributeGroup((Element)node);
					xsdschema.addGroup(xsdgroup);
				}
			}
			else if(name.equals("include"))
			{
				if(includescan)
				{
					// FIXME: check if we included it already?
					parseXSDInclude((Element)node, false, xsdschema);
				}
			}
			else if(name.equals("import"))
			{
				if(includescan)
				{
					// FIXME: check if we included it already?
				       	parseXSDInclude((Element)node, true, xsdschema);
				}
			}
			else if(name.equals("annotation"))
			{
				XSDAnnotation xsdann = parseXSDAnnotation((Element)node);
				xsdschema.setAnnotation(xsdann);
			}
			else
			{
				debug("Warning: unknown toplevel tag " + name);
			}
		}

		return xsdschema;
	}

	/**
	 * Parses an XML Schema element and creates an \ref XSDSchema object.
	 *
	 * This method is used to parse XSD information already present as
	 * an XML instance. The method \ref parseSchemaFile would be used for
	 * standalone XSD files.
	 *
	 * If previously added schema fragments should be considered, this
	 * method should be called with a \b null parameter. Otherwise,
	 * they would be discarded.
	 *
	 * @param schema The XML element referring to XML Schema data
	 * @param level Schema access level, e.g. \ref PARSER_FLAT
	 * @return \ref XSDSchema object, or \b null in case of invalid data
	 */
	public XSDSchema parseSchemaElement(Element schema, int level)
	{
		debug("Parser level: " + level);

		if(schema != null)
		{
			reset();
		}

		if((level < PARSER_FLAT) || (level > PARSER_MERGED))
		{
			debug("Error: invalid level");
			return null;
		}

		this.level = level;
		boolean scanincludes;

		if(schema != null)
		{
			if(this.level != PARSER_FLAT)
			{
				boolean ret = scanIncludes(schema);
				if(!ret)
				{
					debug("Error: include file loading aborted");
					return null;
				}
				scanincludes = false;
			}
			else
			{
				scanincludes = true;
			}
		}
		else
		{
			scanincludes = true;
		}

		if(this.preloadfiles.size() > 0)
		{
			debug("Preloading files...");
			for(int i = 0; i < this.preloadfiles.size(); i++)
			{
				String xsdfile = (String)this.preloadfiles.get(i);
				Element preschema = load(xsdfile);
				if(preschema != null)
				{
					this.preloadschemas.add(preschema);
					this.schemas.add(preschema);
				}
				else
				{
					debug("Error: preloading failed");
					return null;
				}
			}
			this.preloadfiles.clear();
		}

		if(this.schemas.size() == 0)
		{
			//XSDSchema xsdschema = new XSDSchema();
			XSDSchema xsdschema = this.preloadxsd;

			if(schema != null)
			{
				xsdschema = parseSchemaInternal(schema, xsdschema, scanincludes, false);
				return transform(xsdschema);
			}
			else
			{
				return xsdschema;
			}
		}
		else
		{
			debug("Force multi-schema parser!");

			if(schema != null)
			{
				addSchemaElement(schema);
				return parseSchemaElements(false);
			}
			else
			{
				return parseSchemaElements(true);
			}
		}
	}

	private void reset()
	{
		this.schemas.clear();

		for(int i = 0; i < this.preloadschemas.size(); i++)
		{
			Element xsdschema = (Element)this.preloadschemas.get(i);
			this.schemas.add(xsdschema);
		}
	}

	private void resetLocal()
	{
		this.tns = null;
		this.attributeformdefault = false;
		this.elementformdefault = false;
	}

	private String prefixPart(String s)
	{
		String[] n = s.split(":", -1);
		if(n.length > 1)
		{
			return n[0];
		}
		return null;
	}

	private String localPart(String s)
	{
		String[] n = s.split(":", -1);
		if(n.length > 0)
		{
			return n[n.length - 1];
		}
		return s;
	}

	private String rootnamespace(String prefix)
	{
		String uri = (String)this.namespaces.get(prefix);
		return uri;
	}

	private QName qname(String typestr, boolean use_tns)
	{
		if(typestr == null)
		{
			return null;
		}

		String typeprefix = prefixPart(typestr);
		String typelocal = localPart(typestr);
		String typens = rootnamespace(typeprefix);
		if((typens == null) && (use_tns))
		{
			typens = this.tns;
		}
		if((typeprefix == null) && (!use_tns))
		{
			/*String xmlns = rootnamespace("");
			String ns_xsd = XSDCommon.NAMESPACE_XSD;
			if((xmlns != null) && (xmlns.equals(ns_xsd)))
			{
				typens = ns_xsd;
			}*/
			typens = rootnamespace("");
		}

		if(typelocal.length() == 0)
		{
			return null;
		}

		QName qtype = new QName(typens, typelocal);

		// FIXME: if no namespace found for prefix, should log an error?
		return qtype;
	}

	private XSDElement parseXSDElement(Element el)
	{
		XSDElement xsdelement = new XSDElement();
		debug("Found element!");

		String name = el.getAttribute("name");
		if(name.length() > 0)
		{
			xsdelement.setName(qname(name, true));
			debug("+ element name " + name);
		}

		String ref = el.getAttribute("ref");
		if(ref.length() > 0)
		{
			xsdelement.setRef(qname(ref, false));
			debug("+ element reference " + ref);
		}

		String form = el.getAttribute("form");
		if(form.length() > 0)
		{
			if(form.equals("qualified")) xsdelement.setQualified(true);
		}
		else
		{
			xsdelement.setQualified(this.elementformdefault);
		}

		String nillable = el.getAttribute("nillable");
		if(nillable.length() > 0)
		{
			if(nillable.equals("true")) xsdelement.setNillable(true);
		}

		String typestr = el.getAttribute("type");
		if(typestr.length() > 0)
		{
			xsdelement.setTypeRef(qname(typestr, false));
		}
		else
		{
			// complex/simple inline type or anytype
		}

		String min = el.getAttribute("minOccurs");
		String max = el.getAttribute("maxOccurs");

		if(min.length() > 0)
		{
			Integer imin = new Integer(min);
			xsdelement.setMinOccurs(imin.intValue());
		}

		if(max.length() > 0)
		{
			if(max.equals("unbounded"))
			{
				xsdelement.setMaxOccurs(XSDElement.OCCURS_UNBOUNDED);
			}
			else
			{
				Integer imax = new Integer(max);
				xsdelement.setMaxOccurs(imax.intValue());
			}
		}

		String defaultvalue = el.getAttribute("default");
		if(defaultvalue.length() > 0)
		{
			xsdelement.setDefaultValue(defaultvalue);
			debug("+ element default value " + defaultvalue);
		}

		String fixedvalue = el.getAttribute("fixed");
		if(fixedvalue.length() > 0)
		{
			xsdelement.setFixedValue(fixedvalue);
			debug("+ element fixed value " + fixedvalue);
		}

		// Find out anonymous (inline) complex or simple types
		NodeList list = el.getChildNodes();
		for(int i = 0; i < list.getLength(); i++)
		{
			Node node = list.item(i);
			if(node.getNodeType() != Node.ELEMENT_NODE)
			{
				continue;
			}
			name = node.getLocalName();

			if(name.equals("complexType"))
			{
				XSDType xsdtype = parseXSDComplexType((Element)node);
				// TODO: double-check so only one type is declared at most?
				xsdelement.setType(xsdtype);
				xsdtype.setParentElement(xsdelement);
				debug("+ element type (inline) " + xsdtype.getName());
			}
			else if(name.equals("simpleType"))
			{
				XSDType xsdtype = parseXSDSimpleType((Element)node);
				// TODO: double-check so only one type is declared at most?
				xsdelement.setType(xsdtype);
				xsdtype.setParentElement(xsdelement);
				debug("+ element type (inline) " + xsdtype.getName());
			}
			else if(name.equals("annotation"))
			{
				XSDAnnotation xsdann = parseXSDAnnotation((Element)node);
				xsdelement.setAnnotation(xsdann);
			}
			else if(name.equals("unique"))
			{
				XSDUnique xsduniq = parseXSDUnique((Element)node);
				xsdelement.addUniqueConstraint(xsduniq);
			}
			else
			{
				debug("Warning: unknown element tag " + name);
			}
		}

		debug("Found element: done");

		return xsdelement;
	}

	private XSDUnique parseXSDUnique(Element el)
	{
		XSDUnique xsduniq = new XSDUnique();

		String uniqname = el.getAttribute("name");
		if(uniqname.length() > 0)
		{
			xsduniq.setName(uniqname);
		}

		NodeList list = el.getChildNodes();

		for(int i = 0; i < list.getLength(); i++)
		{
			Node node = list.item(i);
			if(node.getNodeType() != Node.ELEMENT_NODE)
			{
				continue;
			}
			String name = node.getLocalName();

			if(name.equals("selector"))
			{
				Element selector = (Element)node;
				xsduniq.setSelector(selector.getAttribute("xpath"));
			}
			else if(name.equals("field"))
			{
				Element field = (Element)node;
				xsduniq.setField(field.getAttribute("xpath"));
			}
			else
			{
				debug("Warning: unknown unique tag " + name);
			}
		}

		return xsduniq;
	}

	private XSDRestriction parseXSDRestriction(Element restrictions)
	{
		debug("(#restrictions#)");

		XSDRestriction xsdres = new XSDRestriction();

		NodeList list = restrictions.getChildNodes();

		for(int i = 0; i < list.getLength(); i++)
		{
			Node node = list.item(i);
			if(node.getNodeType() != Node.ELEMENT_NODE)
			{
				continue;
			}
			String name = node.getLocalName();

			Element restriction = (Element)node;
			String value = restriction.getAttribute("value");

			// FIXME: handle simple type here

			if(name.equals("annotation"))
			{
				XSDAnnotation xsdann = parseXSDAnnotation(restriction);
				xsdres.setAnnotation(xsdann);
			}
			else if(name.equals("minInclusive"))
			{
				debug("min-inclusive: " + value);
				BigInteger min = new BigInteger(value);
				xsdres.setMinInclusive(min);
			}
			else if(name.equals("maxInclusive"))
			{
				debug("max-inclusive: " + value);
				BigInteger max = new BigInteger(value);
				xsdres.setMaxInclusive(max);
			}
			else if(name.equals("minExclusive"))
			{
				debug("min-exclusive: " + value);
				BigInteger min = new BigInteger(value);
				xsdres.setMinExclusive(min);
			}
			else if(name.equals("maxExclusive"))
			{
				debug("max-exclusive: " + value);
				BigInteger max = new BigInteger(value);
				xsdres.setMaxExclusive(max);
			}
			else if(name.equals("totalDigits"))
			{
				debug("total-digits: " + value);
				BigInteger digits = new BigInteger(value);
				xsdres.setTotalDigits(digits);
			}
			else if(name.equals("fractionDigits"))
			{
				debug("fractionDigits: " + value);
				BigInteger digits = new BigInteger(value);
				xsdres.setFractionDigits(digits);
			}
			else if(name.equals("pattern"))
			{
				debug("pattern: " + value);
				xsdres.setPattern(value);
			}
			else if(name.equals("enumeration"))
			{
				debug("enumeration: " + value);
				xsdres.addEnumeration(value);
			}
			else if(name.equals("length"))
			{
				debug("length: " + value);
				BigInteger length = new BigInteger(value);
				xsdres.setLength(length);
			}
			else if(name.equals("minLength"))
			{
				debug("minLength: " + value);
				BigInteger length = new BigInteger(value);
				xsdres.setMinLength(length);
			}
			else if(name.equals("maxLength"))
			{
				debug("maxLength: " + value);
				BigInteger length = new BigInteger(value);
				xsdres.setMaxLength(length);
			}
			else if(name.equals("whiteSpace"))
			{
				debug("whiteSpace: " + value);
				int policy = 0;
				if(value.equals("collapse"))
					policy = XSDRestriction.WHITESPACE_COLLAPSE;
				else if(value.equals("collate"))
					policy = XSDRestriction.WHITESPACE_COLLATE;
				else if(value.equals("replace"))
					policy = XSDRestriction.WHITESPACE_REPLACE;
				else if(value.equals("preserve"))
					policy = XSDRestriction.WHITESPACE_PRESERVE;

				if(policy != 0)
				{
					xsdres.setWhiteSpace(policy);
				}
			}
			else
			{
				debug("Warning: unknown restriction tag " + name);
			}
		}

		debug("(#finish restrictions#)");

		return xsdres;
	}

	private XSDAnnotation parseXSDAnnotation(Element el)
	{
		XSDAnnotation xsdann = new XSDAnnotation();

		NodeList list = el.getChildNodes();

		for(int i = 0; i < list.getLength(); i++)
		{
			Node node = list.item(i);
			if(node.getNodeType() != Node.ELEMENT_NODE)
			{
				continue;
			}
			String name = node.getLocalName();

			if(name.equals("documentation"))
			{
				Element documentation = (Element)node;

				String urlstr = documentation.getAttribute("source");
				String docstr = textvalue(node);
				if(urlstr.length() > 0)
				{
					xsdann.setDocumentationURL(urlstr);
				}
				if(docstr.length() > 0)
				{
					xsdann.setDocumentation(docstr);
				}
			}
			else if(name.equals("appinfo"))
			{
				// ignore
			}
			else
			{
				debug("Warning: unknown annotation tag " + name);
			}
		}

		return xsdann;
	}

	// Returns list of child elements of a certain namespace
	// If filter is null, all elements are added, else only those which match filter
	private ArrayList elementlist(Element parent, String filter)
	{
		if(parent == null)
		{
			return null;
		}

		String ns_xsd = XSDCommon.NAMESPACE_XSD;
		ArrayList list = new ArrayList();
		NodeList nodelist = parent.getChildNodes();

		for(int i = 0; i < nodelist.getLength(); i++)
		{
			Node node = nodelist.item(i);
			if(node.getNodeType() != Node.ELEMENT_NODE)
			{
				continue;
			}

			String namespace = node.getNamespaceURI();
			if(!ns_xsd.equals(namespace))
			{
				continue;
			}

			String name = node.getLocalName();
			if((filter == null) || (name.equals(filter)))
			{
				list.add((Element)node);
			}
		}

		return list;
	}

	private XSDType parseXSDSimpleType(Element simpletype)
	{
		XSDType xsdtype = new XSDType();
		debug("Found derived simple type!");

		String name = simpletype.getAttribute("name");
		xsdtype.setName(qname(name, true));
		debug("+ type name " + xsdtype.getName());

		ArrayList list = elementlist(simpletype, null);

		for(int i = 0; i < list.size(); i++)
		{
			Node node = (Element)list.get(i);
			name = node.getLocalName();

			if(name.equals("restriction"))
			{
				debug("+ restriction...");

				Element restriction = (Element)node;
				String base = restriction.getAttribute("base");
				if(base.length() > 0)
				{
					xsdtype.setBaseRef(qname(base, false));
					debug("+ base type " + base);
				}
				else
				{
					ArrayList list2 = elementlist((Element)node, "simpleType");
					if(list2.size() == 1)
					{
						Element element = (Element)list2.get(0);
						XSDType t = parseXSDSimpleType(element);
						xsdtype.setBaseType(t);
						debug("+ restriction base type (inline)");
					}
					else if(list2.size() == 0)
					{
						debug("Error: restriction base type missing");
						return null;
					}
					else
					{
						debug("Error: more than one restriction base type");
						return null;
					}
				}

				XSDRestriction xsdres = parseXSDRestriction(restriction);
				xsdtype.setRestriction(xsdres);
			}
			else if(name == "list")
			{
				debug("+ list...");

				Element listtype = (Element)node;
				String itemtype = listtype.getAttribute("itemType");

				if(itemtype.length() > 0)
				{
					debug("+ list item type " + itemtype);

					xsdtype.setType(XSDType.TYPE_LIST);
					xsdtype.setBaseRef(qname(itemtype, false));
				}
				else
				{
					ArrayList list2 = elementlist((Element)node, "simpleType");
					if(list2.size() == 1)
					{
						Element element = (Element)list2.get(0);
						XSDType t = parseXSDSimpleType(element);
						xsdtype.setBaseType(t);
						debug("+ list item type (inline)");
					}
					else if(list2.size() == 0)
					{
						debug("Error: list item type missing");
						return null;
					}
					else
					{
						debug("Error: more than one item type");
						return null;
					}
				}
			}
			else if(name == "union")
			{
				debug("+ union...");

				Element uniontype = (Element)node;
				String membertypes = uniontype.getAttribute("memberTypes");

				if(membertypes.length() > 0)
				{
					debug("+ union member types " + membertypes);
					xsdtype.setType(XSDType.TYPE_UNION);

					String[] members = membertypes.split(" ");
					for(int j = 0; j < members.length; j++)
					{
						String typestr = members[j];
						xsdtype.addMemberRef(qname(typestr, false));
					}
				}
				else
				{
					ArrayList list2 = elementlist((Element)node, "simpleType");
					for(int j = 0; j < list2.size(); j++)
					{
						Element element = (Element)list2.get(j);
						XSDType t = parseXSDSimpleType(element);
						xsdtype.addMemberType(t);
						debug("+ union member type (inline)");
					}

					if(list2.size() == 0)
					{
						debug("Error: union member types missing");
						return null;
					}
				}
			}
			else if(name.equals("annotation"))
			{
				XSDAnnotation xsdann = parseXSDAnnotation((Element)node);
				xsdtype.setAnnotation(xsdann);
			}
			else
			{
				debug("Warning: unknown simple type tag " + name);
			}
		}

		debug("Found derived simple type: done");

		return xsdtype;
	}

	private XSDType parseXSDComplexType(Element el)
	{
		XSDType xsdtype = new XSDType();
		xsdtype.setType(XSDType.TYPE_COMPLEX);
		debug("Found complex type!");

		String name = el.getAttribute("name");
		xsdtype.setName(qname(name, true));
		debug("+ type name " + xsdtype.getName());

		String mixed = el.getAttribute("mixed");
		if(mixed.length() > 0)
		{
			if(mixed.equals("true")) xsdtype.setMixed(true);
		}

		NodeList list = el.getChildNodes();

		for(int i = 0; i < list.getLength(); i++)
		{
			Node node = list.item(i);
			if(node.getNodeType() != Node.ELEMENT_NODE)
			{
				continue;
			}
			name = node.getLocalName();

			if(name.equals("sequence"))
			{
				XSDSequence seq = parseXSDSequence((Element)node);
				xsdtype.setSequence(seq);
				seq.setParentType(xsdtype);
				// TODO: sanity checks like at-most-once here too?
			}
			else if(name.equals("all"))
			{
				XSDSequence seq = parseXSDSequence((Element)node);
				seq.setAll(true);
				xsdtype.setSequence(seq);
				seq.setParentType(xsdtype);
			}
			else if(name.equals("attribute"))
			{
				XSDAttribute xsdatt = parseXSDAttribute((Element)node);
				xsdtype.addAttribute(xsdatt);
			}
			else if(name.equals("anyAttribute"))
			{
				// ... FIXME
			}
			else if(name.equals("attributeGroup"))
			{
				XSDSequence group = parseXSDAttributeGroup((Element)node);
				group.setParentType(xsdtype);

				XSDType pseudotype = new XSDType();
				pseudotype.setType(XSDType.TYPE_ATTRIBUTEGROUP);
				//pseudotype.setAttributeGroup(group);
				pseudotype.setSequence(group);

				XSDAttribute pseudo = new XSDAttribute();
				pseudo.setType(pseudotype);
				xsdtype.addAttribute(pseudo);
				//allattributes.add(pseudo);
			}
			else if(name.equals("simpleContent"))
			{
				parsesimplecontent(xsdtype, (Element)node);
			}
			else if(name.equals("complexContent"))
			{
				xsdtype = parsecomplexcontent(xsdtype, (Element)node);
			}
			else if(name.equals("annotation"))
			{
				XSDAnnotation xsdann = parseXSDAnnotation((Element)node);
				xsdtype.setAnnotation(xsdann);
			}
			else
			{
				debug("Warning: unknown complex type tag " + name);
			}
		}

		debug("Found complex type: done");

		return xsdtype;
	}

	private XSDSequence parseXSDSequence(Element el)
	{
		XSDSequence xsdsequence = new XSDSequence();
		debug("Found sequence!");

		NodeList list = el.getChildNodes();

		for(int i = 0; i < list.getLength(); i++)
		{
			Node node = list.item(i);
			if(node.getNodeType() != Node.ELEMENT_NODE)
			{
				continue;
			}
			String name = node.getLocalName();

			if(name.equals("element"))
			{
				XSDElement element = parseXSDElement((Element)node);
				element.setParentSequence(xsdsequence);
				xsdsequence.addElement(element);
			}
			else if(name.equals("choice"))
			{
				XSDChoice xsdchoice = parseXSDChoice((Element)node);

				XSDType pseudotype = new XSDType();
				pseudotype.setType(XSDType.TYPE_CHOICE);
				pseudotype.setChoice(xsdchoice);

				XSDElement pseudo = new XSDElement();
				pseudo.setParentSequence(xsdsequence);
				pseudo.setType(pseudotype);
				xsdsequence.addElement(pseudo);
			}
			else if(name.equals("group"))
			{
				XSDSequence xsdgroup = parseXSDGroup((Element)node);

				XSDType pseudotype = new XSDType();
				pseudotype.setType(XSDType.TYPE_GROUP);
				pseudotype.setSequence(xsdgroup);

				XSDElement pseudo = new XSDElement();
				pseudo.setParentSequence(xsdsequence);
				pseudo.setType(pseudotype);
				xsdsequence.addElement(pseudo);
			}
			else if(name.equals("any"))
			{
				// ... FIXME!
			}
			else if(name.equals("annotation"))
			{
				XSDAnnotation xsdann = parseXSDAnnotation((Element)node);
				xsdsequence.setAnnotation(xsdann);
			}
			else
			{
				debug("Warning: unknown sequence tag " + name);
			}
		}

		debug("Found sequence: done");

		return xsdsequence;
	}

	private XSDAttribute parseXSDAttribute(Element el)
	{
		XSDAttribute xsdattribute = new XSDAttribute();
		debug("Found attribute!");

		String name = el.getAttribute("name");
		if(name.length() > 0)
		{
			xsdattribute.setName(qname(name, true));
			debug("+ attribute name " + name);
		}

		String ref = el.getAttribute("ref");
		if(ref.length() > 0)
		{
			xsdattribute.setRef(qname(ref, false));
			debug("+ attribute reference " + ref);
		}

		String form = el.getAttribute("form");
		if(form.length() > 0)
		{
			if(form.equals("qualified")) xsdattribute.setQualified(true);
		}
		else
		{
			xsdattribute.setQualified(this.attributeformdefault);
		}

		String typestr = el.getAttribute("type");
		if(typestr.length() > 0)
		{
			xsdattribute.setTypeRef(qname(typestr, false));
		}
		else
		{
			// inline simple type
		}

		String usestr = el.getAttribute("use");
		if(usestr.length() > 0)
		{
			int use = XSDAttribute.USE_REQUIRED;
			if(usestr.equals("optional"))
			{
				use = XSDAttribute.USE_OPTIONAL;
			}
			else if(usestr.equals("prohibited"))
			{
				use = XSDAttribute.USE_PROHIBITED;
			}
			xsdattribute.setUse(use);
			debug("+ attribute usage policy " + usestr);
		}

		String defaultvalue = el.getAttribute("default");
		if(defaultvalue.length() > 0)
		{
			xsdattribute.setDefaultValue(defaultvalue);
			debug("+ attribute default value " + defaultvalue);
		}

		String fixedvalue = el.getAttribute("fixed");
		if(fixedvalue.length() > 0)
		{
			xsdattribute.setFixedValue(fixedvalue);
			debug("+ attribute fixed value " + fixedvalue);
		}

		NodeList list = el.getChildNodes();

		for(int i = 0; i < list.getLength(); i++)
		{
			Node node = list.item(i);
			if(node.getNodeType() != Node.ELEMENT_NODE)
			{
				continue;
			}
			name = node.getLocalName();

			if(name.equals("simpleType"))
			{
				XSDType xsdtype = parseXSDSimpleType((Element)node);
				xsdattribute.setType(xsdtype);
				//xsdtype.setParentElement(xsdelement);
				// FIXME: parent attributes in object tree?
				debug("+ attribute type (inline) " + xsdtype.getName());
			}
			else if(name.equals("annotation"))
			{
				XSDAnnotation xsdann = parseXSDAnnotation((Element)node);
				xsdattribute.setAnnotation(xsdann);
			}
			else
			{
				// FIXME: restrictions/extensions etc.
				debug("Warning: unknown attribute tag " + name);
			}
		}

		debug("Found attribute: done");

		return xsdattribute;
	}

	private XSDChoice parseXSDChoice(Element el)
	{
		XSDChoice xsdchoice = new XSDChoice();
		debug("Found choice!");

		NodeList list = el.getChildNodes();

		for(int i = 0; i < list.getLength(); i++)
		{
			Node node = list.item(i);
			if(node.getNodeType() != Node.ELEMENT_NODE)
			{
				continue;
			}
			String name = node.getLocalName();

			if(name.equals("element"))
			{
				XSDElement xsdelement = parseXSDElement((Element)node);
				xsdchoice.addElement(xsdelement);
			}
			else if(name.equals("group"))
			{
				XSDSequence xsdgroup = parseXSDGroup((Element)node);
				xsdchoice.addGroup(xsdgroup);
			}
			else
			{
				debug("Warning: unknown choice tag " + name);
			}
		}

		debug("Found choice: done");

		return xsdchoice;
	}

	private XSDSequence parseXSDGroup(Element el)
	{
		XSDSequence xsdgroup = new XSDSequence();
		debug("Found group!");

		String name = el.getAttribute("name");
		if(name.length() > 0)
		{
			xsdgroup.setGroupName(qname(name, true));
			debug("+ group name " + name);
		}

		String ref = el.getAttribute("ref");
		if(ref.length() > 0)
		{
			xsdgroup.setGroupRef(qname(ref, false));
			debug("+ group reference " + ref);
		}

		NodeList list = el.getChildNodes();

		for(int i = 0; i < list.getLength(); i++)
		{
			Node node = list.item(i);
			if(node.getNodeType() != Node.ELEMENT_NODE)
			{
				continue;
			}
			name = node.getLocalName();

			// FIXME: handle choice

			if(name.equals("sequence"))
			{
				XSDSequence xsdgroup2 = parseXSDSequence((Element)node);
				xsdgroup2.setGroupName(xsdgroup.getGroupName());
				xsdgroup2.setGroupRef(xsdgroup.getGroupRef());
				xsdgroup = xsdgroup2;
			}
			else if(name.equals("annotation"))
			{
				XSDAnnotation xsdann = parseXSDAnnotation((Element)node);
				xsdgroup.setAnnotation(xsdann);
			}
			else
			{
				debug("Warning: unknown group tag " + name);
			}
		}

		debug("Found group: done");

		return xsdgroup;
	}

	private XSDSequence parseXSDAttributeGroup(Element el)
	{
		XSDSequence xsdgroup = new XSDSequence();
		xsdgroup.setGroupAttribute(true);
		debug("Found attribute group!");

		String name = el.getAttribute("name");
		if(name.length() > 0)
		{
			xsdgroup.setGroupName(qname(name, true));
			debug("+ group name " + name);
		}

		String ref = el.getAttribute("ref");
		if(ref.length() > 0)
		{
			xsdgroup.setGroupRef(qname(ref, false));
			debug("+ group reference " + ref);
		}

		NodeList list = el.getChildNodes();

		for(int i = 0; i < list.getLength(); i++)
		{
			Node node = list.item(i);
			if(node.getNodeType() != Node.ELEMENT_NODE)
			{
				continue;
			}
			name = node.getLocalName();

			if(name.equals("attribute"))
			{
				XSDAttribute xsdatt = parseXSDAttribute((Element)node);
				xsdgroup.addAttribute(xsdatt);
			}
			else if(name.equals("attributeGroup"))
			{
				XSDSequence group = parseXSDAttributeGroup((Element)node);
				//group.setParentType(xsdtype);

				XSDType pseudotype = new XSDType();
				pseudotype.setType(XSDType.TYPE_ATTRIBUTEGROUP);
				pseudotype.setSequence(group);

				XSDAttribute pseudo = new XSDAttribute();
				pseudo.setType(pseudotype);
				xsdgroup.addAttribute(pseudo);
			}
			else
			{
				debug("Warning: unknown attribute group tag " + name);
			}
		}

		debug("Found attribute group: done");

		return xsdgroup;
	}

	private boolean parseXSDInclude(Element include, boolean isimport, XSDSchema xsdschema)
	{
		// FIXME: checks from parseSchemaInternal already here or in both?

		String location = include.getAttribute("schemaLocation");
		String namespace = include.getAttribute("namespace");

		if(location.length() > 0)
		{
			if(namespace.length() == 0)
			{
				namespace = null;
			}

			if(isimport)
			{
				xsdschema.addImport(location, namespace);
			}
			else
			{
				xsdschema.addInclude(location);
			}

			return true;
		}
		else
		{
			if(isimport)
			{
				// FIXME: this is legal but XSD4J cannot handle it
				// FIXME: check for bootstrapped schemas
				//        and for such in multi-part schemas (e.g. in WSDL files)
				if(namespace.length() > 0)
				{
					debug("Warning: import statement without location");
					xsdschema.addImport(null, namespace);
					return true;
				}
				else
				{
					debug("Error: import statement without location/namespace");
					return false;
				}
			}
			else
			{
				debug("Error: include statement without location");
				return false;
			}
		}
	}

	private XSDType parsecomplexcontent(XSDType xsdtype, Element el)
	{
		debug("Found complex content!");

		NodeList list = el.getChildNodes();

		for(int i = 0; i < list.getLength(); i++)
		{
			Node node = list.item(i);
			if(node.getNodeType() != Node.ELEMENT_NODE)
			{
				continue;
			}
			String name = node.getLocalName();

			if(name.equals("extension"))
			{
				parseextension(xsdtype, (Element)node);
			}
			else if(name.equals("restriction"))
			{
				debug("+ restriction");

				QName savename = xsdtype.getName();
				xsdtype = parseXSDComplexType((Element)node);
				xsdtype.setName(savename);

				Element restriction = (Element)node;
				String base = restriction.getAttribute("base");
				if(base.length() > 0)
				{
					xsdtype.setBaseRef(qname(base, false));
					debug("+ base type " + base);
				}

				xsdtype.setRestricted(true);
			}
			else
			{
				debug("Warning: unknown complex content tag " + name);
			}
		}

		debug("Found complex content: done");

		return xsdtype;
	}

	private void parsesimplecontent(XSDType xsdtype, Element el)
	{
		debug("Found simple content!");

		NodeList list = el.getChildNodes();

		for(int i = 0; i < list.getLength(); i++)
		{
			Node node = list.item(i);
			if(node.getNodeType() != Node.ELEMENT_NODE)
			{
				continue;
			}
			String name = node.getLocalName();

			if(name.equals("extension"))
			{
				parseextension(xsdtype, (Element)node);
			}
			else
			{
				debug("Warning: unknown simple content tag " + name);
			}
		}

		debug("Found simple content: done");
	}

	private void parseextension(XSDType xsdtype, Element el)
	{
		debug("Found extension!");

		String base = el.getAttribute("base");
		if(base.length() > 0)
		{
			xsdtype.setBaseRef(qname(base, false));
			debug("+ base type " + base);
		}

		NodeList list = el.getChildNodes();

		for(int i = 0; i < list.getLength(); i++)
		{
			Node node = list.item(i);
			if(node.getNodeType() != Node.ELEMENT_NODE)
			{
				continue;
			}
			String name = node.getLocalName();

			// FIXME: handle groups

			if(name.equals("attribute"))
			{
				XSDAttribute xsdatt = parseXSDAttribute((Element)node);
				xsdtype.addAttribute(xsdatt);
			}
			else if(name.equals("attributeGroup"))
			{
				XSDSequence group = parseXSDAttributeGroup((Element)node);
				group.setParentType(xsdtype);

				XSDType pseudotype = new XSDType();
				pseudotype.setType(XSDType.TYPE_ATTRIBUTEGROUP);
				pseudotype.setSequence(group);

				XSDAttribute pseudo = new XSDAttribute();
				pseudo.setType(pseudotype);
				xsdtype.addAttribute(pseudo);
			}
			else if(name.equals("sequence"))
			{
				// only for complexcontent, not for simplecontent!
				XSDSequence seq = parseXSDSequence((Element)node);
				xsdtype.setSequence(seq);
				seq.setParentType(xsdtype);
			}
			else
			{
				debug("Warning: unknown extension tag " + name);
			}
		}

		debug("Found extension: done");
	}
}

