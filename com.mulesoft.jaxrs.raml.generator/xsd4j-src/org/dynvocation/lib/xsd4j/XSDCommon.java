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

import javax.xml.parsers.*;

import org.w3c.dom.*;

/**
 * \brief Common class for the other XSD4J classes.
 *
 * This class handles conversion of XML Schema simple type strings to internal
 * type codes and vice-versa. It also eases the creation of new XML DOM
 * Document objects.
 *
 * @author Josef Spillner <spillner@rn.inf.tu-dresden.de>
 */
public class XSDCommon
{
	/** Symbolic value which refers to the XML Schema namespace. */
	public static final String NAMESPACE_XSD = "http://www.w3.org/2001/XMLSchema";
	/** Symbolic value which refers to the XML Schema Instance namespace. */
	//public static final String NAMESPACE_XSI = "http://www.w3.org/2001/XMLSchema-instance";
	public static final String NAMESPACE_XSI = "http://www.w3.org/1999/XMLSchema-instance";
	/** Symbolic value which refers to the XML namespace. */
	public static final String NAMESPACE_XML = "http://www.w3.org/XML/1998/namespace";
	/** Symbolic value which refers to the XML Namespace namespace. */
	public static final String NAMESPACE_XMLNS = "http://www.w3.org/2000/xmlns/";

	/**
	 * Converts an XML Schema simple type name to a type value.
	 *
	 * The type name is converted to one of the type symbols present in
	 * the \ref XSDType class.
	 *
	 * @param typelocal Name of the XML Schema type
	 * @return Type value for use with XSDType
	 * @see xsdtypeToName
	 */
	public static int nameToType(String typelocal)
	{
		int typetype = XSDType.TYPE_INVALID;

		if(typelocal.equals("annotated"))
		{
			// FIXME: error in XSD spec?!
			return XSDType.TYPE_ANY_TYPE;
		}

		// FIXME: treat as internal until bootstrapping works
		// FIXME: but reject later as they're only utility types!
		//HashMap internal = new HashMap();
		//internal.put("derivationControl", "");
		//internal.put("simpleDerivationSet", "");
		//internal.put("simpleType", "");
		//internal.put("topLevelSimpleType", "");
		//internal.put("localSimpleType", "");
		//internal.put("facet", "");
		//internal.put("noFixedFacet", "");
		//internal.put("numFacet", "");
		//if(internal.get(typelocal) != null)
		//{
		//	return XSDType.TYPE_ANY_TYPE;
		//}

		if(typelocal.equals("anySimpleType"))
		{
			typetype = XSDType.TYPE_ANY_SIMPLE_TYPE;
		}
		else if(typelocal.equals("string"))
		{
			typetype = XSDType.TYPE_STRING;
		}
		else if(typelocal.equals("normalizedString"))
		{
			typetype = XSDType.TYPE_NORMALIZED_STRING;
		}
		else if(typelocal.equals("token"))
		{
			typetype = XSDType.TYPE_TOKEN;
		}
		else if(typelocal.equals("base64Binary"))
		{
			typetype = XSDType.TYPE_BASE64_BINARY;
		}
		else if(typelocal.equals("hexBinary"))
		{
			typetype = XSDType.TYPE_HEX_BINARY;
		}

		else if(typelocal.equals("integer"))
		{
			typetype = XSDType.TYPE_INTEGER;
		}
		else if(typelocal.equals("positiveInteger"))
		{
			typetype = XSDType.TYPE_POSITIVE_INTEGER;
		}
		else if(typelocal.equals("negativeInteger"))
		{
			typetype = XSDType.TYPE_NEGATIVE_INTEGER;
		}
		else if(typelocal.equals("nonNegativeInteger"))
		{
			typetype = XSDType.TYPE_NON_NEGATIVE_INTEGER;
		}
		else if(typelocal.equals("nonPositiveInteger"))
		{
			typetype = XSDType.TYPE_NON_POSITIVE_INTEGER;
		}
		else if(typelocal.equals("long"))
		{
			typetype = XSDType.TYPE_LONG;
		}
		else if(typelocal.equals("unsignedLong"))
		{
			typetype = XSDType.TYPE_UNSIGNED_LONG;
		}
		else if(typelocal.equals("int"))
		{
			typetype = XSDType.TYPE_INT;
		}
		else if(typelocal.equals("unsignedInt"))
		{
			typetype = XSDType.TYPE_UNSIGNED_INT;
		}
		else if(typelocal.equals("short"))
		{
			typetype = XSDType.TYPE_SHORT;
		}
		else if(typelocal.equals("unsignedShort"))
		{
			typetype = XSDType.TYPE_UNSIGNED_SHORT;
		}
		else if(typelocal.equals("byte"))
		{
			typetype = XSDType.TYPE_BYTE;
		}
		else if(typelocal.equals("unsignedByte"))
		{
			typetype = XSDType.TYPE_UNSIGNED_BYTE;
		}

		else if(typelocal.equals("decimal"))
		{
			typetype = XSDType.TYPE_DECIMAL;
		}
		else if(typelocal.equals("float"))
		{
			typetype = XSDType.TYPE_FLOAT;
		}
		else if(typelocal.equals("double"))
		{
			typetype = XSDType.TYPE_DOUBLE;
		}

		else if(typelocal.equals("boolean"))
		{
			typetype = XSDType.TYPE_BOOLEAN;
		}

		else if(typelocal.equals("duration"))
		{
			typetype = XSDType.TYPE_DURATION;
		}
		else if(typelocal.equals("dateTime"))
		{
			typetype = XSDType.TYPE_DATE_TIME;
		}
		else if(typelocal.equals("date"))
		{
			typetype = XSDType.TYPE_DATE;
		}
		else if(typelocal.equals("time"))
		{
			typetype = XSDType.TYPE_TIME;
		}
		else if(typelocal.equals("gYear"))
		{
			typetype = XSDType.TYPE_G_YEAR;
		}
		else if(typelocal.equals("gYearMonth"))
		{
			typetype = XSDType.TYPE_G_YEAR_MONTH;
		}
		else if(typelocal.equals("gMonth"))
		{
			typetype = XSDType.TYPE_G_MONTH;
		}
		else if(typelocal.equals("gMonthDay"))
		{
			typetype = XSDType.TYPE_G_MONTH_DAY;
		}
		else if(typelocal.equals("gDay"))
		{
			typetype = XSDType.TYPE_G_DAY;
		}

		else if(typelocal.equals("Name"))
		{
			typetype = XSDType.TYPE_NAME;
		}
		else if(typelocal.equals("QName"))
		{
			typetype = XSDType.TYPE_QNAME;
		}
		else if(typelocal.equals("NCName"))
		{
			typetype = XSDType.TYPE_NCNAME;
		}
		else if(typelocal.equals("anyURI"))
		{
			typetype = XSDType.TYPE_ANY_URI;
		}
		else if(typelocal.equals("language"))
		{
			typetype = XSDType.TYPE_LANGUAGE;
		}

		else if(typelocal.equals("ID"))
		{
			typetype = XSDType.TYPE_ID;
		}
		else if(typelocal.equals("IDREF"))
		{
			typetype = XSDType.TYPE_IDREF;
		}
		else if(typelocal.equals("IDREFS"))
		{
			typetype = XSDType.TYPE_IDREFS;
		}
		else if(typelocal.equals("ENTITY"))
		{
			typetype = XSDType.TYPE_ENTITY;
		}
		else if(typelocal.equals("ENTITIES"))
		{
			typetype = XSDType.TYPE_ENTITIES;
		}
		else if(typelocal.equals("NOTATION"))
		{
			typetype = XSDType.TYPE_NOTATION;
		}
		else if(typelocal.equals("NMTOKEN"))
		{
			typetype = XSDType.TYPE_NMTOKEN;
		}
		else if(typelocal.equals("NMTOKENS"))
		{
			typetype = XSDType.TYPE_NMTOKENS;
		}

		return typetype;
	}

	/**
	 * Converts a type value to an XML Schema simple type name.
	 *
	 * The type object, which carries a type value according to the
	 * symbols in the \ref XSDType class, is converted to a name which
	 * can be used within any XML Schema document.
	 *
	 * @param xsdtype Type object describing a simple type
	 * @return Simple type name according to XML Schema
	 * @see nameToType
	 */
	public static String xsdtypeToName(XSDType xsdtype)
	{
		if(xsdtype == null) return null;

		int type = xsdtype.getType();

		if(type == XSDType.TYPE_ANY_TYPE) return "";
		if(type == XSDType.TYPE_ANY_SIMPLE_TYPE) return "xsd:anySimpleType";

		if(type == XSDType.TYPE_STRING) return "xsd:string";
		if(type == XSDType.TYPE_NORMALIZED_STRING) return "xsd:normalizedString";
		if(type == XSDType.TYPE_TOKEN) return "xsd:token";
		if(type == XSDType.TYPE_BASE64_BINARY) return "xsd:base64Binary";
		if(type == XSDType.TYPE_HEX_BINARY) return "xsd:hexBinary";
		if(type == XSDType.TYPE_INTEGER) return "xsd:integer";
		if(type == XSDType.TYPE_POSITIVE_INTEGER) return "xsd:positiveInteger";
		if(type == XSDType.TYPE_NEGATIVE_INTEGER) return "xsd:negativeInteger";
		if(type == XSDType.TYPE_NON_NEGATIVE_INTEGER) return "xsd:nonNegativeInteger";
		if(type == XSDType.TYPE_NON_POSITIVE_INTEGER) return "xsd:nonPositiveInteger";
		if(type == XSDType.TYPE_LONG) return "xsd:long";
		if(type == XSDType.TYPE_UNSIGNED_LONG) return "xsd:unsignedLong";
		if(type == XSDType.TYPE_INT) return "xsd:int";
		if(type == XSDType.TYPE_UNSIGNED_INT) return "xsd:unsignedInt";
		if(type == XSDType.TYPE_SHORT) return "xsd:short";
		if(type == XSDType.TYPE_UNSIGNED_SHORT) return "xsd:unsignedShort";
		if(type == XSDType.TYPE_BYTE) return "xsd:byte";
		if(type == XSDType.TYPE_UNSIGNED_BYTE) return "xsd:unsignedByte";

		if(type == XSDType.TYPE_DECIMAL) return "xsd:decimal";
		if(type == XSDType.TYPE_FLOAT) return "xsd:float";
		if(type == XSDType.TYPE_DOUBLE) return "xsd:double";

		if(type == XSDType.TYPE_BOOLEAN) return "xsd:boolean";

		if(type == XSDType.TYPE_DURATION) return "xsd:duration";
		if(type == XSDType.TYPE_DATE_TIME) return "xsd:dateTime";
		if(type == XSDType.TYPE_DATE) return "xsd:date";
		if(type == XSDType.TYPE_TIME) return "xsd:time";
		if(type == XSDType.TYPE_G_YEAR) return "xsd:gYear";
		if(type == XSDType.TYPE_G_YEAR_MONTH) return "xsd:gYearMonth";
		if(type == XSDType.TYPE_G_MONTH) return "xsd:gMonth";
		if(type == XSDType.TYPE_G_MONTH_DAY) return "xsd:gMonthDay";
		if(type == XSDType.TYPE_G_DAY) return "xsd:gDay";

		if(type == XSDType.TYPE_NAME) return "xsd:Name";
		if(type == XSDType.TYPE_QNAME) return "xsd:QName";
		if(type == XSDType.TYPE_NCNAME) return "xsd:NCName";
		if(type == XSDType.TYPE_ANY_URI) return "xsd:anyURI";
		if(type == XSDType.TYPE_LANGUAGE) return "xsd:language";

		if(type == XSDType.TYPE_ID) return "xsd:ID";
		if(type == XSDType.TYPE_IDREF) return "xsd:IDREF";
		if(type == XSDType.TYPE_IDREFS) return "xsd:IDREFS";
		if(type == XSDType.TYPE_ENTITY) return "xsd:ENTITY";
		if(type == XSDType.TYPE_ENTITIES) return "xsd:ENTITIES";
		if(type == XSDType.TYPE_NOTATION) return "xsd:NOTATION";
		if(type == XSDType.TYPE_NMTOKEN) return "xsd:NMTOKEN";
		if(type == XSDType.TYPE_NMTOKENS) return "xsd:NMTOKENS";

		return null;
	}

	/**
	 * Creation of a XML DOM document.
	 *
	 * This is a convenience method which returns an XML document
	 * without any danger of throwing an exception.
	 * All exceptions are converted to \b null values.
	 *
	 * @return XML DOM Document object, or \b null in the case of failure
	 */
	public static Document createDocument()
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;

		try
		{
			builder = factory.newDocumentBuilder();
		}
		catch(ParserConfigurationException e)
		{
			System.out.println("!!! DOM creation exception");
			System.out.println(e.toString());
		}

		Document doc = builder.newDocument();

		return doc;
	}

	public static boolean checkDependencies()
	{
		try
		{
			Class.forName("org.dynvocation.lib.regexinstant.RegExpConstraint");
		}
		catch(ClassNotFoundException e)
		{
			return false;
		}

		return true;
	}
}

