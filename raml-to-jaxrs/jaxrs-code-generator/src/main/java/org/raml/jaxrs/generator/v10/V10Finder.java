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

import org.raml.jaxrs.generator.GFinder;
import org.raml.jaxrs.generator.GFinderListener;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.api.Library;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.datamodel.StringTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Jean-Philippe Belanger on 12/6/16. Just potential zeroes and ones
 */
public class V10Finder implements GFinder {

  private final Api api;
  private final V10TypeRegistry registry;

  private Map<String, TypeDeclaration> foundTypes = new HashMap<>();

  public V10Finder(Api api, V10TypeRegistry registry) {
    this.api = api;
    this.registry = registry;
  }

  @Override
  public GFinder findTypes(GFinderListener listener) {

    if (api.uses() != null) {

      goThroughLibraries(new HashSet<String>(), api.uses(), listener);
    }

    localTypes(api.types(), listener);
    resourceTypes(api.resources(), listener);

    return this;
  }

  private void resourceTypes(List<Resource> resources, GFinderListener listener) {

    for (Resource resource : resources) {

      resourceTypes(resource.resources(), listener);
      for (TypeDeclaration parameterTypeDeclaration : resource.uriParameters()) {

        TypeDeclaration supertype = pullSupertype(parameterTypeDeclaration);
        if (supertype == null || !TypeUtils.shouldCreateNewClass(parameterTypeDeclaration, supertype)) {
          continue;
        }

        V10GType type = registry.fetchType(parameterTypeDeclaration.type(), parameterTypeDeclaration);
        listener.newTypeDeclaration(type);
      }

      for (Method method : resource.methods()) {

        typesInBodies(resource, method, method.body(), listener);
      }
    }
  }

  private void typesInBodies(Resource resource, Method method, List<TypeDeclaration> body,
                             GFinderListener listener) {
    for (TypeDeclaration typeDeclaration : body) {

      TypeDeclaration supertype = pullSupertype(typeDeclaration);
      if (supertype == null || !TypeUtils.shouldCreateNewClass(typeDeclaration, supertype)) {
        continue;
      }

      V10GType type = registry.fetchType(resource, method, typeDeclaration);
      listener.newTypeDeclaration(type);
    }

    for (TypeDeclaration parameterTypeDeclaration : method.queryParameters()) {

      TypeDeclaration supertype = pullSupertype(parameterTypeDeclaration);
      if (supertype == null || !TypeUtils.shouldCreateNewClass(parameterTypeDeclaration, supertype)) {
        continue;
      }

      V10GType type = registry.fetchType(resource, method, parameterTypeDeclaration);
      listener.newTypeDeclaration(type);
    }

    for (Response response : method.responses()) {
      for (TypeDeclaration typeDeclaration : response.body()) {
        TypeDeclaration supertype = pullSupertype(typeDeclaration);
        if (supertype == null || !TypeUtils.shouldCreateNewClass(typeDeclaration, supertype)) {
          continue;
        }

        V10GType type = registry.fetchType(resource, method, response, typeDeclaration);
        listener.newTypeDeclaration(type);
      }
    }
  }

  private TypeDeclaration pullSupertype(TypeDeclaration typeDeclaration) {

    // This allows us to find enumerations.
    if (typeDeclaration instanceof StringTypeDeclaration && typeDeclaration.type() != null
        && typeDeclaration.type().equals("string")
        && ((StringTypeDeclaration) typeDeclaration).enumValues().size() > 0) {

      return typeDeclaration.parentTypes().get(0);
    }

    return foundTypes.get(typeDeclaration.type());
  }


  private void localTypes(List<TypeDeclaration> types, GFinderListener listener) {

    for (TypeDeclaration typeDeclaration : types) {

      foundTypes.put(typeDeclaration.name(), typeDeclaration);

      V10GType type = registry.fetchType(typeDeclaration);
      listener.newTypeDeclaration(type);
    }
  }

  private void goThroughLibraries(Set<String> visitedLibraries, List<Library> libraries,
                                  GFinderListener listener) {

    for (Library library : libraries) {
      if (visitedLibraries.contains(library.name())) {

        continue;
      } else {

        visitedLibraries.add(library.name());
      }

      goThroughLibraries(visitedLibraries, library.uses(), listener);
      for (TypeDeclaration typeDeclaration : library.types()) {

        V10GType type = registry.fetchType(typeDeclaration);
        listener.newTypeDeclaration(type);
      }
    }
  }
}
