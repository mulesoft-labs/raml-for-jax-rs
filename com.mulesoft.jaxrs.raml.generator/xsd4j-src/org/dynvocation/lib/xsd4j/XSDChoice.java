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

/**
 * \brief Class corresponding to a choice in XML Schema.
 *
 * Choices are represented as pseudo-elements of the special type
 * \ref XSDType::TYPE_CHOICE. The type's \ref XSDType::getChoice method
 * will then return all the choices (either elements or element groups),
 * one of which will take the place of the pseudo-element in instances.
 *
 * @author Josef Spillner <spillner@rn.inf.tu-dresden.de>
 */
public class XSDChoice
{
	// Fully-qualified name
	// FIXME: choices have no names!?
	//private QName name;
	// Element groups (XSDSequence) contained in this choice
	private ArrayList groups;
	// Single elements (XSDElement) contained in this choice
	private ArrayList elements;

	/**
	 * Default constructor.
	 *
	 * Produces an empty choice.
	 */
	public XSDChoice()
	{
		this.groups = new ArrayList();
		this.elements = new ArrayList();
	}

	/*public void setName(QName name)
	{
		this.name = name;
	}*/

	/**
	 * Adds an element to the choice.
	 *
	 * @param xsdelement Element to be added to this choice
	 */
	public void addElement(XSDElement xsdelement)
	{
		this.elements.add(xsdelement);
	}

	/**
	 * Adds an element group to the choice.
	 *
	 * @param xsdgroup Group of elements to be added
	 */
	public void addGroup(XSDSequence xsdgroup)
	{
		this.groups.add(xsdgroup);
	}

	/*public QName getName()
	{
		return this.name;
	}*/

	/**
	 * Returns all elements of the choice.
	 *
	 * All single elements which were added are returned here.
	 *
	 * @return List of XSDElement objects
	 */
	public ArrayList getElements()
	{
		return this.elements;
	}

	/**
	 * Returns all element groups of this choice.
	 *
	 * All element groups which were added are returned here.
	 *
	 * @return List of XSDSequence objects representing element groups
	 */
	public ArrayList getGroups()
	{
		return this.groups;
	}
}

