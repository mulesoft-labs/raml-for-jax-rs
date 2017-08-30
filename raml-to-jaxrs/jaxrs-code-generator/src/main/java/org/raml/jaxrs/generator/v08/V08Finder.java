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
package org.raml.jaxrs.generator.v08;

import org.raml.jaxrs.generator.GAbstractionFactory;
import org.raml.jaxrs.generator.GFinder;
import org.raml.jaxrs.generator.GFinderListener;
import org.raml.jaxrs.generator.v10.TypeUtils;
import org.raml.jaxrs.generator.v10.V10GType;
import org.raml.v2.api.model.v08.api.Api;
import org.raml.v2.api.model.v08.api.GlobalSchema;
import org.raml.v2.api.model.v08.bodies.BodyLike;
import org.raml.v2.api.model.v08.bodies.Response;
import org.raml.v2.api.model.v08.methods.Method;
import org.raml.v2.api.model.v08.resources.Resource;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Jean-Philippe Belanger on 12/6/16. Just potential zeroes and ones
 */
public class V08Finder implements GFinder {

  private final Api api;
  private final GAbstractionFactory factory;
  private final V08TypeRegistry registry;
  private Set<String> globalSchemas = new HashSet<>();


  public V08Finder(Api api, GAbstractionFactory factory, V08TypeRegistry registry) {
    this.api = api;
    this.factory = factory;
    this.registry = registry;
  }

  @Override
  public GFinder findTypes(GFinderListener listener) {


    goThroughSchemas(api.schemas());

    resourceTypes(api.resources(), listener);

    return this;
  }

  private void goThroughSchemas(List<GlobalSchema> schemas) {


    for (GlobalSchema schema : schemas) {

      globalSchemas.add(schema.key());
    }

  }

  private void resourceTypes(List<Resource> resources, GFinderListener listener) {

    for (Resource resource : resources) {

      resourceTypes(resource.resources(), listener);
      for (Method method : resource.methods()) {

        typesInBodies(resource, method, method.body(), listener);
      }
    }
  }

  private void typesInBodies(Resource resource, Method method, List<BodyLike> body,
                             GFinderListener listener) {
    for (BodyLike typeDeclaration : body) {

      if (typeDeclaration.schema() == null) {

        continue;
      }

      if (globalSchemas.contains(typeDeclaration.schema().value())) {
        V08GType type = new V08GType(typeDeclaration.schema().value(), typeDeclaration);
        registry.addType(type);
        listener.newTypeDeclaration(type);
      } else {

        V08GType type = new V08GType(resource, method, typeDeclaration);
        registry.addType(type);

        listener.newTypeDeclaration(type);
      }
    }

    for (Response response : method.responses()) {
      for (BodyLike typeDeclaration : response.body()) {

        if (typeDeclaration.schema() == null) {
          continue;
        }
        if (globalSchemas.contains(typeDeclaration.schema().value())) {
          V08GType type = new V08GType(typeDeclaration.schema().value(), typeDeclaration);
          registry.addType(type);
          listener.newTypeDeclaration(type);
        } else {

          V08GType type = new V08GType(resource, method, response, typeDeclaration);
          registry.addType(type);
          listener.newTypeDeclaration(type);
        }
      }
    }
  }

  public Set<String> globalSchemas() {

    return globalSchemas;
  }
}
