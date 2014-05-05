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
 * \brief Class corresponding to an annotation in an XML Schema.
 *
 * Annotations contain element, type and schema documentation, and sometimes
 * application-specific information.
 *
 * @author Josef Spillner <spillner@rn.inf.tu-dresden.de>
 */
public class XSDAnnotation
{
	// annotation's documentation string
	private String documentation;
	// annotation's documentation URL for extended documentation
	private String docurl;
	// annotation's appinfo string
	private String appinfo;

	/**
	 * Default constructor.
	 *
	 * Creates an empty annotation object.
	 */
	public XSDAnnotation()
	{
	}

	/**
	 * Sets the documentation string.
	 *
	 * Exactly one string can be given.
	 * FIXME: i18n by xml:lang
	 *
	 * @param documentation The documentation string of the annotation
	 */
	public void setDocumentation(String documentation)
	{
		documentation = documentation.trim();

		this.documentation = documentation;
	}

	/**
	 * Sets the documentation URL.
	 *
	 * In addition to a documentation string, an URL can point
	 * to a HTML document containing additional information.
	 *
	 * @param docurl URL pointing to a HTML document
	 */
	public void setDocumentationURL(String docurl)
	{
		this.docurl = docurl;
	}

	/**
	 * Sets the application information.
	 *
	 * This string contains information for some processing applications.
	 *
	 * @param appinfo The application information
	 */
	public void setAppInfo(String appinfo)
	{
		this.appinfo = appinfo;
	}

	/**
	 * Returns the documentation string.
	 *
	 * @return Documentation string of this annotation
	 */
	public String getDocumentation()
	{
		return this.documentation;
	}

	/**
	 * Returns the documentation URL.
	 *
	 * The URL might not always be set, returning \b null
	 * in this case. If it is set, it will point to a HTML
	 * document with more documentation on the annotation's
	 * parent element.
	 *
	 * @return Documentation URL of this annotation
	 */
	public String getDocumentationURL()
	{
		return this.docurl;
	}

	/**
	 * Returns the application information of this annotation.
	 *
	 * @return Application information
	 */
	public String getAppInfo()
	{
		return this.appinfo;
	}
}

