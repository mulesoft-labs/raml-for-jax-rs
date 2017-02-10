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
package org.raml.jaxrs.converter.model;

import org.raml.jaxrs.model.JaxRsResource;
import org.raml.api.RamlResource;
import org.raml.api.RamlResourceMethod;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.raml.jaxrs.converter.model.Utilities.toRamlMethods;
import static org.raml.jaxrs.converter.model.Utilities.toRamlResources;

public class JaxRsRamlResource implements RamlResource {

  private final JaxRsResource resource;

  private JaxRsRamlResource(JaxRsResource resource) {
    this.resource = resource;
  }

  public static JaxRsRamlResource create(JaxRsResource resource) {
    checkNotNull(resource);

    return new JaxRsRamlResource(resource);
  }

  @Override
  public String getPath() {
    return resource.getPath().getStringRepresentation();
  }

  @Override
  public List<RamlResource> getChildren() {
    return toRamlResources(this.resource.getChildren()).toList();
  }

  @Override
  public List<RamlResourceMethod> getMethods() {
    return toRamlMethods(this.resource.getMethods()).toList();
  }
}
