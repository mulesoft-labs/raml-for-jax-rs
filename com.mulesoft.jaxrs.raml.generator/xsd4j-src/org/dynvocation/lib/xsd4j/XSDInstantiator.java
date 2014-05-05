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

import org.dynvocation.lib.regexinstant.*;

import java.util.*;
import java.math.*;

import javax.xml.namespace.*;

import org.w3c.dom.*;

/**
 * \brief Instantiator class for schema expressions.
 *
 * The goal of this class is to be able to produce XML instance documents
 * whose structure is determined by the source schema.
 * This is needed to create initial instances for XForms models, for
 * example.
 * Smart values are used to honour restrictions on simple types.
 *
 * @author Josef Spillner <spillner@rn.inf.tu-dresden.de>
 */
public class XSDInstantiator extends XMLBase
{
	/**
	 * Default constructor.
	 *
	 * Creates an empty instantiator object.
	 */
	public XSDInstantiator()
	{
		super();
	}

	/**
	 * Creates an instance of a complete schema.
	 *
	 * This method iterates over all top-level elements of a schema
	 * and converts them to XML instance data.
	 *
	 * @param xsdschema Schema to convert to an instance
	 *
	 * @return XML document containing the instances below the root element
	 *
	 * @see create
	 */
	public Document createall(XSDSchema xsdschema)
	{
		Document doc = XSDCommon.createDocument();

		Element xml_root = doc.createElement("INSTANCE-ROOT");
		doc.appendChild(xml_root);

		ArrayList list = xsdschema.getElements();
		for(int i = 0; i < list.size(); i++)
		{
			XSDElement xsdelement = (XSDElement)list.get(i);

			create(xsdelement, doc, xml_root, xsdschema.getTargetNamespace());
		}

		return doc;
	}
	
	/**
	 * Creates an instance of a complete schema.
	 *
	 * If there's only one top element, uses it as a root node, 
	 * otherwise iterates over all top-level elements of a schema
	 * and converts them to XML instance data.
	 *
	 * @param xsdschema Schema to convert to an instance
	 *
	 * @return XML document containing the instances below the root element
	 *
	 * @see create
	 */
	public Document createWithRoot(XSDSchema xsdschema)
	{
		Document doc = XSDCommon.createDocument();

		if (xsdschema.getElements().size() == 1) {
			XSDElement xsdelement = (XSDElement) xsdschema.getElements().get(0);
			create(xsdelement, doc, null, xsdschema.getTargetNamespace());
		} else {
			Element xmlRoot = doc.createElement("INSTANCE-ROOT");
			doc.appendChild(xmlRoot);
			
			ArrayList list = xsdschema.getElements();
			for(int i = 0; i < list.size(); i++)
			{
				XSDElement xsdelement = (XSDElement)list.get(i);
				
				create(xsdelement, doc, xmlRoot, xsdschema.getTargetNamespace());
			}
		}

		return doc;
	}


	public Document createtype(XSDSchema xsdschema, QName typename)
	{
		Document doc = XSDCommon.createDocument();

		Element xml_root = doc.createElementNS(typename.getNamespaceURI(), typename.getLocalPart());
		doc.appendChild(xml_root);

		ArrayList types = xsdschema.getTypes();
		for(int i = 0; i < types.size(); i++)
		{
			//XSDType xsdtype = (XSDType)types.get(i);
			// FIXME: we don't have type-based yet!
		}

		return doc;
	}

	public Document createelement(XSDSchema xsdschema, QName elementname)
	{
		ArrayList elements = xsdschema.getElements();
		for(int i = 0; i < elements.size(); i++)
		{
			XSDElement xsdelement = (XSDElement)elements.get(i);

			if(xsdelement.getName().equals(elementname))
			{
				Document doc = XSDCommon.createDocument();
				create(xsdelement, doc, null, xsdschema.getTargetNamespace());
				return doc;
			}
		}

		debug("Error: requested element " + elementname + " not found");

		return null;
	}

	/**
	 * Creates an instance of a schema element.
	 *
	 * This method generates an initial XML instance from a specific element
	 * from a schema. The type of the element and possibly further
	 * elements and attributes from the type are taken into account,
	 * so that all restrictions on the type are fulfilled by the instance.
	 *
	 * @param xsdelement Schema element to generate instance from
	 * @param doc XML document to put XML instance data in
	 * @param parent XML root node for the XML instance data
	 * @param namespace Namespace for the resulting instance
	 */
	public void create(XSDElement xsdelement, Document doc, Element parent, String namespace)
	{
		String ns_xsi = XSDCommon.NAMESPACE_XSI;
		String ns_i = namespace;

		XSDType t = xsdelement.getType();
		debug("Element " + xsdelement.getName() + " of type " + t.getName());

		if(t.getType() == XSDType.TYPE_COMPLEX)
		{
			debug("- is complex");

			Element el;
			if(xsdelement.getQualified())
			{
				el = doc.createElementNS(ns_i, typename(xsdelement.getName()));
			}
			else
			{
				el = doc.createElement(xsdelement.getName().getLocalPart());
			}

			if(parent == null)
			{
				doc.appendChild(el);
			}
			else
			{
				parent.appendChild(el);
			}

			XSDSequence xsdseq = t.getSequence();
			if(xsdseq != null)
			{
				ArrayList list = xsdseq.getElements();
				for(int i = 0; i < list.size(); i++)
				{
					XSDElement xsdchild = (XSDElement)list.get(i);
					create(xsdchild, doc, el, namespace);
					if(xsdchild.getMinOccurs() > 1)
					{
						for(int j = 1; j < xsdchild.getMinOccurs(); j++)
						{
							create(xsdchild, doc, el, namespace);
						}
					}
				}
			}

			// FIXME: how does SOAP handle (typed) attributes?
			ArrayList attributes = t.getAttributes();
			for(int i = 0; i < attributes.size(); i++)
			{
				XSDAttribute xsdatt = (XSDAttribute)attributes.get(i);

				createattribute(xsdatt, el, ns_i);
			}

			// Simple content or complex extension, or complex restriction
			XSDType basetype = t.getBaseType();
			if(basetype != null)
			{
				if(basetype.getType() == XSDType.TYPE_COMPLEX)
				{
					xsdseq = basetype.getSequence();
					if((xsdseq != null) && (!t.getRestricted()))
					{
						ArrayList list = xsdseq.getElements();
						for(int i = 0; i < list.size(); i++)
						{
							XSDElement xsdchild = (XSDElement)list.get(i);
							create(xsdchild, doc, el, namespace);
						}
					}

					attributes = basetype.getAttributes();
					for(int i = 0; i < attributes.size(); i++)
					{
						XSDAttribute xsdatt = (XSDAttribute)attributes.get(i);

						createattribute(xsdatt, el, ns_i);
					}
				}
				else
				{
					String smartinstance = smartvalue(basetype);
					if(smartinstance != null)
					{
						Text smart = doc.createTextNode(smartinstance);
						el.appendChild(smart);
					}
				}
			}
		}
		else if(t.getType() == XSDType.TYPE_CHOICE)
		{
			debug("- is choice");

			// FIXME: always use first one?
			XSDChoice xsdchoice = t.getChoice();
			ArrayList groups = xsdchoice.getGroups();
			ArrayList elements = xsdchoice.getElements();
			if(groups.size() > 0)
			{
				XSDSequence seq = (XSDSequence)groups.get(0);
				ArrayList seqelements = seq.getElements();

				for(int i = 0; i < seqelements.size(); i++)
				{
					XSDElement elem = (XSDElement)seqelements.get(i);
					create(elem, doc, parent, namespace);
				}
			}
			else if(elements.size() > 0)
			{
				XSDElement elem = (XSDElement)elements.get(0);
				create(elem, doc, parent, namespace);
			}
		}
		else if(t.getType() == XSDType.TYPE_GROUP)
		{
			debug("- is group");

			XSDSequence xsdgroup = t.getSequence();
			ArrayList seqelements = xsdgroup.getElements();
			for(int i = 0; i < seqelements.size(); i++)
			{
				XSDElement elem = (XSDElement)seqelements.get(i);
				create(elem, doc, parent, namespace);
			}
		}
		else
		{
			debug("- is simple");

			QName nname = xsdelement.getName();
			QName nqtype = xsdelement.getType().getName();

			if(xsdelement.getMaxOccurs() == 0)
			{
				return;
			}
			// FIXME: handle minOccurs > 1

			Element el;
			if(xsdelement.getQualified())
			{
				el = doc.createElementNS(ns_i, typename(nname));
			}
			else
			{
				el = doc.createElement(nname.getLocalPart());
			}

			if(nqtype != null)
			{
				el.setAttributeNS(ns_xsi, "type", typename(nqtype));
			}

			if(parent == null)
			{
				doc.appendChild(el);
			}
			else
			{
				parent.appendChild(el);
			}

			String smartinstance = null;
			if(xsdelement.getFixedValue() != null)
			{
				smartinstance = xsdelement.getFixedValue();
				debug("- assign fixed smartinstance value " + smartinstance);
			}
			else if(xsdelement.getDefaultValue() != null)
			{
				smartinstance = xsdelement.getDefaultValue();
				debug("- assign default smartinstance value " + smartinstance);
			}
			else
			{
				XSDType basetype = t.getBaseType();
				if(basetype != null)
				{
					debug("- base type is " + basetype.getName());
					if(basetype.getType() == XSDType.TYPE_COMPLEX)
					{
						// FIXME: must not happen!
					}
					else
					{
						smartinstance = smartvalue(basetype);
						debug("- assign base type smartinstance value " + smartinstance);
					}
				}
				else
				{
					smartinstance = smartvalue(xsdelement.getType());
					debug("- assign regular smartinstance value " + smartinstance);
				}
			}
			if(smartinstance != null)
			{
				Text smart = doc.createTextNode(smartinstance);
				el.appendChild(smart);
			}

			if(xsdelement.getNillable())
			{
				el.setAttributeNS(ns_xsi, "nil", "false");
			}
		}
	}

	private void createattribute(XSDAttribute xsdatt, Element el, String ns_i)
	{
		if(xsdatt.getType().getType() == XSDType.TYPE_ATTRIBUTEGROUP)
		{
			XSDSequence group = xsdatt.getType().getSequence();
			ArrayList attributes = group.getAttributes();
			for(int i = 0; i < attributes.size(); i++)
			{
				XSDAttribute xsdatt2 = (XSDAttribute)attributes.get(i);
				createattribute(xsdatt2, el, ns_i);
			}
			return;
		}

		if(xsdatt.getUse() == XSDAttribute.USE_PROHIBITED)
		{
			return;
		}

		String smartinstance = null;
		if(xsdatt.getFixedValue() != null)
		{
			smartinstance = xsdatt.getFixedValue();
		}
		else if(xsdatt.getDefaultValue() != null)
		{
			smartinstance = xsdatt.getDefaultValue();
		}
		else
		{
			smartinstance = smartvalue(xsdatt.getType());
		}
		if(smartinstance == null)
		{
			smartinstance = "";
		}

		if(xsdatt.getQualified())
		{
			el.setAttributeNS(ns_i, typename(xsdatt.getName()), smartinstance);
		}
		else
		{
			el.setAttribute(xsdatt.getName().getLocalPart(), smartinstance);
			// FIXME: smart attributes
		}
	}

	// This method produces valid values for possibly restricted simple types
	// Sometimes, the empty string is valid, but often it is not!
	private String smartvalue(XSDType xsdtype)
	{
		// FIXME: smartness for non-string defaults!
		// (another valuable concept of WSGUI engines)
		// FIXME: smartness needed for all value-restricted type
		//        where empty string is not a valid value
		// FIXME: always keep in sync with xsdComponent
		int typetype = xsdtype.getType();

		if(typetype == XSDType.TYPE_LIST)
		{
			XSDType basetype = xsdtype.getBaseType();
			String listsmart = smartvalue(basetype);
			return listsmart;
		}
		else if(typetype == XSDType.TYPE_UNION)
		{
			ArrayList membertypes = xsdtype.getMemberTypes();
			if(membertypes.size() > 0)
			{
				XSDType uniontype = (XSDType)membertypes.get(0);
				String unionsmart = smartvalue(uniontype);
				return unionsmart;
			}
		}
		else if((typetype == XSDType.TYPE_INTEGER)
		|| (typetype == XSDType.TYPE_NON_NEGATIVE_INTEGER)
		|| (typetype == XSDType.TYPE_NON_POSITIVE_INTEGER)
		|| (typetype == XSDType.TYPE_LONG)
		|| (typetype == XSDType.TYPE_UNSIGNED_LONG)
		|| (typetype == XSDType.TYPE_INT)
		|| (typetype == XSDType.TYPE_UNSIGNED_INT)
		|| (typetype == XSDType.TYPE_SHORT)
		|| (typetype == XSDType.TYPE_UNSIGNED_SHORT)
		|| (typetype == XSDType.TYPE_BYTE)
		|| (typetype == XSDType.TYPE_UNSIGNED_BYTE)
		|| (typetype == XSDType.TYPE_DECIMAL)
		|| (typetype == XSDType.TYPE_FLOAT)
		|| (typetype == XSDType.TYPE_DOUBLE))
		{
			return "0";
		}
		else if(typetype == XSDType.TYPE_NEGATIVE_INTEGER)
		{
			return "-1";
		}
		else if(typetype == XSDType.TYPE_POSITIVE_INTEGER)
		{
			return "1";
		}
		else if(typetype == XSDType.TYPE_BOOLEAN)
		{
			return "false";
		}
		else if(typetype == XSDType.TYPE_DURATION)
		{
			return "P0D";
		}
		else if(typetype == XSDType.TYPE_DATE_TIME)
		{
			// FIXME: dynamic according to current date?
			return "2006-01-01T00:00:00";
		}
		else if(typetype == XSDType.TYPE_DATE)
		{
			return "2006-01-01";
		}
		else if(typetype == XSDType.TYPE_TIME)
		{
			return "00:00:00";
		}
		else if(typetype == XSDType.TYPE_G_YEAR)
		{
			return "2006";
		}
		else if(typetype == XSDType.TYPE_G_YEAR_MONTH)
		{
			return "2006-01";
		}
		else if(typetype == XSDType.TYPE_G_MONTH)
		{
			return "01"; // --MM
		}
		else if(typetype == XSDType.TYPE_G_MONTH_DAY)
		{
			return "01-01"; // --MM-DD
		}
		else if(typetype == XSDType.TYPE_G_DAY)
		{
			return "01"; // ---DD
		}
		else if(typetype == XSDType.TYPE_NAME)
		{
			return null; // ???
		}
		else if(typetype == XSDType.TYPE_QNAME)
		{
			return null; // ???
		}
		else if(typetype == XSDType.TYPE_NCNAME)
		{
			return null; // ???
		}
		else if(typetype == XSDType.TYPE_ANY_URI)
		{
			return "http://";
		}
		else if(typetype == XSDType.TYPE_LANGUAGE)
		{
			return "de";
		}
		else if(typetype == XSDType.TYPE_ID)
		{
			return null; // ???
		}
		else if(typetype == XSDType.TYPE_IDREF)
		{
			return null; // ???
		}
		else if(typetype == XSDType.TYPE_IDREFS)
		{
			return "x x"; // ???
		}
		else if(typetype == XSDType.TYPE_ENTITY)
		{
			return null; // ???
		}
		else if(typetype == XSDType.TYPE_ENTITIES)
		{
			return "x x"; // ??
		}
		else if(typetype == XSDType.TYPE_NOTATION)
		{
			return null;
		}
		else if(typetype == XSDType.TYPE_NMTOKEN)
		{
			return null; // ???
		}
		else if(typetype == XSDType.TYPE_NMTOKENS)
		{
			return "x x";
		}

		XSDRestriction xsdres = xsdtype.getRestriction();
		if(xsdres != null)
		{
			BitSet res = xsdres.getRestrictions();

			if(res.get(XSDRestriction.RESTRICTION_ENUMERATION))
			{
				ArrayList list = xsdres.getEnumerations();

				if(list.size() > 0)
				{
					String enumeration = (String)list.get(0);
					return enumeration;
				}
			}

			boolean range = false;
			String startrange = null;
			String endrange = null;

			if(res.get(XSDRestriction.RESTRICTION_MIN_INCLUSIVE))
			{
				BigInteger min = xsdres.getMinInclusive();
				startrange = min.toString();
			}
			if(res.get(XSDRestriction.RESTRICTION_MAX_INCLUSIVE))
			{
				BigInteger max = xsdres.getMaxInclusive();
				endrange = max.toString();
			}
			if(res.get(XSDRestriction.RESTRICTION_MIN_EXCLUSIVE))
			{
				BigInteger min = xsdres.getMinExclusive();
				startrange = min.toString();
			}
			if(res.get(XSDRestriction.RESTRICTION_MAX_EXCLUSIVE))
			{
				BigInteger max = xsdres.getMaxExclusive();
				endrange = max.toString();
			}
			// FIXME: exclude exclusive values but we don't know the type
			if((startrange != null) && (endrange != null))
			{
				range = true;
			}

			if(range)
			{
				String minstring = startrange;
				return minstring;
			}

			if(res.get(XSDRestriction.RESTRICTION_TOTAL_DIGITS))
			{
				//Integer digits = new Integer(xsdres.getTotalDigits()); // unused
				//int totaldigits = digits.intValue(); // unused
				// FIXME!
			}
			if(res.get(XSDRestriction.RESTRICTION_FRACTION_DIGITS))
			{
				//Integer digits = new Integer(xsdres.getFractionDigits()); // unused
				//int fractiondigits = digits.intValue(); // unused
				// FIXME!
			}

			BigInteger minlength = null;
			BigInteger maxlength = null;

			if(res.get(XSDRestriction.RESTRICTION_LENGTH))
			{
				BigInteger both = xsdres.getLength();
				minlength = both;
				maxlength = both;
			}
			if(res.get(XSDRestriction.RESTRICTION_MIN_LENGTH))
			{
				BigInteger min = xsdres.getMinLength();
				minlength = min;
			}
			if(res.get(XSDRestriction.RESTRICTION_MAX_LENGTH))
			{
				BigInteger max = xsdres.getMaxLength();
				maxlength = max;
			}

			if(res.get(XSDRestriction.RESTRICTION_PATTERN))
			{
				String instance = null;
				String pattern = xsdres.getPattern();

				// FIXME: RegExpConstraint only accepts integers
				int minconstraint = -1;
				int maxconstraint = -1;
				if(minlength != null)
				{
					minconstraint = minlength.intValue();
				}
				if(maxlength != null)
				{
					maxconstraint = maxlength.intValue();
				}

				RegExpConstraint rec = new RegExpConstraint();
				rec.addConstraint(RegExpConstraint.MIN_LENGTH, minconstraint);
				rec.addConstraint(RegExpConstraint.MAX_LENGTH, maxconstraint);
				RegExpInstantiator rei = new RegExpInstantiator();
				RegExpExpression ree = rei.build(pattern);
				RegExpExpression reec = rei.constrain(ree, rec);
				instance = reec.instanceString();

				return instance;
			}

			if(res.get(XSDRestriction.RESTRICTION_WHITE_SPACE))
			{
				// ignore???
			}
		}

		return null;
	}
}

