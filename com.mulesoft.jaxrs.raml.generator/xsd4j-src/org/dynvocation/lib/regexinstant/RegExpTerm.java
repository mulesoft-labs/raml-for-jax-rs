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
 * \brief Regular expression term abstraction class.
 *
 * A term is a part of a regular expression which might contain one
 * or more characters, represented by \ref RegExpCharacter objects.
 * An expression is split into terms to honour the different quantifications
 * of parts of the expression. Whenever the quantification changes,
 * a new term is started.
 * Therefore, each term object provides a \ref getMinOccurs and
 * a \ref getMaxOccurs method describing its quantification.
 * Typical occurrence pairs are (0,1) for optional terms, (1,1) for
 * required terms, (0,*) for any count and (1,*) for at least
 * one occurrence.
 * Note that * means unlimited and is expressed by -1.
 *
 * The term object can represent itself in the original (unconstrained)
 * version regexp format via \ref definition.
 * Since constraints might be applied from the surrounding
 * expression object, calling \ref regexpString justifies the
 * quantifiers accordingly, and \ref instanceString returns a string
 * matching this constrained term.
 *
 * @author Josef Spillner <spillner@rn.inf.tu-dresden.de>
 */
public class RegExpTerm extends Base
{
	private ArrayList characters;
	private int minoccurs;
	private int maxoccurs;
	private ArrayList altterms;

	/**
	 * Default constructor.
	 *
	 * Invoking this constructor will result in an invalid term
	 * object. At least \ref setCharacters will have to be called
	 * to make it valid.
	 */
	public RegExpTerm()
	{
		super();

		this.minoccurs = 1;
		this.maxoccurs = 1;
		this.altterms = new ArrayList();
	}

	/**
	 * Sets the characters of this term.
	 *
	 * This method fills out the term with the characters it contains.
	 * Unless the term object is configured further, the character
	 * sequence is supposed to appear exactly once.
	 *
	 * @param characters List of \ref RegExpCharacter objects
	 */
	public void setCharacters(ArrayList characters)
	{
		this.characters = characters;
	}

	/**
	 * Sets the minimum occurrence of the term.
	 *
	 * The occurrence specifies how often a term is (repeatedly)
	 * used for string generation of matching. The minimum
	 * occurrence defines the lower bound for the count.
	 * The default minimum occurrence is 1.
	 * A minimum occurrence of -1 means an infinite count.
	 * The minimum must always be smaller than or equal to
	 * the maximum occurrence.
	 *
	 * @param minoccurs Minimum occurrence for this term
	 *
	 * @see setMaxOccurs
	 */
	public void setMinOccurs(int minoccurs)
	{
		this.minoccurs = minoccurs;
	}

	/**
	 * Sets the maximum occurrence of the term.
	 *
	 * The occurrence specifies how often a term is (repeatedly)
	 * used for string generation of matching. The maximum
	 * occurrence defines the upper bound for the count.
	 * The default maximum occurrence is 1.
	 * A maximum occurrence of -1 means an infinite count.
	 * The maximum must always be larger than or equal to
	 * the minimum occurrence.
	 *
	 * @param maxoccurs Maximum occurrence for this term
	 *
	 * @see setMinOccurs
	 */
	public void setMaxOccurs(int maxoccurs)
	{
		this.maxoccurs = maxoccurs;
	}

	/**
	 * Adds an alternative term.
	 *
	 * A term might be constructed like a union in that only
	 * one of multiple possible terms will be active at any
	 * time in the instance.
	 *
	 * @param alternative Alternative term
	 */
	public void addAlternativeTerm(RegExpTerm alternative)
	{
		this.altterms.add(alternative);
	}

	/**
	 * Returns the previously set minimum occurrence.
	 *
	 * If none has been set yet, returns the default value of 1.
	 *
	 * @return Minimum occurrence for this term
	 */
	public int getMinOccurs()
	{
		return this.minoccurs;
	}

	/**
	 * Returns the previously set maximum occurrence.
	 *
	 * If none has been set yet, returns the default value of 1.
	 *
	 * @return Maximum occurrence for this term
	 */
	public int getMaxOccurs()
	{
		return this.maxoccurs;
	}

	/**
	 * Returns the sequence of characters.
	 *
	 * The sequence is a list of \ref RegExpCharacter objects which
	 * are those contained in the term.
	 * If none have been set yet, \b null is returned instead,
	 * denoting an invalid term.
	 *
	 * @return List of \ref RegExpCharacter objects, or \b null for invalid terms
	 *
	 * @see setCharacters
	 */
	public ArrayList getCharacters()
	{
		return this.characters;
	}

	/**
	 * Returns the list of alternative terms.
	 *
	 * @return List of RegExpTerm objects
	 */
	public ArrayList getAlternativeTerms()
	{
		return this.altterms;
	}

	/**
	 * Returns the length of instances of the term.
	 *
	 * The length specifies how many character objects are contained
	 * within it. While each character object of type \ref RegExpCharacter
	 * might represent one or more real characters, instances thereof
	 * will always have exactly one real character. Therefore, the
	 * result of this method is equal to the string length of
	 * instances of this term.
	 *
	 * @return Length of the string produced by this term
	 *
	 * @see instanceString
	 */
	public int length()
	{
		return this.characters.size();
	}

	/**
	 * Regular expression representation of the term.
	 *
	 * The original regexp term (group) which was used
	 * to build up this object is returned here. It consists
	 * of a sequence of character definitions and a quantification.
	 *
	 * @return A string being the regexp of the term
	 */
	public String definition()
	{
		String term = new String();

		for(int i = 0; i < this.characters.size(); i++)
		{
			RegExpCharacter ch = (RegExpCharacter)this.characters.get(i);
			term += ch.regexpString();
		}

		if(this.altterms.size() > 0)
		{
			for(int i = 0; i < this.altterms.size(); i++)
			{
				term += "|";
				RegExpTerm altterm = (RegExpTerm)this.altterms.get(i);
				for(int j = 0; j < altterm.characters.size(); j++)
				{
					RegExpCharacter ch = (RegExpCharacter)altterm.characters.get(j);
					term += ch.regexpString();
				}
			}
		}

		if(this.characters.size() > 1)
		{
			term = "(" + term + ")";
		}

		if((this.minoccurs == 0) && (this.maxoccurs == 1))
		{
			term += "?";
		}
		else if((this.minoccurs == 0) && (this.maxoccurs == -1))
		{
			term += "*";
		}
		else if((this.minoccurs == 1) && (this.maxoccurs == -1))
		{
			term += "+";
		}
		else if((this.minoccurs != -1) && (this.maxoccurs != -1))
		{
			if(this.minoccurs != this.maxoccurs)
			{
				term += "{" + this.minoccurs + "," + this.maxoccurs + "}";
			}
			else if(this.minoccurs != 1)
			{
				term += "{" + this.minoccurs + "}";
			}
		}

		return term;
	}

	/**
	 * Regular expression representation of the term in one quantification.
	 *
	 * A regexp term (group) describing this object is returned
	 * here. This is not the original regexp, but one fitting the
	 * quantity parameter.
	 * The quantification of the string is given as a parameter,
	 * independent of the real quantification, so that constraints
	 * on the parent \ref RegExpExpression can be expressed.
	 * To retrieve the original regexp definition including the
	 * actual quantification, use \ref definition instead.
	 *
	 * This method discards all alternatives (see addAlternativeTerm).
	 *
	 * @param number Number of repeated generations, overriding the quantification
	 *
	 * @return A string being the regexp of the term
	 *
	 * @see instanceString
	 * @see definition
	 */
	public String regexpString(int number)
	{
		String term = new String();
		String multiterm = new String();

		for(int i = 0; i < this.characters.size(); i++)
		{
			RegExpCharacter ch = (RegExpCharacter)this.characters.get(i);
			term += ch.regexpString();
		}

		for(int j = 0; j < number; j++)
		{
			multiterm += term;
		}

		return multiterm;
	}

	/**
	 * Instance representation of the term in one quantification.
	 *
	 * Each term object, depending on the type of the characters contained
	 * in it, represents a sequence of real characters.
	 * This method creates one valid instance string which will
	 * match the regular expression returned by \ref regexpString.
	 * The quantification of the string is given as a parameter,
	 * independent of the real quantification, so that constraints
	 * on the parent \ref RegExpExpression can be expressed.
	 * To retrieve the original regexp definition including the
	 * actual quantification, use \ref definition instead.
	 *
	 * This method discards all alternatives (see addAlternativeTerm).
	 *
	 * @param number Number of repeated generations, overriding the quantification
	 *
	 * @return A string describing the term instance in a certain quantification
	 *
	 * @see regexpString
	 * @see definition
	 */
	public String instanceString(int number)
	{
		String term = new String();
		String multiterm = new String();

		for(int i = 0; i < this.characters.size(); i++)
		{
			RegExpCharacter ch = (RegExpCharacter)this.characters.get(i);
			term += ch.instanceString();
		}

		for(int j = 0; j < number; j++)
		{
			multiterm += term;
		}

		return multiterm;
	}
}

