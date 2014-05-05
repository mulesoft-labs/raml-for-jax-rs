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
 * \brief Class describing an XML Schema tree.
 *
 * Most of the time, only one object of this type exists in the application,
 * but its child elements expand to a whole XML schema tree representation.
 * All of the \ref XSDSequence methods are also applicable to objects of
 * this type, but not all of them make sense.
 *
 * For all schema access levels (see \ref XSDParser on how to build XSDSchema
 * objects), the schema definitions can be retrieved. The methods for this
 * are \ref getTypes, \ref getGroups, \ref getAttributes and \ref getElements,
 * the latter two being from \ref XSDSequence.
 * Types include both simple and complex types, and groups include both
 * attribute groups and the element groups referenced in choices.
 *
 * In case of non-resolved included files (i.e. in access level "flat"),
 * the methods \ref getIncludes and \ref getImports return the locations
 * of the referenced files. This information is used by \ref XSDTransformer
 * for augmenting the level.
 *
 * @author Josef Spillner <spillner@rn.inf.tu-dresden.de>
 */
public class XSDSchema extends XSDSequence
{
	// Target namespace, might be null for non-namespaced schemas
	private String tns;
	// Top-level XSDTypes
	private ArrayList types;
	// Top-level XSDSequences describing element and attribute groups
	private ArrayList groups;
	// Top-level Strings as URLs to not-yet-included include files
	private ArrayList includes;
	// Top-level Strings as URLs to not-yet-imported import files
	private ArrayList imports;
	// Corresponding namespaces for the imports
	private ArrayList importnamespaces;
	// Current access level
	private int level;

	/**
	 * Default constructor.
	 *
	 * Returns an empty XML schema object.
	 */
	public XSDSchema()
	{
		super();

		this.tns = null;
		this.types = new ArrayList();
		this.groups = new ArrayList();
		this.includes = new ArrayList();
		this.imports = new ArrayList();
		this.importnamespaces = new ArrayList();

		this.level = XSDParser.PARSER_FLAT;
	}

	/**
	 * Sets the target namespace of the schema.
	 *
	 * The target namespace determines if element references,
	 * base types and such can be located in the current schema.
	 * It also determines the fully-qualified name of the
	 * schema top-level entries.
	 * Schemas do not need namespaces in all cases.
	 *
	 * @param tns Namespace unique for this schema, if present
	 *
	 * @see getTargetNamespace
	 */
	public void setTargetNamespace(String tns)
	{
		this.tns = tns;
	}

	/**
	 * Returns the schema's target namespace.
	 *
	 * If a target namespace is present, returns it, or
	 * otherwise returns \b null.
	 *
	 * @return Target namespace of the schema
	 *
	 * @see setTargetNamespace
	 */
	public String getTargetNamespace()
	{
		return this.tns;
	}

	/**
	 * Adds a single top-level type definition.
	 *
	 * The type might be simple or complex.
	 *
	 * @param xsdtype Type to be added to the schema
	 */
	public void addType(XSDType xsdtype)
	{
		this.types.add(xsdtype);
	}

	/**
	 * Returns all top-level type definitions.
	 *
	 * All types previously added with \ref addType
	 * are returned here as a list.
	 *
	 * @return List of \ref XSDType objects
	 */
	public ArrayList getTypes()
	{
		return this.types;
	}

	/**
	 * Adds a group top-level definition.
	 *
	 * Both element groups and attribute groups might be
	 * added here.
	 *
	 * @param xsdgroup Sequence of elements or attributes
	 */
	public void addGroup(XSDSequence xsdgroup)
	{
		this.groups.add(xsdgroup);
	}

	/**
	 * Empty the list of top-level groups.
	 *
	 * Whenever groups are dissolved by augmenting the access level
	 * to \ref XSDParser::PARSER_MERGED, the list of top-level
	 * groups is cleared.
	 *
	 * @internal
	 */
	public void clearGroups()
	{
		this.groups= new ArrayList();
	}

	/**
	 * Returns all top-level group definitions.
	 *
	 * All groups previously added with \ref addGroup
	 * are returned here as a list.
	 *
	 * @return List of \ref XSDSequence objects describing groups
	 */
	public ArrayList getGroups()
	{
		return this.groups;
	}

	/**
	 * Adds a top-level include definition.
	 *
	 * Include statements contain a schema location where
	 * to find the additional schema to include into this
	 * one. Its URL is added here.
	 *
	 * @param includefile URL of the schema to include
	 */
	public void addInclude(String includefile)
	{
		this.includes.add(includefile);
	}

	/**
	 * Empty the list of include definitions.
	 *
	 * Whenever the list of included and imported schemas is resolved
	 * by loading and parsing them (by augmenting the access
	 * level to \ref XSDParser::PARSER_FLAT_INCLUDES at least),
	 * the list of include and import URLs is cleared.
	 *
	 * @internal
	 */
	public void clearIncludes()
	{
		this.includes = new ArrayList();
	}

	/**
	 * Returns the list of all include files for this chema.
	 *
	 * If no include files are present or all of them have been
	 * resolved already, this method returns the empty list.
	 *
	 * @return List of Strings describing URLs of include files
	 */
	public ArrayList getIncludes()
	{
		return this.includes;
	}

	/**
	 * Adds a top-level import definition.
	 *
	 * Import statements contain a schema location where
	 * to find the additional schema to import into this
	 * one with a certain namespace.
	 * The schema's URL and namespace are added here.
	 *
	 * @param importfile URL of the schema to import
	 * @param namespace Namespace of the schema
	 */
	public void addImport(String importfile, String namespace)
	{
		this.imports.add(importfile);
		this.importnamespaces.add(namespace);
	}

	/**
	 * Empty the list of import definitions.
	 *
	 * Whenever the list of included and imported schemas is resolved
	 * by loading and parsing them (by augmenting the access
	 * level to \ref XSDParser::PARSER_FLAT_INCLUDES at least),
	 * the list of include and import URLs is cleared.
	 *
	 * @internal
	 */
	public void clearImports()
	{
		this.imports = new ArrayList();
		this.importnamespaces = new ArrayList();
	}

	/**
	 * Returns the list of all import files for this chema.
	 *
	 * If no import files are present or all of them have been
	 * resolved already, this method returns the empty list.
	 *
	 * @return List of Strings describing URLs of import files
	 *
	 * @see getImportNamespaces
	 */
	public ArrayList getImports()
	{
		return this.imports;
	}

	/**
	 * Returns the list of all import namespaces for this schema.
	 *
	 * If no import files are present or all of them have been
	 * resolved already, this method returns the empty list.
	 *
	 * @return List of Strings describing namespaces of imported schemas
	 *
	 * @see getImports
	 */
	public ArrayList getImportNamespaces()
	{
		return this.importnamespaces;
	}

	/**
	 * Sets the schema access level.
	 *
	 * This method should only be used by \ref XSDParser and
	 * \ref XSDTransformer to specify the access level.
	 * The level requires a certain internal structure and overriding
	 * it may lead to inconsistencies.
	 *
	 * @param level New access level for this schema
	 *
	 * @internal
	 */
	public void setLevel(int level)
	{
		this.level = level;
	}

	/**
	 * Returns the schema access level.
	 *
	 * Depending on how the schema was parsed by \ref XSDParser and
	 * if it was transformed thereafter by \ref XSDTransformer, this
	 * method returns the current access level for this schema.
	 * The value is one of the PARSER_* constants defined in
	 * \ref XSDParser.
	 *
	 * @return Current access level for this schema object
	 */
	public int getLevel()
	{
		return this.level;
	}
}

