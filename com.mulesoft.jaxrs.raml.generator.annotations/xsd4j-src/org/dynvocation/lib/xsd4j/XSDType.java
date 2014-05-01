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

import javax.xml.namespace.*;

/**
 * \brief Class describing an XML Schema type.
 *
 * Each element and attribute is assigned a type. In both cases, the type
 * may be a simple one, but it can also be a complex one for elements,
 * which means that its sequence contains child elements with other types.
 * In all cases, simple types may be restricted by several facets.
 *
 * @author Josef Spillner <spillner@rn.inf.tu-dresden.de>
 */
public class XSDType
{
	// represented type (one of TYPE_* below)
	private int type;
	// the type's name
	private QName name;
	// the type's base type reference, leading to 'basetype'
	// used by restrictions, lists, complex types with simple content, ...
	private QName baseref;
	// the type's member references, leading to 'members'
	// used by union types only
	private ArrayList memberrefs;
	// sequence of elements (and groups), for complex types and groups
	private XSDSequence sequence;
	// set of facets, for simple types
	private XSDRestriction restriction;
	// the parent type - might be wrong unless detached!
	private XSDElement parent;
	// list of attributes (and attribute groups), for complex types
	// top-level attributes are directly in XSDSequence (via XSDSchema)
	private ArrayList attributes;
	// member types, see 'memberrefs'
	private ArrayList members;
	// base type, see 'baseref'
	private XSDType basetype;
	// choice element, for elements in complex types
	private XSDChoice choice;
	// if the type is restricted instead of extended, for complex types
	private boolean restricted;
	// if the type accepts mixed content, for complex types
	private boolean mixed;
	// annotation object, if any
	private XSDAnnotation annotation;

	/** Symbolic value to denote an invalid type. */
	public static final int TYPE_INVALID = -1;
	/** Symbolic value to denote a complex type. */
	public static final int TYPE_COMPLEX = 0;
	/** Symbolic value to denote a simple derived type. */
	public static final int TYPE_DERIVED = 1;
	/** Symbolic value to denote any type (the default). */
	public static final int TYPE_ANY_TYPE = 2;
	/** Symbolic value to denote any simple type. */
	public static final int TYPE_ANY_SIMPLE_TYPE = 3;

	/** Symbolic value to denote a list of simple types. */
	public static final int TYPE_LIST = 4;
	/** Symbolic value to denote a union of simple types. */
	public static final int TYPE_UNION = 5;
	/** Symbolic value to denote a choice pseudo-type. */
	public static final int TYPE_CHOICE = 6;
	/** Symbolic value to denote an attribute group pseudo-type. */
	public static final int TYPE_ATTRIBUTEGROUP = 7;
	/** Symbolic value to denote an element group pseudo-type. */
	public static final int TYPE_GROUP = 8;

	public static final int TYPE_STRING = 10;
	public static final int TYPE_NORMALIZED_STRING = 11;
	public static final int TYPE_TOKEN = 12;
	public static final int TYPE_BASE64_BINARY = 13;
	public static final int TYPE_HEX_BINARY = 14;

	public static final int TYPE_INTEGER = 20;
	public static final int TYPE_POSITIVE_INTEGER = 21;
	public static final int TYPE_NEGATIVE_INTEGER = 22;
	public static final int TYPE_NON_NEGATIVE_INTEGER = 23;
	public static final int TYPE_NON_POSITIVE_INTEGER = 24;
	public static final int TYPE_LONG = 25;
	public static final int TYPE_UNSIGNED_LONG = 26;
	public static final int TYPE_INT = 27;
	public static final int TYPE_UNSIGNED_INT = 28;
	public static final int TYPE_SHORT = 29;
	public static final int TYPE_UNSIGNED_SHORT = 30;
	public static final int TYPE_BYTE = 31;
	public static final int TYPE_UNSIGNED_BYTE = 32;

	public static final int TYPE_DECIMAL = 40;
	public static final int TYPE_FLOAT = 41;
	public static final int TYPE_DOUBLE = 42;

	public static final int TYPE_BOOLEAN = 50;

	public static final int TYPE_DURATION = 60;
	public static final int TYPE_DATE_TIME = 61;
	public static final int TYPE_DATE = 62;
	public static final int TYPE_TIME = 63;
	public static final int TYPE_G_YEAR = 64;
	public static final int TYPE_G_YEAR_MONTH = 65;
	public static final int TYPE_G_MONTH = 66;
	public static final int TYPE_G_MONTH_DAY = 67;
	public static final int TYPE_G_DAY = 68;

	public static final int TYPE_NAME = 70;
	public static final int TYPE_QNAME = 71;
	public static final int TYPE_NCNAME = 72;
	public static final int TYPE_ANY_URI = 73;
	public static final int TYPE_LANGUAGE = 74;

	public static final int TYPE_ID = 80;
	public static final int TYPE_IDREF = 81;
	public static final int TYPE_IDREFS = 82;
	public static final int TYPE_ENTITY = 83;
	public static final int TYPE_ENTITIES = 84;
	public static final int TYPE_NOTATION = 85;
	public static final int TYPE_NMTOKEN = 86;
	public static final int TYPE_NMTOKENS = 87;

	/**
	 * Default constructor.
	 *
	 * Creates an XML Schema type object which is invalid, and thus
	 * must be configured later in order to be of any use.
	 */
	public XSDType()
	{
		this.type = TYPE_INVALID;
		this.attributes = new ArrayList();
		this.members = new ArrayList();
		this.restricted = false;
		this.mixed = false;
		this.memberrefs = new ArrayList();
	}

	/**
	 * Sets the name of this type.
	 *
	 * This is the schema-specific name, not to be confused with the
	 * automatically determined name of its base type in the case of
	 * derived types. A type might be anonymous, in which case it
	 * wouldn't have a name, but a name is automatically generated in
	 * such a case.
	 *
	 * @param name Name of this type object
	 */
	public void setName(QName name)
	{
		this.name = name;
	}

	/**
	 * Sets the type's sequence.
	 *
	 * Complex types always have a sequence with one or more child
	 * elements. This method is not useful for simple types.
	 * For element group pseudo-types, this is the sequence of
	 * elements contained in the group.
	 *
	 * @param sequence Sequence to be used for the complex type
	 */
	public void setSequence(XSDSequence sequence)
	{
		this.sequence = sequence;
	}

	/**
	 * Sets the type's restriction.
	 *
	 * A restriction object, itself possibly representing multiple
	 * restrictions, can be given for simple types.
	 * They limit the range of possible values depending on the
	 * element's base type.
	 *
	 * @param restriction Restriction to be used for the simple type
	 */
	public void setRestriction(XSDRestriction restriction)
	{
		this.restriction = restriction;
	}

	/**
	 * Sets the type's internal type value.
	 *
	 * This value is either \ref TYPE_COMPLEX, or any of the symbolic
	 * simple type values. It can also be \ref TYPE_ANY_TYPE to mean that
	 * the element can be of any type, or \ref TYPE_LIST or \ref TYPE_UNION
	 * for constructions of simple types.
	 *
	 * @param type Type value according to the available type symbols
	 */
	public void setType(int type)
	{
		this.type = type;
	}

	/**
	 * Sets the type's base type.
	 *
	 * For values derived from non-builtin simple types, as well
	 * as \ref TYPE_LIST types, a base type needs to be defined.
	 *
	 * @param basetype Base type for this type
	 */
	public void setBaseType(XSDType basetype)
	{
		this.basetype = basetype;
	}

	/**
	 * Sets the type's parent element.
	 *
	 * This makes the element known to the type.
	 *
	 * @param parent Parent element of this type object
	 * @internal
	 */
	public void setParentElement(XSDElement parent)
	{
		this.parent = parent;
	}

	/**
	 * Adds an attribute to the type of a complex element.
	 *
	 * In case of the type object being complex, attributes
	 * may be added here to indicate that instance elements
	 * of this type carry these attributes.
	 * The method \ref setAttributes can be used to set a whole
	 * list in one go, alternatively.
	 *
	 * @param attribute Attribute to add to this type
	 *
	 * @see setAttributes
	 */
	public void addAttribute(XSDAttribute attribute)
	{
		this.attributes.add(attribute);
	}

	/**
	 * Adds a list of attributes to the type of a complex element.
	 *
	 * This method sets all attributes for a complex type.
	 * Note: this will overwrite all previously added attributes,
	 * both by this method and by \ref addAttribute.
	 *
	 * @param attributes List of XSDAttribute objects to add to this type
	 */
	public void setAttributes(ArrayList attributes)
	{
		this.attributes = new ArrayList();

		for(int i = 0; i < attributes.size(); i++)
		{
			XSDAttribute xsdatt = (XSDAttribute)attributes.get(i);
			addAttribute(xsdatt);
		}
	}

	/**
	 * Adds a member type to the list of union types.
	 *
	 * Union types are based on their member types.
	 * This requires the internal type to be \ref TYPE_UNION.
	 *
	 * @param member Type which is part of the union
	 */
	public void addMemberType(XSDType member)
	{
		this.members.add(member);
	}

	/**
	 * Sets the choice which substitutes the parent element.
	 *
	 * Choices are represented as elements of type
	 * \ref TYPE_CHOICE.
	 *
	 * @param choice Choice containing elements and groups
	 *
	 * @see getChoice
	 */
	public void setChoice(XSDChoice choice)
	{
		this.choice = choice;
	}

	/**
	 * Configures a complex type to be restricted.
	 *
	 * Complex types might be restricted or extended. Both will
	 * have a base type. If the base type is being restricted,
	 * this method must be called.
	 * It is only valid for \ref TYPE_COMPLEX types. For simple
	 * types, \ref setRestriction is the equivalent function.
	 *
	 * @param restricted Whether or not this type is restricted
	 */
	public void setRestricted(boolean restricted)
	{
		this.restricted = restricted;
	}

	/**
	 * Configures a complex type to be mixed.
	 *
	 * Mixed types might contain additional floating text
	 * around their elements.
	 * This method is only valid for \ref TYPE_COMPLEX types.
	 *
	 * @param mixed Whether or not this type is mixed
	 */
	public void setMixed(boolean mixed)
	{
		this.mixed = mixed;
	}

	/**
	 * Set reference to a base type.
	 *
	 * Sets the fully-qualified name of the element which is
	 * later resolved to the base type via \ref setBaseType.
	 * This method is valid for simple types of type \ref TYPE_LIST,
	 * and for complex types of type \ref TYPE_COMPLEX, as well as
	 * for all restricted or extended types.
	 *
	 * @param baseref Fully-qualified name of the base type
	 */
	public void setBaseRef(QName baseref)
	{
		this.baseref = baseref;
	}

	/**
	 * Add reference to a member type.
	 *
	 * Union types consist of several types which make up the union.
	 * All \ref TYPE_UNION types need this method to set up the
	 * references to those, which are then resolved via
	 * \ref addMemberType.
	 *
	 * @param memberref Fully-qualified name of a member
	 */
	public void addMemberRef(QName memberref)
	{
		this.memberrefs.add(memberref);
	}

	/**
	 * Sets the annotation for this type.
	 *
	 * Types can carry some documentation in form of an annotation
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
	 * Returns the type's name.
	 *
	 * @return Name of this type
	 */
	public QName getName()
	{
		return this.name;
	}

	/**
	 * Returns the type's sequence.
	 *
	 * For element groups, the sequence contains the elements in the group,
	 * otherwise it represents the content of a complex type.
	 *
	 * @return An XML Schema sequence in the case of complex types, or \b null otherwise
	 */
	public XSDSequence getSequence()
	{
		return this.sequence;
	}

	/**
	 * Returns the type's restriction.
	 *
	 * @return A restriction object, or \b null if the type is not restricted
	 */
	public XSDRestriction getRestriction()
	{
		return this.restriction;
	}

	/**
	 * Returns the type's internal type value.
	 *
	 * @return Type value of this type
	 */
	public int getType()
	{
		return this.type;
	}

	/**
	 * Returns the type's base type.
	 *
	 * @return Base type of this type
	 */
	public XSDType getBaseType()
	{
		return this.basetype;
	}

	/**
	 * Returns the type's parent element.
	 *
	 * @return Parent element of this type
	 * @internal
	 */
	public XSDElement getParentElement()
	{
		return this.parent;
	}

	/**
	 * Returns the list of attributes for this type.
	 *
	 * @return List of attributes, or empty list for simple types
	 */
	public ArrayList getAttributes()
	{
		return this.attributes;
	}

	/**
	 * Returns the list of member types in case the type is of
	 * the \ref TYPE_UNION nature.
	 *
	 * @return Member types, or empty list for non-union types
	 */
	public ArrayList getMemberTypes()
	{
		return this.members;
	}

	/**
	 * Returns the choices behind a pseudo-element.
	 *
	 * Elements of type \ref TYPE_CHOICE are representing choices.
	 * The choices can be retrieved using this method.
	 *
	 * @return Choices of a pseudo-element
	 *
	 * @see setChoice
	 */
	public XSDChoice getChoice()
	{
		return this.choice;
	}

	/**
	 * Tells whether the complex type is restricted.
	 *
	 * Types of type \ref TYPE_COMPLEX can be restricted or
	 * extended. In the former case, this method returns \b true.
	 *
	 * @return Whether this type is restricted or not
	 *
	 * @see setRestricted
	 */
	public boolean getRestricted()
	{
		return this.restricted;
	}

	/**
	 * Tells whether the complex type is mixed.
	 *
	 * If the type is \ref TYPE_COMPLEX and allow text
	 * beside its elements, then this method returns \b true.
	 *
	 * @return Whether this type is mixed or not
	 *
	 * @see setMixed
	 */
	public boolean getMixed()
	{
		return this.mixed;
	}

	/**
	 * Returns the reference to the type's base type.
	 *
	 * Returns a fully-qualified name to this type's base type,
	 * or \b null if no base type exists.
	 *
	 * @return Fully-qualified name of base type
	 */
	public QName getBaseRef()
	{
		return this.baseref;
	}

	/**
	 * Returns the reference to the type's member types.
	 *
	 * Returns a list of all member types if the type is
	 * in fact \ref TYPE_UNION, or \b null otherwise.
	 *
	 * @return List of QName objects describing the member types
	 */
	public ArrayList getMemberRefs()
	{
		return this.memberrefs;
	}

	/**
	 * Returns the annotation attached to this type.
	 *
	 * @return Annotation, which is usually \b null
	 */
	public XSDAnnotation getAnnotation()
	{
		return this.annotation;
	}
}

