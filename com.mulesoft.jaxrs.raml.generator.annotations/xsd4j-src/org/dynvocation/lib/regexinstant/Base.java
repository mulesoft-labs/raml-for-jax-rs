// RegExpInstantiator - Constraint-based value creation from regexps
// Copyright (C) 2006 - 2008 Josef Spillner <spillner@rn.inf.tu-dresden.de>
//
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 3 of the License, or
// (at your option) any later version.
//
// This file is part of the XSD4J library.
// It has been created as part of Project Dynvocation, a research project
// at the Chair of Computer Networks, Faculty for Computer Sciences,
// Dresden University of Technology.
// See http://dynvocation.selfip.net/xsd4j/ for more information.

package org.dynvocation.lib.regexinstant;

/**
 * \brief Base class for debugging.
 *
 * A convenient base class derived from XSDBase which handles debugging
 * internally and to stderr if REIDEBUG is set.
 *
 * @author Josef Spillner <spillner@rn.inf.tu-dresden.de>
 */
public class Base
{
	// Log messages identifier (usually name of the class)
	private String logname = null;
	// The cumulative log, filled by debug() calls
	private StringBuffer log = new StringBuffer();

	/**
	 * Default constructor.
	 *
	 * The debugging is initialised to use the derived class' name
	 * as a prefix.
	 */
	public Base()
	{
		Class c = this.getClass();
		String n = c.getName();
		initDebug(n);
	}

	protected void debug(String s)
	{
		this.log.append("<<");
		this.log.append(this.logname);
		this.log.append(">>: ");
		this.log.append(s);
		this.log.append("\n");

		if(System.getenv("REIDEBUG") != null)
			System.err.println("<<" + this.logname + ">>: " + s);
	}

	private void initDebug(String logname)
	{
		this.logname = logname;
	}

	/**
	 * Returns the debug messages.
	 *
	 * All debug messages ever created by inheriting objects
	 * are stored as a single string and can be read from here.
	 *
	 * @return Cumulative debug log of the inheriting object
	 */
	public String getDebug()
	{
		return this.log.toString();
	}
}

