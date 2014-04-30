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
 * \brief Character abstraction class.
 *
 * A character is the atomic type of each regular expression. It might
 * refer to more than one real character, but only one of those will
 * match or be used for creating an instance.
 *
 * The three types are real characters (\ref CHARACTER_CHARACTER),
 * symbolic character classes (\ref CHARACTER_SYMBOL) and
 * sets of characters and character ranges (\ref CHARACTER_SET).
 * Depending on which type is given to \ref setType, the next call
 * would be \ref setCharacter, \ref setSymbol or several calls of
 * \ref addSet.
 * For convenience reasons, the methods \ref parse and \ref parseSet
 * are available and are used by the parser of this library.
 *
 * The character object can represent itself in the original
 * regexp format via \ref regexpString, or as an instance matching
 * the regexp via \ref instanceString.
 *
 * @author Josef Spillner <spillner@rn.inf.tu-dresden.de>
 */
public class RegExpCharacter extends Base
{
	/** The character represents a real character */
	public static final int CHARACTER_CHARACTER = 1;
	/** The character represents a symbol referring to a character class, like \\d */
	public static final int CHARACTER_SYMBOL = 2;
	/** The character represents an arbitrary character set, like [0-9a-f] */
	public static final int CHARACTER_SET = 3;
	/** The character represents an intersection of two character sets, like [\\d-[:]]. */
	public static final int CHARACTER_INTERSECTION = 4;
	/** The character represents a nested expression, like (ab*)+ */
	public static final int CHARACTER_EXPRESSION = 5;

	/** The character class contains digit characters (\\d) */
	public static final int SYMBOL_DIGIT = 101;
	/** The character class contains non-digit characters (\\D) */
	public static final int SYMBOL_NON_DIGIT = 102;
	/** The character class contains word characters (\\w) */
	public static final int SYMBOL_WORD = 103;
	/** The character class contains non-word characters (\\W) */
	public static final int SYMBOL_NON_WORD = 104;
	/** The character class contains whitespace characters (\\s) */
	public static final int SYMBOL_WHITESPACE = 105;
	/** The character class contains non-whitespace characters (\\S) */
	public static final int SYMBOL_NON_WHITESPACE = 106;

	/** The XSD character class contains initial characters (\\i) */
	public static final int SYMBOL_XSD_INITIALCHAR = 120;
	/** The XSD character class contains characters (\\c) */
	public static final int SYMBOL_XSD_CHAR = 121;

	// Number of SYMBOL_* constants above, for efficiency reasons
	private static final int MAX_SYMBOLS = 8;

	// The type of character - one of the CHARACTER_* constants above
	private int type;
	// A list of char[] sets for CHARACTER_SET characters
	private ArrayList sets;
	// A single character representing the CHARACTER_CHARACTER content
	private char ascii;
	// The symbolic character class - one of the SYMBOL_* constants above
	// For CHARACTER_INTERSECTION, both a symbol and one set are used
	private int symbol;
	// The nested term for CHARACTER_EXPRESSION expressions
	private RegExpExpression expression;

	// Mapping between symbol constants and their characters
	private int[] symbolvals;
	private char[] symbolkeys;
	private int maxsymbols;

	/**
	 * Default constructor.
	 * 
	 * It will create a character object which is invalid until at
	 * least \ref setType and one associated method have been called.
	 */
	public RegExpCharacter()
	{
		super();

		this.type = 0;
		this.symbol = 0;
		this.sets = null;
		setupSymbolMap();
	}

	/**
	 * Sets the type of this character.
	 *
	 * This method will always have to be used to specify what kind of
	 * character this is. Note that calling \ref parse or \ref parseSet
	 * will do this automatically.
	 *
	 * @param type One of the CHARACTER_* constants describing the type
	 *
	 * @see getType
	 * @see parse
	 * @see parseSet
	 */
	public void setType(int type)
	{
		if((type == CHARACTER_CHARACTER)
		|| (type == CHARACTER_SET)
		|| (type == CHARACTER_SYMBOL)
		|| (type == CHARACTER_INTERSECTION)
		|| (type == CHARACTER_EXPRESSION))
		{
			this.type = type;
		}
		else
		{
			System.out.println("character error: wrong type set");
		}

		if(type == CHARACTER_SET)
		{
			this.sets = new ArrayList();
		}
	}

	/**
	 * Returns the previously set type.
	 *
	 * If no type has been set
	 * yet, the character object is still invalid and the type will
	 * not match any of the CHARACTER_* constants.
	 *
	 * @return The type of this character
	 *
	 * @see setType
	 */
	public int getType()
	{
		return this.type;
	}

	/**
	 * Specifies the represented character.
	 *
	 * This method is to be used to specify which real character is
	 * actually represented by this character object, whenever
	 * the type is \ref CHARACTER_CHARACTER.
	 * It does't need to be used if \ref parse was used instead.
	 *
	 * @param character The real character represented by this object
	 *
	 * @see parse
	 * @see getCharacter
	 */
	public void setCharacter(char character)
	{
		if(this.type == CHARACTER_CHARACTER)
		{
			this.ascii = character;
		}
		else
		{
			System.out.println("character error: type is not character");
		}
	}

	/**
	 * Returns the previously set character.
	 *
	 * If the type of this object is \ref CHARACTER_CHARACTER and a
	 * character has been assigned with \ref setCharacter or \ref
	 * parse, then it returns the character represented by this object.
	 * Otherwise, a random invalid character is returned.
	 *
	 * @return The real character represented by this object
	 *
	 * @see setCharacter
	 */
	public char getCharacter()
	{
		return this.ascii;
	}

	/**
	 * Specifies one more represented character set.
	 *
	 * Whenever the type of the object is CHARACTER_SET, repetitive
	 * calls of this method can add character sets (ranges) to
	 * the set of characters represented by this object.
	 * It does't need to be used if \ref parseSet was used instead.
	 *
	 * If the type is CHARACTER_INTERSECTION, then exactly one set
	 * is added here, which is the set of characters prohibited from
	 * a symbolic set available in \ref getSymbol at the same time.
	 *
	 * @param set An array of characters to be added to the set
	 *
	 * @see parseSet
	 * @see getSets
	 */
	public void addSet(char[] set)
	{
		if(this.type == CHARACTER_SET)
		{
			this.sets.add(set);
		}
		else
		{
			System.out.println("character error: type is not set");
		}
	}

	/**
	 * Returns all sets which were added to this character.
	 *
	 * If the type of this object is \ref CHARACTER_SET and sets
	 * of characters have been assigned with \ref addSet or \ref
	 * parseSet, then it returns a list of the ranges (as arrays)
	 * of characters represented by this object.
	 * Otherwise, a \b null list or \b empty list is returned
	 * instead.
	 *
	 * @return The real character represented by this object
	 *
	 * @see setCharacter
	 */
	public ArrayList getSets()
	{
		return this.sets;
	}

	/**
	 * Specifies the represented character class as a symbol.
	 *
	 * For character classes, various SYMBOL_* constants are available,
	 * each of them representing a certain class (set) of characters
	 * which are so common that they can be specified with a symbolic
	 * value within regular expressions.
	 * Whenever \ref CHARACTER_SYMBOL is the type of this object,
	 * or \ref CHARACTER_INTERSECTION, this method must be called.
	 * It does't need to be used if \ref parse was used instead.
	 *
	 * @param symbol The symbol constant referring to a character class
	 *
	 * @see parse
	 * @see getSymbol
	 */
	public void setSymbol(int symbol)
	{
		if(this.type == CHARACTER_SYMBOL)
		{
			if(symbolToChar(symbol) != ' ')
			{
				this.symbol = symbol;
			}
			else
			{
				System.out.println("character error: unknown symbol");
			}
		}
		else
		{
			System.out.println("character error: type is not symbol");
		}
	}

	/**
	 * Returns the previously set symbol.
	 *
	 * If the type of this object is \ref CHARACTER_SYMBOL and a
	 * character class has been assigned with \ref setSymbol or \ref
	 * parse, then it returns the symbolic character class
	 * represented by this object.
	 * Otherwise, the character is invalid and the symbol will
	 * not match any of the SYMBOL_* constants.
	 *
	 * @return The symbol constant referring to a character class
	 *
	 * @see setSymbol
	 */
	public int getSymbol()
	{
		return this.symbol;
	}

	/**
	 * Specifies the represented expression.
	 *
	 * All characters of type \ref CHARACTER_EXPRESSION represent a nested
	 * expression which in turn contains one or more terms with characters.
	 *
	 * @param expression The expression represented by this object
	 *
	 * @see getExpression
	 */
	public void setExpression(RegExpExpression expression)
	{
		if(this.type == CHARACTER_EXPRESSION)
		{
			this.expression = expression;
		}
		else
		{
			System.out.println("character error: type is not term");
		}
	}

	/**
	 * Returns the previously set term.
	 *
	 * If the type of this object is \ref CHARACTER_EXPRESSION and a
	 * expression has been assigned with \ref setExpression,
	 * then it returns the expression represented by this object.
	 * Otherwise, \b null is returned.
	 *
	 * @return The expression represented by this object
	 *
	 * @see setExpression
	 */
	public RegExpExpression getExpression()
	{
		return this.expression;
	}

	/**
	 * Instance representation of the character.
	 *
	 * Each character object, depending on its type, represents one
	 * or more real characters. Any of those can be used to
	 * match the regexp, which can be accessed using \ref regexpString.
	 * Therefore, this method will simply return one out of the
	 * possibly many real characters represented by this character object.
	 *
	 * @return A string of length 1 containing a valid real character
	 *
	 * @see regexpString
	 */
	public String instanceString()
	{
		if(getType() == RegExpCharacter.CHARACTER_SYMBOL)
		{
			int sym = getSymbol();

			if(sym == SYMBOL_DIGIT) return "0";
			if(sym == SYMBOL_NON_DIGIT) return "a";
			if(sym == SYMBOL_WORD) return "a";
			if(sym == SYMBOL_NON_WORD) return "/";
			if(sym == SYMBOL_WHITESPACE) return " ";
			if(sym == SYMBOL_NON_WHITESPACE) return ".";
			if(sym == SYMBOL_XSD_INITIALCHAR) return "a";
			if(sym == SYMBOL_XSD_CHAR) return "a";
		}
		else if(getType() == RegExpCharacter.CHARACTER_CHARACTER)
		{
			String ret = new String();
			ret += getCharacter();
			return ret;
		}
		else if(getType() == RegExpCharacter.CHARACTER_SET)
		{
			if(this.sets.size() > 0)
			{
				char[] chars = (char[])this.sets.get(0);
				String ret = new String();
				ret += chars[0];
				return ret;
			}
		}
		else if(getType() == RegExpCharacter.CHARACTER_INTERSECTION)
		{
			// FIXME: ???
			return "a";
		}
		else if(getType() == RegExpCharacter.CHARACTER_EXPRESSION)
		{
			RegExpExpression ree = getExpression();
			return ree.instanceString();
		}

		System.out.println("character error: instance conversion failed");
		return new String();
	}

	private String regexpstring_symbol(int symbol)
	{
		return "\\" + symbolToChar(getSymbol());
	}

	private String regexpstring_character(char c)
	{
		String ret = new String();
		ret += c;
		return ret;
	}

	private String regexpstring_set(ArrayList sets)
	{
		String ret = new String();
		ret += "[";
		for(int i = 0; i < sets.size(); i++)
		{
			char[] chars = (char[])sets.get(i);
			if(chars.length == 1)
			{
				if(chars[0] == '-')
					ret += '\\';
				ret += chars[0];
			}
			else
			{
				ret += chars[0] + "-" + chars[chars.length - 1];
			}
		}
		ret += "]";
		return ret;
	}

	/**
	 * Regular expression representation of the character.
	 *
	 * The original regexp character or character range which was used
	 * to build up this object is returned here.
	 *
	 * @return A string being the regexp of the represented character(s)
	 *
	 * @see instanceString
	 */
	public String regexpString()
	{
		if(getType() == RegExpCharacter.CHARACTER_SYMBOL)
		{
			return regexpstring_symbol(getSymbol());
		}
		else if(getType() == RegExpCharacter.CHARACTER_CHARACTER)
		{
			return regexpstring_character(getCharacter());
		}
		else if(getType() == RegExpCharacter.CHARACTER_SET)
		{
			return regexpstring_set(this.sets);
		}
		else if(getType() == RegExpCharacter.CHARACTER_INTERSECTION)
		{
			String a = regexpstring_symbol(getSymbol());
			String b = regexpstring_set(this.sets);
			String ab = "[" + a + "-" + b + "]";
			return ab;
		}
		else if(getType() == RegExpCharacter.CHARACTER_EXPRESSION)
		{
			RegExpExpression ree = getExpression();
			return "(" + ree.regexpString() + ")";
		}

		System.out.println("character error: conversion failed");
		return new String();
	}

	// Put one more symbol into the symbol constant<->symbol character mapping
	private void populateMap(char c, int symbol)
	{
		if(this.maxsymbols >= RegExpCharacter.MAX_SYMBOLS)
		{
			System.out.println("character error: too many symbols");
		}

		symbolkeys[this.maxsymbols] = c;
		symbolvals[this.maxsymbols] = symbol;

		this.maxsymbols += 1;
	}

	// Register all symbolic constants with their representation in regexps
	private void setupSymbolMap()
	{
		this.maxsymbols = 0;

		this.symbolvals = new int[MAX_SYMBOLS];
		this.symbolkeys = new char[MAX_SYMBOLS];

		populateMap('d', RegExpCharacter.SYMBOL_DIGIT);
		populateMap('D', RegExpCharacter.SYMBOL_NON_DIGIT);
		populateMap('w', RegExpCharacter.SYMBOL_WORD);
		populateMap('W', RegExpCharacter.SYMBOL_NON_WORD);
		populateMap('s', RegExpCharacter.SYMBOL_WHITESPACE);
		populateMap('S', RegExpCharacter.SYMBOL_NON_WHITESPACE);
		populateMap('c', RegExpCharacter.SYMBOL_XSD_CHAR);
		populateMap('i', RegExpCharacter.SYMBOL_XSD_INITIALCHAR);
	}

	// Returns the character for a symbol constant (without the backslash)
	private char symbolToChar(int symbol)
	{
		for(int i = 0; i < MAX_SYMBOLS; i++)
		{
			if(this.symbolvals[i] == symbol)
			{
				return this.symbolkeys[i];
			}
		}

		return ' ';
	}

	// Returns the symbol constant for a character without the backslash
	private int charToSymbol(char c)
	{
		for(int i = 0; i < MAX_SYMBOLS; i++)
		{
			if(this.symbolkeys[i] == c)
			{
				return this.symbolvals[i];
			}
		}

		return -1;
	}

	/**
	 * Configures this object according to a single real character.
	 *
	 * The parser uses this method for both \ref CHARACTER_CHARACTER
	 * and \ref CHARACTER_SYMBOL objects: It finds out the type
	 * automatically from a real character and how it appeared in
	 * the parsed regular expression. If a character was quoted and
	 * it matches one of the SYMBOL_* constants, this object becomes
	 * a symbolic character class object, otherwise it will represent
	 * a single real character.
	 *
	 * @param c The real character which is to be parsed
	 * @param quoted Whether or not the real character appeared quoted
	 *
	 * @see setCharacter
	 * @see setSymbol
	 */
	public void parse(char c, boolean quoted)
	{
		int probesymbol = charToSymbol(c);

		if((quoted) && (probesymbol != -1))
		{
			setType(RegExpCharacter.CHARACTER_SYMBOL);
			setSymbol(probesymbol);
		}
		else
		{
			setType(RegExpCharacter.CHARACTER_CHARACTER);
			setCharacter(c);
		}
	}

	private void parseintersection(String intersect)
	{
		int sep = intersect.indexOf("-");
		if(sep == -1)
		{
			System.out.println("intersection error: no separator found");
			return;
		}

		String a = intersect.substring(0, sep);
		String b = intersect.substring(sep + 1, intersect.length());
		b = b.substring(1, b.length() - 1);

		parse(a.charAt(a.length() - 1), true);
		parseSet(b);

		setType(RegExpCharacter.CHARACTER_INTERSECTION);
	}

	/**
	 * Configures this object according to a character set.
	 *
	 * Whenever the parser encounters a set definition, that is,
	 * one or more single characters and character ranges, the whole
	 * definition is parsed here.
	 * Consequently, this object is configured to be of type
	 * \ref CHARACTER_SET.
	 *
	 * @param set The entire character set definition
	 *
	 * @see addSet
	 */
	public void parseSet(String set)
	{
		if(set.charAt(set.length() - 1) == ']')
		{
			parseintersection(set);
			return;
		}

		setType(RegExpCharacter.CHARACTER_SET);

		debug("parseSet...");
		String ds = new String();
		for(int i = 0; i < set.length(); i++)
		{
			ds += set.charAt(i);
		}
		debug("- set: '" + ds + "'");

		// FIXME: handle quoting, e.g. in '\-+'

		for(int i = 0; i < set.length(); i++)
		{
			char first = set.charAt(i);
			if(first == '\\')
			{
				i++;
				first = set.charAt(i);
			}

			char range = ' ';
			char second = ' ';
			if(i < set.length() - 2)
			{
				range = set.charAt(i + 1);
				second = set.charAt(i + 2);
				if(second == '\\')
				{
					if(i < set.length() - 3)
					{
						i++;
						second = set.charAt(i + 2);
					}
				}
			}

			if(range == '-')
			{
				int diff = second - first + 1;
				char chars[] = new char[diff];
				for(int j = 0; j < diff; j++)
				{
					chars[j] = (char)(first + j);
				}
				addSet(chars);
				i += 2;
			}
			else
			{
				char chars[] = new char[1];
				chars[0] = first;
				addSet(chars);
			}
		}

		debug("- parsed set: " + regexpstring_set(this.sets));
	}
}

