/*
 * Copyright 2015 (c) MuleSoft, Inc.
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
package org.raml.jaxrs.codegen.core;

import java.util.Collection;

import javax.ws.rs.core.Response;

import org.raml.jaxrs.codegen.core.ext.GeneratorExtension;
import org.raml.model.Action;
import org.raml.model.MimeType;

import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JDocComment;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JType;

public class ClientGenerator extends AbstractGenerator {

	protected void addResourceMethod(final JDefinedClass resourceInterface,
			final String resourceInterfacePath, final Action action,
			final MimeType bodyMimeType,
			final boolean addBodyMimeTypeInMethodName,
			final Collection<MimeType> uniqueResponseMimeTypes)
			throws Exception {
		final String methodName = Names.buildResourceMethodName(action,
				addBodyMimeTypeInMethodName ? bodyMimeType : null);
		final JType resourceMethodReturnType = getResourceMethodReturnType(
				methodName, action, uniqueResponseMimeTypes.isEmpty(),
				false, resourceInterface);
		final JMethod method = context.createResourceMethod(resourceInterface,
				methodName, resourceMethodReturnType);
		context.addHttpMethodAnnotation(action.getType().toString(), method);
		addParamAnnotation(resourceInterfacePath, action, method);
		addConsumesAnnotation(bodyMimeType, method);
		addProducesAnnotation(uniqueResponseMimeTypes, method);
		final JDocComment javadoc = addBaseJavaDoc(action, method);
		addParameters(action, bodyMimeType, method, javadoc);
		/* call registered extensions */
		for (GeneratorExtension e : extensions) {
			e.onAddResourceMethod(method, action, bodyMimeType,
					uniqueResponseMimeTypes);
		}
	}

	private void addParameters(final Action action,
			final MimeType bodyMimeType, final JMethod method,
			final JDocComment javadoc) throws Exception {
		addPathParameters(action, method, javadoc);
		addHeaderParameters(action, method, javadoc);
		addQueryParameters(action, method, javadoc);
		addBodyParameters(bodyMimeType, method, javadoc);
	}

	private JType getResourceMethodReturnType(String methodName, Action action,
			boolean empty, boolean async, JDefinedClass resourceInterface) {
		if (empty&&context.getConfiguration().isEmptyResponseReturnVoid())
        {
            return types.getGeneratorType(Response.Status.class);
        }
		return context.ref(Response.class.getName());
	}
}
