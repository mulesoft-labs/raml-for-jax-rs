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
 * \brief Class describing an XML Schema sequence.
 *
 * Sequences are used to hold one or more elements as children of an
 * existing XML Schema element.
 * Objects of type \ref XSDSchema are special sequences which do not have
 * a parent element, but can contain a list of attributes in addition to
 * elements.
 * Other special sequences are element groups and attribute groups. Both
 * will have their group name or reference set (see \ref getGroupName
 * and \ref getGroupRef), and can be distinguished by testing
 * \ref getGroupAttribute.
 *
 * @author Josef Spillner <spillner@rn.inf.tu-dresden.de>
 */
public class XSDSequence
{
	// List of XSDElement child elements
	private ArrayList elements;
	// Parent type, is null for schema
	private XSDType parent;
	// Name of the group, if the sequence represents one
	private QName groupname;
	// Reference to another group
	private QName groupref;
	// Sequence is an all-sequence
	private boolean all;
	// List of XSDAttribute objects, for attribute groups and schema
	private ArrayList attributes;
	// Group is an attribute group (as opposed to element group)
	private boolean groupattribute;
	// annotation object, if any
	private XSDAnnotation annotation;

	/**
	 * Default constructor.
	 *
	 * Creates a sequence which does not yet contain any elements.
	 * Sequences appearing in choices are element groups, and others
	 * might be attribute groups.
	 */
	public XSDSequence()
	{
		this.elements = new ArrayList();
		this.all = false;

		this.attributes = new ArrayList();
		this.groupattribute = false;
	}

	/**
	 * Adds an element to the sequence.
	 *
	 * The list of elements is enhanced by the given element.
	 *
	 * @param element Element to add to this sequence
	 */
	public void addElement(XSDElement element)
	{
		this.elements.add(element);
	}

	/**
	 * Sets the parent type.
	 *
	 * The type of the parent element is made known to the sequence.
	 *
	 * @param parent XML Schema type of the parent element
	 * @internal
	 */
	public void setParentType(XSDType parent)
	{
		this.parent = parent;
	}

	/**
	 * Sets the group name.
	 *
	 * If the sequence is an element or attribute group, it will
	 * have a name associated with it.
	 *
	 * @param groupname Fully-qualified name of the group.
	 */
	public void setGroupName(QName groupname)
	{
		this.groupname = groupname;
	}

	/**
	 * Configures this sequence as an all-sequence.
	 *
	 * All-sequences are special sequences which contain all of
	 * their elements either once or not at all.
	 *
	 * @param all Whether or not this is an all-sequence
	 */
	public void setAll(boolean all)
	{
		this.all = all;
	}

	/**
	 * Adds an attribute to this sequence.
	 *
	 * If either the sequence represents the schema
	 * (see \ref XSDSchema), or an attribute group,
	 * it will have a list of attributes.
	 *
	 * @param attribute Attribute to add to this sequence
	 */
	public void addAttribute(XSDAttribute attribute)
	{
		this.attributes.add(attribute);
	}

	/**
	 * Configures this group to be an attribute group.
	 *
	 * If this sequence is a group (see \ref setGroupName
	 * and \ref setGroupRef), calling this method will make
	 * it an attribute group instead of an element group.
	 *
	 * @param attribute Whether or not this group is an attribute group
	 */
	public void setGroupAttribute(boolean attribute)
	{
		this.groupattribute = attribute;
	}

	/**
	 * Sets the group reference.
	 *
	 * If the group is not fully defined, but only references another
	 * group, then this method sets the fully-qualified name
	 * of the referenced group.
	 *
	 * @param groupref Fully-qualified name to the referenced group
	 */
	public void setGroupRef(QName groupref)
	{
		this.groupref = groupref;
	}

	/**
	 * Sets the annotation for this sequence.
	 *
	 * Sequences can carry some documentation in form of an annotation
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
	 * Returns the list of elements.
	 *
	 * @return List of all elements within this sequence
	 */
	public ArrayList getElements()
	{
		return this.elements;
	}

	/**
	 * Returns the parent type.
	 *
	 * @return Type of the parent element
	 * @internal
	 */
	public XSDType getParentType()
	{
		return this.parent;
	}

	/**
	 * Returns the name of the group.
	 *
	 * If the sequence is an element or attribute group,
	 * this method will return a name, or otherwise \b null.
	 *
	 * @return Fully-qualified name of this group
	 */
	public QName getGroupName()
	{
		return this.groupname;
	}

	/**
	 * Tells whether this sequence is an all-sequence.
	 *
	 * If all elements in this sequence must appear either once
	 * or not at all, returns \b true, otherwise returns \b false.
	 */
	public boolean getAll()
	{
		return this.all;
	}

	/**
	 * Returns the list of all attributes in this sequence or group.
	 *
	 * If the sequence is the top-level \ref XSDSchema or it
	 * represents an attribute group, the attributes can be retrieved
	 * from this method. Otherwise, returns the empty list.
	 *
	 * @return List of XSDAttribute objects
	 */
	public ArrayList getAttributes()
	{
		return this.attributes;
	}

	/**
	 * Tells whether this group is an attribute group.
	 *
	 * If this sequence is a group and it is an attribute group
	 * instead of an element group, returns \b true, otherwise
	 * returns \b false.
	 *
	 * @return Whether or not this group is an attribute group.
	 */
	public boolean getGroupAttribute()
	{
		return this.groupattribute;
	}

	/**
	 * Returns the reference to another group.
	 *
	 * If the group has not been resolved (merged) yet,
	 * and it is not fully defined but points to another group,
	 * the referenced group's name is returned here. Otherwise,
	 * \b null is returned.
	 *
	 * @return Fully-qualified name of the referenced group
	 */
	public QName getGroupRef()
	{
		return this.groupref;
	}

	/**
	 * Returns the annotation attached to this sequence.
	 *
	 * @return Annotation, which is usually \b null
	 */
	public XSDAnnotation getAnnotation()
	{
		return this.annotation;
	}
}

