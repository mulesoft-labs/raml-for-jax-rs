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

import org.raml.model.Action;
import org.raml.model.MimeType;
import org.raml.model.Resource;

public interface MethodNameBuilderExtension extends GeneratorExtension {
	
	/**
	 * Compose name of JAX RS method
	 * @param action RAML method
	 * @param bodyMimeType mime type used
	 * @param resource action owner resource
	 * @return JAX RS method name
	 */
	String buildResourceMethodName(final Action action, final MimeType bodyMimeType, Resource resource);
}
