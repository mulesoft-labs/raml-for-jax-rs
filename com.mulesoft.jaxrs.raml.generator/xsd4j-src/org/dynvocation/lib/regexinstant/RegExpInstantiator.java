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

/**
 * \mainpage
 *
 * RegExpInstantiator is a small library containing several classes for
 * dealing with regular expressions (regexps) in non-standard ways.
 * It contains a regexp parser, an instantiator, a constraint facility
 * for it and a way to convert regexps to XML Schema (XSD).
 *
 * \section Parsing
 *
 * Unfortunately, none of the existing regexp libraries makes it possible
 * to access the parse tree. Therefore, \ref RegExpInstantiator (the main
 * class after which the library is named) provides the \ref
 * RegExpInstantiator::build method which can parse simple regular
 * expressions. Please refer to the README file to find out about which
 * expressions are supported.
 *
 * The parser returns a \ref RegExpExpression object, which consists of
 * a list of \ref RegExpTerm, each having some quantification information
 * and a list of \ref RegExpCharacter, which may be normal characters,
 * symbolic character classes and arbitrary classes (sets).
 *
 * \section Instantiating
 *
 * Instantiating a regexp is called solving a constraint-satisfaction
 * problem (CSP): Which strings can be produced by a regexp so that they
 * will in turn match the regexp again?
 * All of the expression, term and character objects provide a method
 * to produce such strings.
 * In addition, it is possible to retrieve the constrained regexp
 * string and finally the original regexp definition which was used
 * for parsing.
 *
 * \section Constraining
 *
 * Objects of the \ref RegExpConstraint class can be used to represent
 * constraints on regexp productions such as the minimum length of the
 * resulting string.
 * The \ref RegExpInstantiator object provides a method to apply such
 * a constraint to retrieve a new object sharing the regexp with the
 * old one, but having a unique set of constraints already calculated
 * and applied.
 *
 * \section Schema_export
 *
 * Regexps have, perhaps surprisingly, a lot in common with XML Schema
 * (XSD) files. It is possible to some extent to convert the former
 * into the latter. The \ref RegExpSchema class can be used for this
 * task.
 *
 * \section XSD4J_integration
 *
 * The XSD4J library uses the instantiator to create an initial valid
 * instance data set. However, the Dynvoker application which uses XSD4J
 * might also be configured to convert the regexp into a schema first,
 * thus providing the same initial instance, but a much more convenient
 * way of creating a form for the regexp.
 */

/**
 * \brief Builder class for regular expression objects.
 *
 * This is the main class of the RegExpInstantiator library.
 * It provides two methods, for parsing regexps into
 * \ref RegExpExpression objects and to constrain those
 * objects using \ref RegExpConstraint objects.
 *
 * @author Josef Spillner <spillner@rn.inf.tu-dresden.de>
 */
public class RegExpInstantiator extends Base
{
	/**
	 * Default constructor.
	 *
	 * Produces a valid object of this class which can then
	 * be used to parse or constrain \ref RegExpExpression
	 * objects.
	 */
	public RegExpInstantiator()
	{
		super();
	}

	/**
	 * Constrain a regular expression.
	 *
	 * A copy of a regexp object is returned which shares the same
	 * content, but includes a constraints table on how often each term
	 * might appear for string instance generation.
	 * In the case no instance string would satisfy the constraints, or
	 * if the constraints object is invalid, \b null is returned instead.
	 * The regexp object given as a paramter is left unmodified in any case.
	 *
	 * Constrained regexps are shallow copies of their unconstrained
	 * originals - modifying one will modify the other, probably
	 * going unnoticed!
	 *
	 * @param ree Regular expression object to constrain
	 * @param rec Constraint to apply to copy of the regexp object
	 *
	 * @return Shallow copy of the original regexp with constraints, or \b null
	 */
	public RegExpExpression constrain(RegExpExpression ree, RegExpConstraint rec)
	{
		if(!rec.isValid())
		{
			System.out.println("constraint error: invalid state");
			return null;
		}

		ArrayList terms = ree.getTerms();
		int[] lengths = new int[terms.size()];
		int[] counts = new int[terms.size()];
		RegExpTerm[] aterms = new RegExpTerm[terms.size()];

		for(int i = 0; i < terms.size(); i++)
		{
			RegExpTerm t = (RegExpTerm)terms.get(i);
			lengths[i] = t.length();
			counts[i] = t.getMinOccurs();
			aterms[i] = t;
		}

		int sum;
		int minsum = -1;
		int maxsum = -1;
		boolean found = false;
		while(true)
		{
			sum = 0;
			for(int i = 0; i < terms.size(); i++)
			{
				int length = lengths[i];
				int count = counts[i];
				int termlength = count * length;
				sum += termlength;
			}

			if((rec.getMinLength() == -1) || (sum >= rec.getMinLength()))
			{
				if((rec.getMaxLength() == -1) || (sum <= rec.getMaxLength()))
				{
					found = true;
					break;
				}
			}

			if((sum < minsum) || (minsum == -1))
			{
				minsum = sum;
			}
			if((sum > maxsum) || (maxsum == -1))
			{
				maxsum = sum;
			}

			int pos = 0;
			counts[pos] += 1;
			while(pos < terms.size())
			{
				if(counts[pos] > aterms[pos].getMaxOccurs())
				{
					counts[pos] = aterms[pos].getMinOccurs();
					if(pos < terms.size() - 1)
					{
						counts[pos + 1] += 1;
					}
					pos += 1;
				}
				else
				{
					break;
				}
			}
			if(pos == terms.size())
			{
				System.out.println("constraint error: no matching count");
				break;
			}
		}

		if(!found)
		{
			if(maxsum > rec.getMaxLength())
			{
				System.out.println("constraint error: all instances too long");
				return null;
			}
			if(minsum < rec.getMinLength())
			{
				System.out.println("constraint error: all instances too short");
				return null;
			}
		}

		/*String ret = new String();
		for(int i = 0; i < terms.size(); i++)
		{
			Term t = (Term)terms.get(i);
			ret += t.quantified(counts[i]);
		}*/

		RegExpExpression reec = new RegExpExpression();
		reec.setTerms(ree.getTerms());
		reec.constrain(counts);

		return reec;
	}

	// Workaround for awful ArrayList.subList method
	private ArrayList subList(ArrayList src, int fromincl, int toexcl)
	{
		ArrayList dst = new ArrayList();
		for(int i = fromincl; i < toexcl; i++)
		{
			dst.add(src.get(i));
		}
		return dst;
	}

	/**
	 * Parses a regular expression.
	 *
	 * Calling this method with a string containing a regular
	 * expression yields a \ref RegExpExpression object representing
	 * this expression, or \b null if the regexp was faulty.
	 *
	 * The returned object contains terms of type \ref RegExpTerm
	 * which in turn contain characters objects of type
	 * \ref RegExpCharacter.
	 * It is unconstrained, meaning that it may produce all possible
	 * instance strings as specified by the regexp.
	 *
	 * @param regexp String containing a regular expression
	 *
	 * @return Regular expression object, or \b null
	 */
	public RegExpExpression build(String regexp)
	{
		RegExpExpression ree = new RegExpExpression();
		ArrayList terms = new ArrayList();
		ArrayList tmplist = new ArrayList();
		boolean quoted = false;
		boolean push = false;
		boolean noback = false;
		int altterm = 0;
		int min, max;

		for(int i = 0; i < regexp.length(); i++)
		{
			char c = regexp.charAt(i);

			if(c == '\\')
			{
				quoted = true;
				if(i == regexp.length())
				{
					System.out.println("regexp error: quoting at end");
					return null;
				}
				continue;
			}
			else
			{
				if(quoted)
				{
					RegExpCharacter ch = new RegExpCharacter();
					ch.parse(c, true);
					tmplist.add(ch);

					quoted = false;
					continue;
				}
			}

			min = 1;
			max = 1;

			if(c == '?')
			{
				push = true;
				min = 0;
				max = 1;
			}
			else if(c == '*')
			{
				push = true;
				min = 0;
				max = -1;
			}
			else if(c == '+')
			{
				push = true;
				min = 1;
				max = -1;
			}
			else if(c == '{')
			{
				min = -1;
				max = -1;

				push = true;
				String count = new String();
				for(i = i + 1; i < regexp.length(); i++)
				{
					c = regexp.charAt(i);
					if(c == ',')
					{
						Integer ints = new Integer(count);
						min = ints.intValue();
						count = new String();
					}
					else if(c == '}')
					{
						Integer ints = new Integer(count);
						max = ints.intValue();
						if(min == -1)
						{
							min = max;
						}
						break;
					}
					else
					{
						count += c;
					}
				}
				if((min == -1) || (max == -1))
				{
					System.out.println("regexp error: wrong count");
					return null;
				}
			}
			else if(c == '(')
			{
				String term = new String();
				int nesting = 1;
				for(i = i + 1; i < regexp.length(); i++)
				{
					c = regexp.charAt(i);
					if(c == '(')
					{
						nesting += 1;
					}

					if(c == ')')
					{
						nesting -= 1;
						if(nesting == 0)
						{
							push = true;
							RegExpCharacter ch = new RegExpCharacter();
							RegExpExpression nestedree = build(term);
							if(nestedree == null)
							{
								System.out.println("regexp error: nested expression error");
								return null;
							}
							ch.setType(RegExpCharacter.CHARACTER_EXPRESSION);
							ch.setExpression(nestedree);
							tmplist.add(ch);
							break;
						}
					}

					term += c;
				}
			}
			else if(c == '[')
			{
				String set = new String();
				int nesting = 1;
				for(i = i + 1; i < regexp.length(); i++)
				{
					c = regexp.charAt(i);
					if(c == '[')
					{
						nesting += 1;
						if(nesting == 3)
						{
							System.out.println("regexp error: set nesting too deep");
							return null;
						}
					}

					if(c == ']')
					{
						nesting -= 1;
						if(nesting == 0)
						{
							RegExpCharacter ch = new RegExpCharacter();
							ch.parseSet(set);
							tmplist.add(ch);
							break;
						}
					}

					set += c;
				}
			}
			else if(c == '|')
			{
				push = true;
				noback = true;
				altterm++;
			}
			else
			{
				RegExpCharacter ch = new RegExpCharacter();
				ch.parse(c, false);
				tmplist.add(ch);
			}

			if(i == regexp.length() - 1)
			{
				push = true;
				noback = true;
				if(altterm > 0)
					altterm++;
			}

			if(push)
			{
				if(tmplist.size() == 0)
				{
					// apply to last TERM term
					RegExpTerm t = (RegExpTerm)terms.get(terms.size() - 1);
					t.setMinOccurs(min);
					t.setMaxOccurs(max);
					push = false;
				}
			}

			if(push)
			{
				ArrayList lastterm;
				boolean group = false;
				boolean g1 = false;
				boolean g2 = false;
				boolean go = false;
				RegExpCharacter chfirst = (RegExpCharacter)tmplist.get(0);
				RegExpCharacter chlast = (RegExpCharacter)tmplist.get(tmplist.size() - 1);
				if(chfirst.getType() == RegExpCharacter.CHARACTER_CHARACTER)
				{
					if(chfirst.getCharacter() == '(') g1 = true;
				}
				if(chlast.getType() == RegExpCharacter.CHARACTER_CHARACTER)
				{
					if(chlast.getCharacter() == ')') g2 = true;
				}
				if((g1 == true) && (g2 == true)) group = true;

				if(tmplist.size() > 2)
				{
					RegExpCharacter chlastbo = (RegExpCharacter)tmplist.get(tmplist.size() - 2);
					if(chlastbo.getType() == RegExpCharacter.CHARACTER_CHARACTER)
					{
						if(chlastbo.getCharacter() == '\\') go = true;
					}
				}

				if(group)
				{
					tmplist = subList(tmplist, 1, tmplist.size() - 1);
				}
				else if(!noback)
				{
					int back = 1;
					if(go)
					{
						back = 2;
					}
					if(tmplist.size() > back)
					{
						lastterm = subList(tmplist, tmplist.size() - back, tmplist.size());
						tmplist = subList(tmplist, 0, tmplist.size() - back);
				
						RegExpTerm t = new RegExpTerm();
						t.setCharacters(tmplist);
						t.setMinOccurs(1);
						t.setMaxOccurs(1);
						terms.add(t);

						tmplist = lastterm;
					}
				}
				else
				{
					noback = false;
				}

				RegExpTerm t = new RegExpTerm();
				t.setCharacters(tmplist);
				t.setMinOccurs(min);
				t.setMaxOccurs(max);

				if(altterm <= 1)
				{
					terms.add(t);
				}
				else
				{
					RegExpTerm origterm = (RegExpTerm)terms.get(terms.size() - 1);
					origterm.addAlternativeTerm(t);
				}

				tmplist = new ArrayList();
				push = false;
			}
		}

		ree.setTerms(terms);

		return ree;
	}
}

