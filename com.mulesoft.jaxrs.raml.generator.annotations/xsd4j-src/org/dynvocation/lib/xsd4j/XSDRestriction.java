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

/**
 * \brief Class corresponding to a type restriction in an XML Schema.
 *
 * This class deals with restrictions on simple types, such as the minimum
 * and maximum values for numeric types, or allowed characters for string
 * types.
 *
 * @author Josef Spillner <spillner@rn.inf.tu-dresden.de>
 */
public class XSDRestriction
{
	// A mask with bits set for active restrictions
	// See RESTRICTION_* constants below
	private BitSet restrictions;
	// List of enumerations
	private ArrayList enumerations;
	// Strings (not yet evaluated) containing restriction values...
	// FIXME: convert more types to native
	private BigInteger mininclusive;
	private BigInteger maxinclusive;
	private BigInteger minexclusive;
	private BigInteger maxexclusive;
	private BigInteger totaldigits;
	private BigInteger fractiondigits;
	private String pattern;
	private BigInteger length;
	private BigInteger minlength;
	private BigInteger maxlength;
	private int whitespace;
	// Annotation tag, if any
	private XSDAnnotation annotation;

	// All of the restriction types
	public static final int RESTRICTION_MIN_INCLUSIVE = 1;
	public static final int RESTRICTION_MAX_INCLUSIVE = 2;
	public static final int RESTRICTION_MIN_EXCLUSIVE = 3;
	public static final int RESTRICTION_MAX_EXCLUSIVE = 4;
	public static final int RESTRICTION_TOTAL_DIGITS = 5;
	public static final int RESTRICTION_FRACTION_DIGITS = 6;
	public static final int RESTRICTION_PATTERN = 7;
	public static final int RESTRICTION_LENGTH = 8;
	public static final int RESTRICTION_MIN_LENGTH = 9;
	public static final int RESTRICTION_MAX_LENGTH = 10;
	public static final int RESTRICTION_WHITE_SPACE = 11;
	public static final int RESTRICTION_ENUMERATION = 20;

	// Maximum restriction type constant value
	public static final int MAX_RESTRICTIONS = 32;

	// Whitespace values
	public static final int WHITESPACE_COLLAPSE = 1001;
	public static final int WHITESPACE_COLLATE = 1002;
	public static final int WHITESPACE_REPLACE = 1003;
	public static final int WHITESPACE_PRESERVE = 1004;

	/**
	 * Default constructor.
	 *
	 * Returns a restriction object which does not contain any actual restrictions.
	 */
	public XSDRestriction()
	{
		this.restrictions = new BitSet();
	}

	/**
	 * Sets the minimum numeric value of the element in question.
	 *
	 * This restriction is only allowed for numeric types.
	 *
	 * @param value Minimum value of the type in question
	 */
	public void setMinInclusive(BigInteger value)
	{
		this.mininclusive = value;
		this.restrictions.set(RESTRICTION_MIN_INCLUSIVE);
	}

	/**
	 * Sets the maximum numeric value of the element in question.
	 *
	 * This restriction is only allowed for numeric types.
	 *
	 * @param value Maximum value of the type in question
	 */
	public void setMaxInclusive(BigInteger value)
	{
		this.maxinclusive = value;
		this.restrictions.set(RESTRICTION_MAX_INCLUSIVE);
	}

	/**
	 * Sets the lower border value of the element in question.
	 *
	 * This restriction is only allowed for numeric types.
	 * It corresponds to the minimum value minus one.
	 *
	 * @param value Lower border of the type in question
	 */
	public void setMinExclusive(BigInteger value)
	{
		this.minexclusive = value;
		this.restrictions.set(RESTRICTION_MIN_EXCLUSIVE);
	}

	/**
	 * Sets the upper boarder value of the element in question.
	 *
	 * This restriction is only allowed for numeric types.
	 * It corresponds to the maximum value plus one.
	 *
	 * @param value Upper boarder of the type in question
	 */
	public void setMaxExclusive(BigInteger value)
	{
		this.maxexclusive = value;
		this.restrictions.set(RESTRICTION_MAX_EXCLUSIVE);
	}

	/**
	 * Sets the maximum number of total digits of the element in question.
	 *
	 * This restriction is only allowed for numeric types.
	 *
	 * @param value Maximum number of digits the element may have
	 */
	public void setTotalDigits(BigInteger value)
	{
		this.totaldigits = value;
		this.restrictions.set(RESTRICTION_TOTAL_DIGITS);
	}

	/**
	 * Sets the maximum number of fraction digits of the element in question.
	 *
	 * This restriction is only allowed for numeric types.
	 *
	 * @param value Maximum number of fraction digits the element may have
	 */
	public void setFractionDigits(BigInteger value)
	{
		this.fractiondigits = value;
		this.restrictions.set(RESTRICTION_FRACTION_DIGITS);
	}

	/**
	 * Sets the only allowed value pattern of the element in question.
	 *
	 * This restriction is only allowed for string types.
	 *
	 * @param value Pattern which the value of this element must match
	 */
	public void setPattern(String value)
	{
		this.pattern = value;
		this.restrictions.set(RESTRICTION_PATTERN);
	}

	/**
	 * Sets the exact length of the element in question.
	 *
	 * This restriction is only allowed for string types.
	 *
	 * @param value Length of which the value of the element must be
	 */
	public void setLength(BigInteger value)
	{
		this.length = value;
		this.restrictions.set(RESTRICTION_LENGTH);
	}

	/**
	 * Sets the minimum length of the element in question.
	 *
	 * This restriction is only allowed for string types.
	 *
	 * @param value Minimum length of which the value of the element must be
	 */
	public void setMinLength(BigInteger value)
	{
		this.minlength = value;
		this.restrictions.set(RESTRICTION_MIN_LENGTH);
	}

	/**
	 * Sets the maximum length of the element in question.
	 *
	 * This restriction is only allowed for string types.
	 *
	 * @param value Maximum length of which the value of the element must be
	 */
	public void setMaxLength(BigInteger value)
	{
		this.maxlength = value;
		this.restrictions.set(RESTRICTION_MAX_LENGTH);
	}

	/**
	 * Sets the whitespace policy of the element in question.
	 *
	 * This restriction is only allowed for string types.
	 * It may be of the form \ref WHITESPACE_COLLATE or
	 * \ref WHITESPACE_COLLAPSE or \ref WHITESPACE_REPLACE
	 * or \ref WHITESPACE_PRESERVE.
	 *
	 * @param value Whitespace policy value for the element in question
	 */
	public void setWhiteSpace(int value)
	{
		if((value != WHITESPACE_COLLATE)
		&& (value != WHITESPACE_COLLAPSE)
		&& (value != WHITESPACE_REPLACE)
		&& (value != WHITESPACE_PRESERVE))
			return;

		this.whitespace = value;
		this.restrictions.set(RESTRICTION_WHITE_SPACE);
	}

	/**
	 * Adds an enumeration value to the element.
	 *
	 * Elements which are enumerated may only have values which correspond
	 * to one of the enumerations.
	 *
	 * @param value Enumeration value to add to the list of values
	 */
	public void addEnumeration(String value)
	{
		if(this.enumerations == null)
		{
			this.enumerations = new ArrayList();
		}
		this.enumerations.add(value);
		this.restrictions.set(RESTRICTION_ENUMERATION);
	}

	/**
	 * Sets the annotation for this restriction.
	 *
	 * Restrictions can carry some documentation in form of an annotation
	 * tag. This is unrelated to the resulting XML instance, but might
	 * be useful to processing software, such as WSGUI engines.
	 *
	 * @param annotation The annotation object
	 */
	public void setAnnotation(XSDAnnotation annotation)
	{
		this.annotation = annotation;
	}

	/**
	 * Returns the mask of restrictions on the type of the element.
	 *
	 * This method returns a bitmask, containing a number of bits which may
	 * either be set (in case the specific restriction is active) or
	 * zero (in case the restriction does not apply).
	 *
	 * @return Bitmask containing indicators for all the restrictions
	 */
	public BitSet getRestrictions()
	{
		return this.restrictions;
	}

	/**
	 * Returns the minimum number an element of this type may have.
	 *
	 * @return Minimum number
	 */
	public BigInteger getMinInclusive()
	{
		return this.mininclusive;
	}

	/**
	 * Returns the maximum number an element of this type may have.
	 *
	 * @return Maximum number
	 */
	public BigInteger getMaxInclusive()
	{
		return this.maxinclusive;
	}

	/**
	 * Returns the highest number an element of this type might not have.
	 *
	 * @return Lower bound
	 */
	public BigInteger getMinExclusive()
	{
		return this.minexclusive;
	}

	/**
	 * Returns the lowest number an element of this type might not have.
	 *
	 * @return Upper bound
	 */
	public BigInteger getMaxExclusive()
	{
		return this.maxexclusive;
	}

	/**
	 * Returns the maximum number of digits for this element.
	 *
	 * @return Maximum number of total digits
	 */
	public BigInteger getTotalDigits()
	{
		return this.totaldigits;
	}

	/**
	 * Returns the maximum number of fraction digits for this element.
	 *
	 * @return Maximum number of fraction digits
	 */
	public BigInteger getFractionDigits()
	{
		return this.fractiondigits;
	}

	/**
	 * Returns the pattern to which the value of this element must obey.
	 *
	 * @return Pattern which restricts the value
	 */
	public String getPattern()
	{
		return this.pattern;
	}

	/**
	 * Returns the exact length to which the value of this element must obey.
	 *
	 * @return Length of the value
	 */
	public BigInteger getLength()
	{
		return this.length;
	}

	/**
	 * Returns the minimum length to which the value of this element must obey.
	 *
	 * @return Minimum length of the value
	 */
	public BigInteger getMinLength()
	{
		return this.minlength;
	}

	/**
	 * Returns the maximum length to which the value of this element must obey.
	 *
	 * @return Maximum length of the value
	 */
	public BigInteger getMaxLength()
	{
		return this.maxlength;
	}

	/**
	 * Returns the whitespace policy which applies to the element.
	 *
	 * @return Whitespace policy identifier
	 */
	public int getWhiteSpace()
	{
		return this.whitespace;
	}

	/**
	 * Returns all the enumerations of the element.
	 *
	 * @return List of enumerations, or \b null if no enumeration values exist
	 */
	public ArrayList getEnumerations()
	{
		return this.enumerations;
	}

	/**
	 * Returns the annotation attached to this restriction.
	 *
	 * @return Annotation, which is usually \b null
	 */
	public XSDAnnotation getAnnotation()
	{
		return this.annotation;
	}
}

