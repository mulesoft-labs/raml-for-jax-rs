/*
 * Copyright 2013-2015 (c) MuleSoft, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.raml.jaxrs.codegen.core.ext;

import java.lang.annotation.Annotation;
import java.util.Collection;

import org.raml.model.Action;
import org.raml.model.MimeType;
import org.raml.model.Raml;
import org.raml.model.Resource;
import org.raml.model.parameter.AbstractParam;

import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;

public interface GeneratorExtension {

	
	/**
	 * Called after a class is added to the Java model by the code generator
	 * @param resourceInterface
	 * @param resource
	 */
	public void onCreateResourceInterface(final JDefinedClass resourceInterface, Resource resource);
	
	/**
	 * Called after a method is added to the Java model by the code generator
	 * @param method
	 * @param action
	 * @param bodyMimeType
	 * @param uniqueResponseMimeTypes
	 */
	public void onAddResourceMethod(final JMethod method, final Action action, 
			final MimeType bodyMimeType, final Collection<MimeType> uniqueResponseMimeTypes);
	
	/**
	 * Called to decide if a given parameter should be added to a method.  Is used to avoid passing request parameters to a method (or response parameteres from a method) if
	 * the parameter is handled solely in a servlet filter.
	 * @param name
	 * @param parameter
	 * @param annotationClass
	 * @param method
	 * @return true if the parameter should be added; false if it is should be ignored
	 */
	public boolean AddParameterFilter(final String name,
            final AbstractParam parameter,
            final Class<? extends Annotation> annotationClass,
            final JMethod method);
	
	
	/**
	 * Sets the {@link Raml}.
	 * 
	 * @param raml
	 */
	void setRaml(Raml raml);
}
