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
 * Generator extensions can extend this class
 *
 * @author pbober
 * @version $Id: $Id
 */
public abstract class AbstractGeneratorExtension implements NestedSchemaNameComputer {

	private Api raml;
	
	private JCodeModel codeModel;
	
	/** {@inheritDoc} */
	public void onAddResourceMethod(JMethod method,  Action action,  MimeType bodyMimeType,
			 Collection<MimeType> uniqueResponseMimeTypes) {

		
	}

	/** {@inheritDoc} */
	public String computeNestedSchemaName(MimeType mime) {
		return null;
	}

	/** {@inheritDoc} */
	public void onCreateResourceInterface(JDefinedClass resourceInterface, Resource resource) {

		
	}

	/** {@inheritDoc} */
	public boolean AddParameterFilter(String name,
             INamedParam parameter,
             Class<? extends Annotation> annotationClass,
             JMethod method) {
		return true;
	}

	/** {@inheritDoc} */
	public void setRaml(Api raml) {
		this.raml = raml;
	}
	
	/**
	 * <p>Getter for the field <code>raml</code>.</p>
	 *
	 * @return a {@link org.aml.apimodel.Api} object.
	 */
	protected  Api getRaml() {
		return raml;
	}

	/** {@inheritDoc} */
	public void setCodeModel(JCodeModel codeModel) {
		this.codeModel = codeModel;
	}

	/**
	 * <p>Getter for the field <code>codeModel</code>.</p>
	 *
	 * @return a {@link com.sun.codemodel.JCodeModel} object.
	 */
	public JCodeModel getCodeModel() {
		return codeModel;
	}
}
