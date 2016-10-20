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

import org.aml.apimodel.Action;
import org.aml.apimodel.MimeType;
import org.aml.apimodel.Api;
import org.aml.apimodel.INamedParam;
import org.aml.apimodel.Resource;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;

/**
 * <p>GeneratorExtension interface.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public interface GeneratorExtension {

	
	/**
	 * Called after a class is added to the Java model by the code generator
	 *
	 * @param resourceInterface a {@link com.sun.codemodel.JDefinedClass} object.
	 * @param resourceInterface a {@link com.sun.codemodel.JDefinedClass} object.
	 * @param resourceInterface a {@link com.sun.codemodel.JDefinedClass} object.
	 * @param resourceInterface a {@link com.sun.codemodel.JDefinedClass} object.
	 * @param resourceInterface a {@link com.sun.codemodel.JDefinedClass} object.
	 * @param resourceInterface
	 * @param resource a {@link org.aml.apimodel.Resource} object.
	 */
	public void onCreateResourceInterface(final JDefinedClass resourceInterface, Resource resource);
	
	/**
	 * Called after a method is added to the Java model by the code generator
	 *
	 * @param method a {@link com.sun.codemodel.JMethod} object.
	 * @param action a {@link org.aml.apimodel.Action} object.
	 * @param bodyMimeType a {@link org.aml.apimodel.MimeType} object.
	 * @param uniqueResponseMimeTypes a {@link java.util.Collection} object.
	 */
	public void onAddResourceMethod(final JMethod method, final Action action, 
			final MimeType bodyMimeType, final Collection<MimeType> uniqueResponseMimeTypes);
	
	/**
	 * Called to decide if a given parameter should be added to a method.  Is used to avoid passing request parameters to a method (or response parameteres from a method) if
	 * the parameter is handled solely in a servlet filter.
	 *
	 * @param name a {@link java.lang.String} object.
	 * @param parameter a {@link org.aml.typesystem.ramlreader.NamedParam} object.
	 * @param annotationClass a {@link java.lang.Class} object.
	 * @param method a {@link com.sun.codemodel.JMethod} object.
	 * @return true if the parameter should be added; false if it is should be ignored
	 */
	public boolean AddParameterFilter(final String name,
            final INamedParam parameter,
            final Class<? extends Annotation> annotationClass,
            final JMethod method);
	
	
	/**
	 * Sets the {@link org.aml.apimodel.Api}.
	 *
	 * @param raml a {@link org.aml.apimodel.Api} object.
	 */
	void setRaml(Api raml);
	
	/**
	 * Sets JCodeModel instance used through the generation process
	 *
	 * @param codeModel a {@link com.sun.codemodel.JCodeModel} object.
	 */
	void setCodeModel(JCodeModel codeModel);
}
