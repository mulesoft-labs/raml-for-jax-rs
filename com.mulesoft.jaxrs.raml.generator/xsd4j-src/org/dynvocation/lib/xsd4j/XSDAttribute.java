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

import javax.xml.namespace.*;

/**
 * \brief Class corresponding to an attribute in an XML Schema.
 *
 * This class represents an attribute, which belongs to a certain element
 * of type \ref XSDElement, and carries a certain type of type \ref XSDType.
 * Its methods allow to set and get attribute properties according to the
 * XML Schema specification.
 *
 * @author Josef Spillner <spillner@rn.inf.tu-dresden.de>
 */
public class XSDAttribute
{
	// Fully-qualified name of the attribute
	private QName name;
	// Associated type, if already resolved (see 'typeref')
	private XSDType type;
	// Reference to another attribute
	private QName ref;
	// Reference to a type (see 'type')
	private QName typeref;
	// annotation object, if any
	private XSDAnnotation annotation;
	// Fixed value for this attribute
	private String fixedvalue;
	// Default value for this attribute
	private String defaultvalue;
	// Usage policy, one of the USE_* constants below
	private int use;
	// Whether or not instances will appear with namespace
	private boolean qualified;

	/** Symbolic value to express that this is an optional attribute. */
	public static final int USE_OPTIONAL = 1;
	/** Symbolic value to express that the use of this attribute is necessary. */
	public static final int USE_REQUIRED = 2;
	/** Symbolic value to express that this attribute must not be used. */
	public static final int USE_PROHIBITED = 3;

	/**
	 * Default constructor.
	 *
	 * Creates an attribute object which is required to be used on its
	 * parent element.
	 */
	public XSDAttribute()
	{
		this.use = USE_REQUIRED;
		this.qualified = false;
	}

	/**
	 * Sets the default value.
	 *
	 * The textual value of the XML attribute can be specified with this
	 * method. The default value is used in case the attribute is not present
	 * in the instance data, which in turn must be allowed by its usage
	 * policy.
	 *
	 * @param defaultvalue The default value of this attribute
	 */
	public void setDefaultValue(String defaultvalue)
	{
		this.defaultvalue = defaultvalue;
	}

	/**
	 * Sets a fixed value.
	 *
	 * The textual value of the XML attribute can be fixed with this
	 * method. No other values might then appear in any instance of the
	 * corresponding XML Schema.
	 *
	 * @param fixedvalue The fixed value of this attribute
	 */
	public void setFixedValue(String fixedvalue)
	{
		this.fixedvalue = fixedvalue;
	}

	/**
	 * Sets the usage policy.
	 *
	 * Each attribute may or may not be used on its parent element depending
	 * on this setting. The usage policy can be either of \ref
	 * USE_OPTIONAL, \ref USE_REQUIRED or \ref USE_PROHIBITED.
	 *
	 * @param use Usage policy of this attribute
	 */
	public void setUse(int use)
	{
		this.use = use;
	}

	/**
	 * Sets the name of the attribute.
	 *
	 * Each attribute must carry a name with which it might be identified.
	 *
	 * @param name Name of this attribute
	 */
	public void setName(QName name)
	{
		this.name = name;
	}

	/**
	 * Sets the type of this attribute.
	 *
	 * An attribute might be assigned a type. However, only simple types
	 * may be used in this context, as opposed to elements, which can also
	 * be of a complex type.
	 *
	 * @param type Type object describing a simple type
	 */
	public void setType(XSDType type)
	{
		this.type = type;
	}

	/**
	 * Configures the instance qualification of the attribute.
	 *
	 * If an attribute is to appear fully-qualified, that is,
	 * including namespace prefix, in the resulting instance
	 * data, this method must be used.
	 * The default is to appear unqualified, unless the schema
	 * contains a global default value.
	 *
	 * @param qualified Whether or not instances of this attribute will be qualified
	 */
	public void setQualified(boolean qualified)
	{
		this.qualified = qualified;
	}

	/**
	 * Sets the reference attribute for this attribute.
	 *
	 * XML Schema allows attributes to refer to other attributes by name.
	 * Since the referee might not yet be evaluated at the time this attribute
	 * is being parsed, a temporary reference is set, which is later resolved
	 * automatically by the parser, in which case the reference is then
	 * reset to \b null.
	 * For fully defined attributes, this method is not called.
	 *
	 * @param ref The name of the reference attribute
	 * @internal
	 */
	public void setRef(QName ref)
	{
		this.ref = ref;
	}

	/**
	 * Sets the type reference for this attribute.
	 *
	 * If the type has not yet been resolved,
	 * this reference is everything which hints at which type this
	 * attribute is of.
	 * Once the type gets resolved via \ref setType, it still remains
	 * in place.
	 * For inline-defined types, this method is not called.
	 *
	 * @param typeref Fully-qualified name of the attribute's type
	 */
	public void setTypeRef(QName typeref)
	{
		this.typeref = typeref;
	}

	/**
	 * Sets the annotation for this attribute.
	 *
	 * Attributes can carry some documentation in form of an annotation
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
	 * Returns the fixed value of this attribute.
	 *
	 * @return Fixed value assigned to this attribute
	 */
	public String getFixedValue()
	{
		return this.fixedvalue;
	}

	/**
	 * Returns the default value of this attribute.
	 *
	 * @return Default value assigned to this attribute
	 */
	public String getDefaultValue()
	{
		return this.defaultvalue;
	}

	/**
	 * Returns the usage policy of this attribute.
	 *
	 * @return Usage policy of this attribute
	 */
	public int getUse()
	{
		return this.use;
	}

	/**
	 * Returns the name of this attribute.
	 *
	 * @return Name of this attribute
	 */
	public QName getName()
	{
		return this.name;
	}

	/**
	 * Returns the type of this attribute.
	 *
	 * @return Type object describing the type of this attribute
	 */
	public XSDType getType()
	{
		return this.type;
	}

	/**
	 * Tells whether this attribute is qualified.
	 *
	 * If the attribute should appear fully qualified in instance data,
	 * returns \b true, otherwise \b false.
	 *
	 * @return Qualification of this attribute
	 */
	public boolean getQualified()
	{
		return this.qualified;
	}

	/**
	 * Returns the reference to another attribute.
	 *
	 * This might be \b null if the attribute is fully defined instead
	 * of being a reference to another (top-level) one.
	 *
	 * @return Attribute reference as a fully-qualified name
	 * @internal
	 */
	public QName getRef()
	{
		return this.ref;
	}

	/**
	 * Returns the reference to the attribute's type.
	 *
	 * This might be \b null if the attribute's type is defined inline.
	 *
	 * @return Type reference as a fully-qualified name
	 */
	public QName getTypeRef()
	{
		return this.typeref;
	}

	/**
	 * Returns the annotation attached to this attribute.
	 *
	 * @return Annotation, which is usually \b null
	 */
	public XSDAnnotation getAnnotation()
	{
		return this.annotation;
	}
}

