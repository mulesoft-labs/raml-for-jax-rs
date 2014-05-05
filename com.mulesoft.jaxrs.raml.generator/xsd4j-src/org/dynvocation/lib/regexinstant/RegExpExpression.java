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
 * \brief Regular expression abstraction class.
 *
 * A whole expression is represented by objects of this class.
 * It might be broken up into several terms (see \ref RegExpTerm),
 * which in turn consist of characters (\ref RegExpCharacter) and
 * quantification information.
 * The sequence of terms in it can be retrieved using \ref getTerms.
 *
 * Expressions might be constrained or unconstrained. The method
 * \ref isConstrained informs about whether or not a \ref RegExpConstraint
 * was applied to the original expression, yielding a copy which shares
 * the terms with the original.
 *
 * The expression object can represent itself in the original
 * (unconstrained) version regexp format via \ref definition.
 * Since constraints might be applied to it,
 * calling \ref regexpString justifies the
 * quantifiers of the contained terms accordingly, and \ref
 * instanceString returns a string matching this constrained expression.
 *
 * @author Josef Spillner <spillner@rn.inf.tu-dresden.de>
 */
public class RegExpExpression extends Base
{
	// List of RegExpTerm objects
	private ArrayList terms;
	// Internal constraints table
	private int[] counts;
	private boolean constrained;

	/**
	 * Default constructor.
	 *
	 * Returns an invalid regexp object since no terms
	 * are in it yet. A call to \ref setTerms is the minimum
	 * which must be done in order to change this.
	 */
	public RegExpExpression()
	{
		super();

		this.constrained = false;
	}

	/**
	 * Sets the terms contained in this expression.
	 *
	 * Each regular expression consists of multiple terms.
	 * The sequence of terms is given here to fill out
	 * the expression.
	 *
	 * @param terms List of \ref RegExpTerm objects
	 *
	 * @see getTerms
	 */
	public void setTerms(ArrayList terms)
	{
		this.terms = terms;
		this.counts = new int[terms.size()];
		for(int i = 0; i < terms.size(); i++)
		{
			this.counts[i] = 1;
		}
	}

	/**
	 * Returns the previously set sequence of terms.
	 *
	 * The sequence of terms within this expression is returned
	 * here. In the case of invalid expressions, \b null
	 * is returned instead.
	 *
	 * @return Sequence of \ref RegExpTerm objects, or \b null
	 *
	 * @see setTerms
	 */
	public ArrayList getTerms()
	{
		return this.terms;
	}

	/**
	 * Informs whether or not this regexp is constrained.
	 *
	 * Constraining a regular expression object is done
	 * via \ref RegExpConstraint object by \ref
	 * RegExpInstantiator::constrain.
	 * Constrained regexps are shallow copies of their unconstrained
	 * originals - modifying one will modify the other, probably
	 * going unnoticed!
	 *
	 * @return Whether or not this regexp is constrained
	 */
	public boolean isConstrained()
	{
		return this.constrained;
	}

	/**
	 * Regular expression representation of the regexp.
	 *
	 * The original regular expression which was used
	 * to build up this object is returned here. It consists
	 * of a sequence of term definitions including their quantifications.
	 *
	 * @return A string representing this regular expression
	 */
	public String definition()
	{
		if(this.terms == null)
		{
			return null;
		}

		String expr = new String();

		for(int i = 0; i < this.terms.size(); i++)
		{
			RegExpTerm term = (RegExpTerm)this.terms.get(i);
			expr += term.definition();
		}

		return expr;
	}

	/**
	 * Representation of the regexp.
	 *
	 * A representation of this regexp is returned.
	 * Depending on the set of constraints applied to this object,
	 * the string might contain each term once or a varying number
	 * of times, ranging from zero to any finite number.
	 *
	 * @return A string describing this regexp
	 *
	 * @see instanceString
	 * @see definition
	 * @see isConstrained
	 */
	public String regexpString()
	{
		if(this.terms == null)
		{
			return null;
		}
		if(this.terms.size() != this.counts.length)
		{
			System.out.println("regexp error: dirty constraint state");
			return null;
		}

		String expr = new String();

		for(int i = 0; i < this.terms.size(); i++)
		{
			RegExpTerm term = (RegExpTerm)this.terms.get(i);
			expr += term.regexpString(this.counts[i]);
		}

		return expr;
	}

	/**
	 * Instance representation of the regexp.
	 *
	 * This method produces a string which matches the regexp
	 * as represented by this regular expression object.
	 * Depending on the set of constraints applied to this object,
	 * the string might be a canonical instance or it might reach
	 * out to the minimum or maximum string length of all
	 * possible instantiations.
	 *
	 * @return A string describing a valid instance of this regexp
	 *
	 * @see regexpString
	 * @see definition
	 * @see isConstrained
	 */
	public String instanceString()
	{
		if(this.terms == null)
		{
			return null;
		}
		if(this.terms.size() != this.counts.length)
		{
			System.out.println("regexp error: dirty constraint state");
			return null;
		}

		String expr = new String();

		for(int i = 0; i < this.terms.size(); i++)
		{
			RegExpTerm term = (RegExpTerm)this.terms.get(i);
			expr += term.instanceString(this.counts[i]);
		}

		return expr;
	}

	/**
	 * Constrains the regexp.
	 *
	 * This method is supposed to be called only from
	 * \ref RegExpInstantiator::constrain.
	 * Do not use, it might lead to inconsistencies.
	 *
	 * @param counts Constraints table
	 *
	 * @see isConstrained
	 *
	 * @internal
	 */
	public void constrain(int[] counts)
	{
		this.counts = counts;
		this.constrained = true;
	}
}

