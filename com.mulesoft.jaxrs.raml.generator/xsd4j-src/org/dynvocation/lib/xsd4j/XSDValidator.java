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

import org.w3c.dom.*;

// FIXME: also, check for fixed vs. default consistency etc.
// FIXME: instance validation vs. schema verification!

/**
 * \brief Validator class for XML schema trees.
 *
 * Once a tree has been obtained from an \ref XSDParser object,
 * which means that it has already passed internal validation,
 * some additional validation might be wanted by the application.
 *
 * Warning: This class is not working yet.
 *
 * @author Josef Spillner <spillner@rn.inf.tu-dresden.de>
 */
public class XSDValidator extends XMLBase
{
	/**
	 * Default constructor.
	 *
	 * Returns a new validator object which may then be used to
	 * validate schema tree objects.
	 */
	public XSDValidator()
	{
	}

	/**
	 * Validates an XML instance against the whole XML Schema tree.
	 *
	 * This method takes both an XML schema and a perceived instance
	 * thereof and performs a validation.
	 *
	 * @param schema XML schema tree object
	 * @param instance XML DOM element describing an instance of the schema
	 * @return \b true if the instance matches the schema, or \b false otherwise
	 */
	public boolean validate(XSDSchema schema, Element instance)
	{
		boolean valid;

		debug("(-validator-) begin");

		XSDTransformer xsdtrans = new XSDTransformer();
		xsdtrans.augment(schema, XSDParser.PARSER_TREE);
		debug(xsdtrans.getDebug());

		valid = validate_recursive(schema, instance);

		debug("(-validator-) end");

		return valid;
	}

	private boolean validate_recursive(XSDSchema schema, Element instance)
	{
		boolean valid = true;
		boolean tmpvalid;

		String elnamespace = instance.getNamespaceURI();

		NamedNodeMap map = instance.getAttributes();
		for(int i = 0; i < map.getLength(); i++)
		{
			Attr a = (Attr)map.item(i);
			String namespace = a.getNamespaceURI();
			String name = a.getLocalName();
			if(name == null)
			{
				name = a.getName();
			}
			String tree = treepath_xml(instance) + "/@" + name;
			debug("TreePath: " + tree);

			if(namespace != null)
			{
				if(!namespace.equals(elnamespace))
				{
					debug("-> skip (different namespace)");
					continue;
				}
			}

			XSDAttribute xsdatt = treepath2attribute(schema, tree);
			if(xsdatt != null)
			{
				tmpvalid = validateAttribute(a, xsdatt);
				if(!tmpvalid) valid = false;
				if(tmpvalid)
				{
					debug("-> ok");
				}
				else
				{
					debug("-> invalid (type mismatch)");
				}
			}
			else
			{
				debug("-> invalid (not in schema)");
				valid = false;
			}
		}

		NodeList list = instance.getChildNodes();
		for(int i = 0; i < list.getLength(); i++)
		{
			Node node = list.item(i);
			if(node.getNodeType() != Node.ELEMENT_NODE)
			{
				continue;
			}

			Element el = (Element)node;
			String tree = treepath_xml(el);
			debug("TreePath: " + tree);

			XSDElement xsdelement = treepath2element(schema, tree);
			if(xsdelement != null)
			{
				if(xsdelement.getType().getType() == XSDType.TYPE_COMPLEX)
				{
					debug("-> ok (complex type)");
				}
				else
				{
					tmpvalid = validateElement(el, xsdelement);
					if(!tmpvalid) valid = false;
					if(tmpvalid)
					{
						debug("-> ok");
					}
					else
					{
						debug("-> invalid (type mismatch)");
					}
				}
			}
			else
			{
				debug("-> invalid (not in schema)");
				valid = false;
			}

			tmpvalid = validate_recursive(schema, el);
			if(!tmpvalid) valid = false;
		}

		return valid;
	}

	private boolean validateAttribute(Attr el, XSDAttribute xsdatt)
	{
		// FIXME: do something here
		return true;
	}

	private boolean validateElement(Element el, XSDElement xsdelement)
	{
		String content = textvalue(el);
		debug("## Content: " + content);

		XSDType xsdtype = xsdelement.getType();
		int t = xsdtype.getType();

		if(t == XSDType.TYPE_BOOLEAN)
		{
			// true, false, 1, 0
			if((content.equals("true"))
			|| (content.equals("false"))
			|| (content.equals("1"))
			|| (content.equals("0")))
			{
				// roger
			}
			else
			{
				return false;
			}
		}

		return true;
	}

	private XSDAttribute treepath2attribute(XSDSequence schema, String tree)
	{
		String sections[] = tree.split("/");
		String section = sections[sections.length - 1];
		String elementsection = tree.substring(0, tree.length() - (section.length() + 1));
		debug("# att: DEBUG: Sections = " + sections.length);
		debug("# att: Need: " + section);
		debug("# att: Remainder: " + elementsection);

		if(section.charAt(0) == '@')
		{
			section = section.substring(1, section.length());
		}
		else
		{
			debug("# att: Error: invalid attribute spec");
			return null;
		}

		XSDElement xsdel = treepath2element(schema, elementsection);

		if(xsdel == null)
		{
			return null;
		}

		XSDType xsdtype = xsdel.getType();
		ArrayList attributes = xsdtype.getAttributes();
		for(int i = 0; i < attributes.size(); i++)
		{
			XSDAttribute xsdatt = (XSDAttribute)attributes.get(i);
			debug("# ATTRIBUTE: " + xsdatt.getName());

			if(xsdatt.getName().getLocalPart().equals(section))
			{
					debug("# Found attribute!");
					return xsdatt;
			}
		}

		debug("# att: Ooops: not found");
		return null;
	}

	private XSDElement treepath2element(XSDSequence schema, String tree)
	{
		String sections[] = tree.split("/");
		String section = sections[1];
		String remainder = tree.substring(section.length() + 1);
		debug("# DEBUG: Sections = " + sections.length);
		debug("# Need: " + section);
		debug("# Remainder: " + remainder);

		ArrayList list = schema.getElements();
		for(int i = 0; i < list.size(); i++)
		{
			XSDElement xsdelement = (XSDElement)list.get(i);
			debug("# ELEMENT: " + xsdelement.getName());
			if(xsdelement.getName().getLocalPart().equals(section))
			{
				debug("# Found correct section.");
				if(remainder.length() == 0)
				{
					debug("# Found element!");
					return xsdelement;
				}
				else
				{
					XSDType xsdtype = xsdelement.getType();
					if(xsdtype == null)
					{
						debug("# Ooops: invalid null-type");
						return null;
					}
					if(xsdtype.getType() != XSDType.TYPE_COMPLEX)
					{
						debug("# Oops: element's type is not complex");
						return null;
					}
					else
					{
						debug("# Recurse into remainder...");
						XSDSequence xsdseq = xsdtype.getSequence();
						return treepath2element(xsdseq, remainder);
					}
				}
			}
		}

		debug("# Ooops: not found");
		return null;
	}

	private String treepath_xml(Element el)
	{
		Node node = el.getParentNode();
		if(node != null)
		{
			if(node.getNodeType() == Node.ELEMENT_NODE)
			{
				return treepath_xml((Element)node) + "/" + el.getNodeName();
			}
		}

		return "";
		//return "/" + el.getNodeName();
	}

	public boolean checker(XSDSchema schema)
	{
		boolean ret = true;

		debug("(-checker-) begin");

		XSDTransformer xsdtrans = new XSDTransformer();
		xsdtrans.augment(schema, XSDParser.PARSER_TREE);
		debug(xsdtrans.getDebug());

		ArrayList list = schema.getElements();
		for(int i = 0; i < list.size(); i++)
		{
			XSDElement xsdelement = (XSDElement)list.get(i);
			XSDType xsdtype = xsdelement.getType();

			if(xsdtype == null)
			{
				debug("error: missing or unknown type '" + xsdelement.getTypeRef() + "'");
				ret = false;
				continue;
			}

			int t = xsdtype.getType();

			if(t == XSDType.TYPE_COMPLEX)
			{
				// skipping...
			}
			else
			{
				//debug("- " + t);
				boolean ordered = true;
				if(t == XSDType.TYPE_STRING) ordered = false;
				if(t == XSDType.TYPE_NORMALIZED_STRING) ordered = false;
				if(t == XSDType.TYPE_TOKEN) ordered = false;
				if(t == XSDType.TYPE_BASE64_BINARY) ordered = false;
				if(t == XSDType.TYPE_HEX_BINARY) ordered = false;
				// FIXME: warning - dangerous >= generalisation
				if(t >= XSDType.TYPE_NAME) ordered = false;

				XSDRestriction xsdres = xsdtype.getRestriction();
				if(xsdres == null)
				{
					continue;
				}
				BitSet res = xsdres.getRestrictions();

				if(ordered)
				{
					if(res.get(XSDRestriction.RESTRICTION_TOTAL_DIGITS))
					{
						// dangerous generalisation
						if(t < XSDType.TYPE_FLOAT)
						{
							debug("error: no total digits allowed");
							ret = false;
						}
					}
					if(res.get(XSDRestriction.RESTRICTION_FRACTION_DIGITS))
					{
						// dangerous generalisation
						if(t < XSDType.TYPE_DECIMAL)
						{
							BigInteger fdigits = xsdres.getFractionDigits();
							BigInteger zeroval = new BigInteger("0");
							if(fdigits.compareTo(zeroval) != 0)
							{
								debug("error: no fdigits allowed");
								ret = false;
							}
						}
						// dangerous generalisation
						else if(t != XSDType.TYPE_DECIMAL)
						{
							debug("error: no fdigits allowed");
							ret = false;
						}
					}
				}
				if(!ordered)
				{
					if((res.get(XSDRestriction.RESTRICTION_MIN_INCLUSIVE))
					|| (res.get(XSDRestriction.RESTRICTION_MAX_INCLUSIVE))
					|| (res.get(XSDRestriction.RESTRICTION_MIN_EXCLUSIVE))
					|| (res.get(XSDRestriction.RESTRICTION_MAX_EXCLUSIVE))
					|| (res.get(XSDRestriction.RESTRICTION_TOTAL_DIGITS))
					|| (res.get(XSDRestriction.RESTRICTION_FRACTION_DIGITS)))
					{
						debug("error: unapplicable restriction");
						ret = false;
					}
				}

				if(res.get(XSDRestriction.RESTRICTION_WHITE_SPACE))
				{
					if((t != XSDType.TYPE_STRING)
					&& (t != XSDType.TYPE_NORMALIZED_STRING))
					{
						int whitespace = xsdres.getWhiteSpace();
						if(whitespace != XSDRestriction.WHITESPACE_COLLAPSE)
						{
							debug("error: only collapse allowed");
							ret = false;
						}
					}
				}

				if((res.get(XSDRestriction.RESTRICTION_LENGTH))
				|| (res.get(XSDRestriction.RESTRICTION_MIN_LENGTH))
				|| (res.get(XSDRestriction.RESTRICTION_MAX_LENGTH)))
				{
					// dangerous generalisation
					if((t >= XSDType.TYPE_INTEGER)
					&& (t <= XSDType.TYPE_G_DAY))
					{
						debug("error: no length restriction allowed");
						ret = false;
					}
				}
			}
			// all: length, minLength, maxLength, pattern, enumeration, whiteSpace
			// ordered: maxInclusive, maxExclusive, minInclusive, maxInclusive,
        	        //          totalDigits, fractionDigits

			// list: length, minLength, maxLength, pattern, enumeration
			// union: pattern, enumeration
		}

		debug("(-checker-) end");

		return ret;
	}
}

