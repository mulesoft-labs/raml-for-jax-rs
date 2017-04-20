/*
 * Copyright 2013-2017 (c) MuleSoft, Inc.
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
package org.raml.jaxrs.parser.model;


import org.glassfish.jersey.server.model.RuntimeResource;

import static com.google.common.base.Preconditions.checkNotNull;

class JerseyJaxRsPath implements org.raml.jaxrs.model.Path {

  private final RuntimeResource runtimeResource;

  private JerseyJaxRsPath(RuntimeResource runtimeResource) {
    this.runtimeResource = runtimeResource;
  }

  public static JerseyJaxRsPath fromRuntimeResource(RuntimeResource runtimeResource) {
    checkNotNull(runtimeResource);

    return new JerseyJaxRsPath(runtimeResource);
  }

  @Override
  public String getStringRepresentation() {
    return runtimeResource.getPathPattern().getTemplate().getTemplate();
  }
}
