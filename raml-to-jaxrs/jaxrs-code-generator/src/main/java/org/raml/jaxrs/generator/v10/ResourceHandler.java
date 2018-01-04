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
package org.raml.jaxrs.generator.v10;

import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.GAbstractionFactory;
import org.raml.jaxrs.generator.builders.resources.ResourceBuilder;
import org.raml.jaxrs.generator.v08.V08TypeRegistry;
import org.raml.v2.api.model.v10.resources.Resource;

import java.util.Set;

/**
 * Created by Jean-Philippe Belanger on 10/26/16. These handlers take care of different model types (v08 vs v10).
 */
public class ResourceHandler {

  private final CurrentBuild build;

  public ResourceHandler(CurrentBuild build) {
    this.build = build;
  }

  public void handle(final Resource resource) {

    GAbstractionFactory factory = new GAbstractionFactory();

    ResourceBuilder rg =
        new ResourceBuilder(build, factory.newResource(build, resource),
                            resource.resourcePath(), resource.relativeUri().value());

    build.newResource(rg);
  }

  public void handle(Set<String> globalSchemas, V08TypeRegistry registry,
                     final org.raml.v2.api.model.v08.resources.Resource resource) {

    GAbstractionFactory factory = new GAbstractionFactory();

    ResourceBuilder rg =
        new ResourceBuilder(build, factory.newResource(globalSchemas, registry, resource),
                            resource.displayName(), resource.relativeUri().value());

    build.newResource(rg);
  }

}
