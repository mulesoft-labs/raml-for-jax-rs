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
 * \brief Class describing an XML Schema element.
 *
 * Objects of this type refer to a specific element within an XML schema.
 * Those can be either of a simple type (or derived therefrom), or of a
 * complex type, probably containing attributes and other elements.
 *
 * @author Josef Spillner <spillner@rn.inf.tu-dresden.de>
 */
public class XSDElement
{
	// minimum occurrence of element in complex type
	private int minOccurs;
	// maximum occurrence of element in complex type
	private int maxOccurs;
	// fully-qualified name
	private QName name;
	// type of the element (might be null if not resolved yet)
	private XSDType type;
	// parent sequence (which is the schema for top-level elements)
	private XSDSequence parent;
	// reference to other element
	private QName ref;
	// reference to type, see 'type'
	private QName typeref;
	// annotation object, if any
	private XSDAnnotation annotation;
	// whether or not the element appears with prefix in instance
	private boolean qualified;
	// whether or not the element can appear as xsi:nil in instance
	private boolean nillable;
	// fixed value for the element content, if type is simple
	private String fixedvalue;
	// default value for the element content, if type is simple
	private String defaultvalue;
	// uniqueness constraints
	private ArrayList uniques;

	/** Symbolic value for unbounded occurrence of an element. @see setMaxOccurs */
	public static final int OCCURS_UNBOUNDED = -1;

	/**
	 * Default constructor.
	 *
	 * Creates an XSDElement which appears exactly once in the schema
	 * as per the XML Schema specification.
	 */
	public XSDElement()
	{
		this.minOccurs = 1;
		this.maxOccurs = 1;
		this.qualified = false;
		this.nillable = false;
	}

	/**
	 * Sets the minimum number of occurences of this element in its current
	 * context.
	 *
	 * Valid values range from zero to any positive integer number.
	 *
	 * @param minOccurs Number of minimum occurrences of this element
	 */
	public void setMinOccurs(int minOccurs)
	{
		this.minOccurs = minOccurs;
	}

	/**
	 * Sets the maximum number of occurences of this element in its current
	 * context.
	 *
	 * Valid values range from zero to any positive integer number.
	 * To signal unlimited occurrences, the special value \ref
	 * OCCURS_UNBOUNDED exists.
	 *
	 * @param maxOccurs Number of maximum occurrences of this element
	 */
	public void setMaxOccurs(int maxOccurs)
	{
		this.maxOccurs = maxOccurs;
	}

	/**
	 * Sets the name of this element.
	 *
	 * All elements in a schema must carry a name.
	 *
	 * @param name Name of this element
	 */
	public void setName(QName name)
	{
		this.name = name;
	}

	/**
	 * Sets the type of this element.
	 *
	 * The type is an object of type \ref XSDType, which must previously
	 * have been created.
	 *
	 * @param type Type of this element
	 */
	public void setType(XSDType type)
	{
		this.type = type;
	}

	/**
	 * Sets the param sequence of this element.
	 *
	 * To prevent elements from being freestanding, they must have a parent
	 * sequence, which can either be an arbitrary sequence of a complex type,
	 * or as a special case the \ref XSDSchema object by itself.
	 *
	 * @param parent The parent sequence for this element
	 */
	public void setParentSequence(XSDSequence parent)
	{
		this.parent = parent;
	}

	/**
	 * Sets the reference element for this element.
	 *
	 * XML Schema allows elements to refer to other elements by name.
	 * Since the referee might not yet be evaluated at the time this element
	 * is being parsed, a temporary reference is set, which is later resolved
	 * automatically by the parser, in which case the reference is then
	 * reset to \b null.
	 * For fully defined elements, this method is not called.
	 *
	 * @param ref The name of the reference element
	 * @internal
	 */
	public void setRef(QName ref)
	{
		this.ref = ref;
	}

	/**
	 * Sets the type reference for this element.
	 *
	 * If the type has not yet been resolved, for example within the
	 * parser or permanently in \ref XSDParser::PARSER_FLAT level,
	 * this reference is everything which hints at which type this
	 * element is of.
	 * Once the type gets resolved via \ref setType, it still remains
	 * in place.
	 * For inline-defined types, this method is not called.
	 *
	 * @param typeref Fully-qualified name of the element's type
	 */
	public void setTypeRef(QName typeref)
	{
		this.typeref = typeref;
	}

	/**
	 * Sets the annotation for this element.
	 *
	 * Elements can carry some documentation in form of an annotation
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
	 * Configures the instance qualification of the element.
	 *
	 * If an element is to appear fully-qualified, that is,
	 * including namespace prefix, in the resulting instance
	 * data, this method must be used.
	 * The default is to appear unqualified, unless the schema
	 * contains a global default value.
	 *
	 * @param qualified Whether or not instances of this element will be qualified
	 */
	public void setQualified(boolean qualified)
	{
		this.qualified = qualified;
	}

	/**
	 * Configures the instance nil value of the element.
	 *
	 * If elements should appear with xsi:nil instead of not at
	 * all when being absent in instance data, this method
	 * must be used.
	 *
	 * @param nillable Whether or not instances of this element are nillable
	 */
	public void setNillable(boolean nillable)
	{
		this.nillable = nillable;
	}

	/**
	 * Sets the default value for this element.
	 *
	 * If the element's type is simple, this value will be used
	 * in instances if no other one has been specified.
	 *
	 * @param defaultvalue Default value for the element
	 */
	public void setDefaultValue(String defaultvalue)
	{
		this.defaultvalue = defaultvalue;
	}

	/**
	 * Sets the fixed value for this element.
	 *
	 * If the value is fixed, the element might either not
	 * appear at all or, if it does, must contain this value.
	 * Only simple types allow fixed values.
	 *
	 * @param fixedvalue Fixed value for this element
	 */
	public void setFixedValue(String fixedvalue)
	{
		this.fixedvalue = fixedvalue;
	}

	/**
	 * Adds a uniqueness constraint to this element.
	 *
	 * The constraint will affect validation of this element depending
	 * on how child elements and attributes are matching the uniqueness
	 * constraint.
	 *
	 * @param xsduniq Uniqueness constraint
	 */
	public void addUniqueConstraint(XSDUnique xsduniq)
	{
		if(this.uniques == null)
		{
			this.uniques = new ArrayList();
		}
		this.uniques.add(xsduniq);
	}

	/**
	 * Returns the minimum occurrence value of this element.
	 *
	 * Unless specified explicitely, this is 1.
	 *
	 * @return Minimum occurrence of this element
	 */
	public int getMinOccurs()
	{
		return this.minOccurs;
	}

	/**
	 * Returns the maximum occurrence value of this element.
	 *
	 * Unless specified explicitely, this is 1.
	 * A value of \ref OCCURS_UNBOUNDED represents an infinite number
	 * of occurrences.
	 *
	 * @return Maximum occurrence of this element
	 */
	public int getMaxOccurs()
	{
		return this.maxOccurs;
	}

	/**
	 * Returns the element's name.
	 *
	 * @return Name of this element
	 */
	public QName getName()
	{
		return this.name;
	}

	/**
	 * Returns the element's type.
	 *
	 * @return Type of this element
	 */
	public XSDType getType()
	{
		return this.type;
	}

	/**
	 * Returns the parent sequence.
	 *
	 * @return Parent sequence of this element
	 */
	public XSDSequence getParentSequence()
	{
		return this.parent;
	}

	/**
	 * Returns the reference to another element.
	 *
	 * This might be \b null if the element is fully defined instead
	 * of being a reference to another (top-level) one.
	 *
	 * @return Element reference as a fully-qualified name
	 * @internal
	 */
	public QName getRef()
	{
		return this.ref;
	}

	/**
	 * Returns the reference to the element's type.
	 *
	 * This might be \b null if the element's type is defined inline.
	 *
	 * @return Type reference as a fully-qualified name
	 */
	public QName getTypeRef()
	{
		return this.typeref;
	}

	/**
	 * Returns the annotation attached to this element.
	 *
	 * @return Annotation, which is usually \b null
	 */
	public XSDAnnotation getAnnotation()
	{
		return this.annotation;
	}

	/**
	 * Tells whether this element is qualified.
	 *
	 * If the element should appear fully qualified in instance data,
	 * returns \b true, otherwise \b false.
	 *
	 * @return Qualification of this element
	 */
	public boolean getQualified()
	{
		return this.qualified;
	}

	/**
	 * Tells whether this element is nillable.
	 *
	 * If the element might appear with xsi:nil in instance
	 * data, returns \b true, otherwise \b false.
	 *
	 * @return Nillability of this element
	 */
	public boolean getNillable()
	{
		return this.nillable;
	}

	/**
	 * Returns the element's default value.
	 *
	 * If a default value has been set for elements of simple type,
	 * returns it, otherwise returns \b null.
	 *
	 * @return Default value of this element
	 */
	public String getDefaultValue()
	{
		return this.defaultvalue;
	}

	/**
	 * Returns the element's fixed value.
	 *
	 * If a fixed value has been set for elements of simple type,
	 * returns it, otherwise returns \b null.
	 *
	 * @return Fixed value of this element
	 */
	public String getFixedValue()
	{
		return this.fixedvalue;
	}

	/**
	 * Returns the constraints for this element.
	 *
	 * If any child elements are constrained based on their element
	 * or attribute names, this method will return the list of
	 * uniqueness constraints.
	 *
	 * @return List of \ref XSDUnique objects, or \b null
	 */
	public ArrayList getUniqueConstraints()
	{
		return this.uniques;
	}
}

