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

/**
 * A test extension that annotates each method and class with @TestAnnotation
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

public class TestGeneratorExtension implements GeneratorExtension {

	@Override
	public void onCreateResourceInterface(JDefinedClass resourceInterface, Resource resource) {

		resourceInterface.annotate(TestAnnotation.class);
	}

	@Override
	public void onAddResourceMethod(JMethod method, Action action, MimeType bodyMimeType,
			Collection<MimeType> uniqueResponseMimeTypes) {

		method.annotate(TestAnnotation.class);
		
	}

	@Override
	public void setRaml(Api raml) {
		
	}

	@Override
	public boolean AddParameterFilter(String name, INamedParam parameter,
			Class<? extends Annotation> annotationClass, JMethod method) {
		return true;
	}

	@Override
	public void setCodeModel(JCodeModel codeModel) {}
}
