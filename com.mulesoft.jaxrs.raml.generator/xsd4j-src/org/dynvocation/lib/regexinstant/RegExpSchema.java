// RegExpInstantiator - Constraint-based value creation from regexps
// Copyright (C) 2006, 2007 Josef Spillner <spillner@rn.inf.tu-dresden.de>
//
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 3 of the License, or
// (at your option) any later version.
//
// This file is part of the RegExpInstantiator library.
// It has been created as part of Project Dynvocation, a research project
// at the Chair of Computer Networks, Faculty for Computer Sciences,
// Dresden University of Technology.
// See http://dynvocation.selfip.net/regexinstant/ for more information.

package org.dynvocation.lib.regexinstant;

// Imports - we only need collections, and XSD export
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

/**
 * \brief XML Schema export of regular expressions.
 *
 * Objects of this class are not needed to deal with regexps
 * themselves. The class can rather be seen as a utility when
 * trying to convert a regexp to XML Schema (XSD).
 *
 * The only method of this class is \ref convert, which
 * produces an XML document which represents an XML Schema
 * describing the structure of this regular expression.
 *
 * The use of the conversion is the ability to reuse existing
 * schema tools to extend their work to regexps.
 *
 * @author Josef Spillner <spillner@rn.inf.tu-dresden.de>
 */
public class RegExpSchema extends Base
{
	/**
	 * Default constructor.
	 *
	 * Produces a valid schema object which can then be
	 * used to convert a regular expression.
	 */
	public RegExpSchema()
	{
		super();
	}

	/**
	 * Convert a regular expression to a XSD document.
	 *
	 * This method takes a valid \ref RegExpExpression object
	 * and converts it to a XML document which contains XML
	 * Schema (XSD).
	 * All of the XML elements are using the XSD namespace.
	 * Since the conversion might fail, the returned document
	 * should be checked for being \b null in the case of failure.
	 *
	 * @param ree Regular expression object
	 *
	 * @return XML document containing XML Schema for the regexp
	 */
	public Document convert(RegExpExpression ree)
	{
		// Fetch the terms first
		ArrayList terms = ree.getTerms();
		if(terms == null)
		{
			System.out.println("schema error: invalid regexp");
			return null;
		}

		// Try to get an empty XML document
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;

		try
		{
			builder = factory.newDocumentBuilder();
		}
		catch(ParserConfigurationException e)
		{
			System.out.println("schema error: xml failure");
			return null;
		}

		Document doc = builder.newDocument();

		String ns_xsd = "http://www.w3.org/2001/XMLSchema";

		Element xml_schema = doc.createElementNS(ns_xsd, "schema");
		doc.appendChild(xml_schema);

		xml_schema.setAttribute("targetNamespace", "urn:regex");
		xml_schema.setAttribute("elementFormDefault", "qualified");
		xml_schema.setAttribute("attributeFormDefault", "qualified");

		Element xml_root = doc.createElementNS(ns_xsd, "element");
		xml_schema.appendChild(xml_root);
		xml_root.setAttribute("name", "regex");

		Element xml_rootct;
		Element xml_rootseq;

		if(terms.size() > 1)
		{
			xml_rootct = doc.createElementNS(ns_xsd, "complexType");
			xml_root.appendChild(xml_rootct);

			xml_rootseq = doc.createElementNS(ns_xsd, "sequence");
			xml_rootct.appendChild(xml_rootseq);
		}
		else
		{
			xml_rootct = doc.createElementNS(ns_xsd, "simpleType");
			xml_root.appendChild(xml_rootct);

			xml_rootseq = doc.createElementNS(ns_xsd, "restriction");
			xml_rootct.appendChild(xml_rootseq);
			xml_rootct.setAttribute("base", "string");
		}

		// Convert all terms individually
		for(int i = 0; i < terms.size(); i++)
		{
			RegExpTerm t = (RegExpTerm)terms.get(i);

			// Shortcut for alternatives
			// FIXME: doesn't yet check for const-ness!
			if(t.getAlternativeTerms().size() > 0)
			{
				Element xml_altterm = doc.createElementNS(ns_xsd, "enumeration");
				xml_rootseq.appendChild(xml_altterm);
				Text xml_text = doc.createTextNode(t.instanceString(1));
				xml_altterm.appendChild(xml_text);

				for(int j = 0; j < t.getAlternativeTerms().size(); j++)
				{
					RegExpTerm altterm = (RegExpTerm)t.getAlternativeTerms().get(j);
					xml_altterm = doc.createElementNS(ns_xsd, "enumeration");
					xml_rootseq.appendChild(xml_altterm);
					xml_text = doc.createTextNode(altterm.instanceString(1));
					xml_altterm.appendChild(xml_text);
				}
				continue;
			}

			Element xml_el = doc.createElementNS(ns_xsd, "element");
			xml_rootseq.appendChild(xml_el);
			xml_el.setAttribute("name", "element" + i);
			xml_el.setAttribute("type", "re:term" + i);

			if(t.getMinOccurs() != 1)
			{
				xml_el.setAttribute("minOccurs", "" + t.getMinOccurs());
			}
			if(t.getMaxOccurs() != 1)
			{
				xml_el.setAttribute("maxOccurs", "" + t.getMaxOccurs());
			}

			Element xml_complextype = doc.createElementNS(ns_xsd, "complexType");
			xml_schema.appendChild(xml_complextype);
			xml_complextype.setAttribute("name", "term" + i);

			Element xml_seq = doc.createElementNS(ns_xsd, "sequence");
			xml_complextype.appendChild(xml_seq);

			// Convert all the characters of each term
			ArrayList characters = t.getCharacters();
			for(int j = 0; j < characters.size(); j++)
			{
				RegExpCharacter ch = (RegExpCharacter)characters.get(j);

				Element xml_element;
				
				if(ch.getType() != RegExpCharacter.CHARACTER_SET)
				{
					xml_element = doc.createElementNS(ns_xsd, "element");
					xml_seq.appendChild(xml_element);
					xml_element.setAttribute("name", "c" + j);
				}
				else
				{
					xml_element = doc.createElementNS(ns_xsd, "choice");
					xml_seq.appendChild(xml_element);
				}

				if(ch.getType() == RegExpCharacter.CHARACTER_CHARACTER)
				{
					String fixed = new String();
					fixed += ch.getCharacter();
					xml_element.setAttribute("type", "xsd:string");
					xml_element.setAttribute("fixed", fixed);
				}
				else if(ch.getType() == RegExpCharacter.CHARACTER_SYMBOL)
				{
					int symbol = ch.getSymbol();
					if(symbol == RegExpCharacter.SYMBOL_DIGIT)
					{
						Element xml_st = doc.createElementNS(ns_xsd, "simpleType");
						xml_element.appendChild(xml_st);

						Element xml_res = doc.createElementNS(ns_xsd, "restriction");
						xml_st.appendChild(xml_res);
						xml_res.setAttribute("base", "xsd:int");

						Element xml_min= doc.createElementNS(ns_xsd, "minInclusive");
						xml_res.appendChild(xml_min);
						xml_min.setAttribute("value", "0");

						Element xml_max= doc.createElementNS(ns_xsd, "maxInclusive");
						xml_res.appendChild(xml_max);
						xml_max.setAttribute("value", "9");
					}
					if(symbol == RegExpCharacter.SYMBOL_NON_DIGIT)
					{
						xml_element.setAttribute("type", "xsd:string");
					}
					if(symbol == RegExpCharacter.SYMBOL_WORD)
					{
						xml_element.setAttribute("type", "xsd:string");
					}
					if(symbol == RegExpCharacter.SYMBOL_NON_WORD)
					{
						xml_element.setAttribute("type", "xsd:string");
					}
					if(symbol == RegExpCharacter.SYMBOL_WHITESPACE)
					{
						xml_element.setAttribute("type", "xsd:string");
					}
					if(symbol == RegExpCharacter.SYMBOL_NON_WHITESPACE)
					{
						xml_element.setAttribute("type", "xsd:string");
					}
				}
				else if(ch.getType() == RegExpCharacter.CHARACTER_SET)
				{
					ArrayList sets = ch.getSets();
					for(int k = 0; k < sets.size(); k++)
					{
						Element xml_ce = doc.createElementNS(ns_xsd, "element");
						xml_element.appendChild(xml_ce);
						xml_ce.setAttribute("name", "c" + j + "_" + k);

						char[] chars = (char[])sets.get(k);
						if(chars.length == 1)
						{
							char c = chars[0];
							xml_ce.setAttribute("fixed", "" + c);
						}
						else
						{
							Element xml_st = doc.createElementNS(ns_xsd, "simpleType");
							xml_ce.appendChild(xml_st);

							Element xml_res = doc.createElementNS(ns_xsd, "restriction");
							xml_st.appendChild(xml_res);
							xml_res.setAttribute("base", "xsd:string");

							char first = chars[0];
							char last = chars[chars.length - 1];
							int diff = last - first + 1;
							for(int l = 0; l < diff; l++)
							{
								char c = chars[l];

								Element xml_enum = doc.createElementNS(ns_xsd, "enumeration");
								xml_res.appendChild(xml_enum);
								xml_enum.setAttribute("value", "" + c);
							}
						}
					}
				}
			}
		}

		return doc;
	}
}

