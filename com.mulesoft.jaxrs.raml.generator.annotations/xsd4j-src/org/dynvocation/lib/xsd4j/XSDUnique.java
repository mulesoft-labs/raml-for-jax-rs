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

/**
 * \brief Class for expressing uniqueness constraints in XML Schema.
 *
 * Uniqueness can be enforced for certain attributes or elements within
 * a selection of child nodes of a parent element.
 *
 * @author Josef Spillner <spillner@rn.inf.tu-dresden.de>
 */
public class XSDUnique
{
	// selector for a set of nodes which must be unique in some way
	private String selector;
	// the element or attribute name which must be unique
	private String field;
	// name of this constraint
	private String name;

	/**
	 * Default constructor.
	 *
	 * Creates an empty uniqueness constraint object.
	 */
	public XSDUnique()
	{
	}

	/**
	 * Sets the name of this constraint.
	 *
	 * @param name Name for this constraint
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Sets the node selector.
	 *
	 * The XPath expression given as an argument will select a set
	 * of nodes, relative to the element owning this constraint,
	 * for which the uniqueness will be enforced.
	 *
	 * @param selector XPath expression for node selection
	 */
	public void setSelector(String selector)
	{
		this.selector = selector;
	}

	/**
	 * Sets the field name.
	 *
	 * Another XPath expression which will determine the element or
	 * attribute names among the selected nodes which must be unique.
	 *
	 * @param field XPath expression for the unique field
	 */
	public void setField(String field)
	{
		this.field = field;
	}

	/**
	 * Returns the constraint name.
	 *
	 * @return Name of this constraint
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 * Returns the selector XPath expression.
	 *
	 * @return XPath expression for selection of nodes
	 */
	public String getSelector()
	{
		return this.selector;
	}

	/**
	 * Returns the field XPath expression.
	 *
	 * @return XPath expression for the unique field
	 */
	public String getField()
	{
		return this.field;
	}
}

