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

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.io.Files;
import org.raml.jaxrs.generator.*;
import org.raml.jaxrs.generator.v10.types.V10GTypeFactory;
import org.raml.ramltopojo.extensions.ObjectTypeHandlerPlugin;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.api.Library;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.datamodel.*;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Jean-Philippe Belanger on 12/6/16. Just potential zeroes and ones
 */
public class V10Finder implements GFinder {

  private final CurrentBuild build;
  private final Api api;

  private Map<String, V10GType> foundTypes = new HashMap<>();

  public V10Finder(CurrentBuild build, Api api) {
    this.build = build;
    this.api = api;
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

        if (!isInline(parameterTypeDeclaration)) {
          continue;
        }

        V10GType type =
            createInlineFromResourcesAndSuch(Names.ramlTypeName(resource, parameterTypeDeclaration),
                                             Names.javaTypeName(resource, parameterTypeDeclaration), parameterTypeDeclaration);
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

      if (!isInline(typeDeclaration)) {
        continue;
      }

      V10GType type =
          createInlineFromResourcesAndSuch(Names.ramlTypeName(resource, method, typeDeclaration),
                                           Names.javaTypeName(resource, method, typeDeclaration), typeDeclaration);
      listener.newTypeDeclaration(type);
    }

    for (TypeDeclaration parameterTypeDeclaration : method.queryParameters()) {

      if (!isInline(parameterTypeDeclaration)) {
        continue;
      }

      V10GType type =
          createInlineFromResourcesAndSuch(Names.ramlTypeName(resource, method, parameterTypeDeclaration),
                                           Names.javaTypeName(resource, method, parameterTypeDeclaration),
                                           parameterTypeDeclaration);
      listener.newTypeDeclaration(type);
    }

    for (TypeDeclaration headerType : method.headers()) {

      if (!isInline(headerType)) {
        continue;
      }

      V10GType type =
          createInlineFromResourcesAndSuch(Names.ramlTypeName(resource, method, headerType),
                                           Names.javaTypeName(resource, method, headerType), headerType);
      listener.newTypeDeclaration(type);
    }

    for (Response response : method.responses()) {
      for (TypeDeclaration typeDeclaration : response.body()) {

        if (!isInline(typeDeclaration)) {
          continue;
        }

        V10GType type =
            createInlineFromResourcesAndSuch(Names.ramlTypeName(resource, method, response, typeDeclaration),
                                             Names.javaTypeName(resource, method, response, typeDeclaration), typeDeclaration);
        listener.newTypeDeclaration(type);
      }
    }
  }

  private void localTypes(List<TypeDeclaration> types, GFinderListener listener) {

    for (TypeDeclaration typeDeclaration : types) {


      V10GType type = createTypeFromLibraryPart(typeDeclaration);
      listener.newTypeDeclaration(type);
    }
  }

  private V10GType putInFoundTypes(String name, V10GType type) {

    foundTypes.put(name, type);
    return type;
  }

  private V10GType createTypeFromLibraryPart(TypeDeclaration typeDeclaration) {

    if (typeDeclaration instanceof JSONTypeDeclaration) {

      return putInFoundTypes(typeDeclaration.name(),
                             V10GTypeFactory.createJson((JSONTypeDeclaration) typeDeclaration,
                                                        typeDeclaration.name(), CreationModel.INLINE_FROM_TYPE));
    }

    if (typeDeclaration instanceof XMLTypeDeclaration) {

      return putInFoundTypes(typeDeclaration.name(),
                             V10GTypeFactory.createXml((XMLTypeDeclaration) typeDeclaration,
                                                       typeDeclaration.name(), CreationModel.INLINE_FROM_TYPE));
    }

    if (typeDeclaration instanceof ObjectTypeDeclaration) {
      return putInFoundTypes(typeDeclaration.name(), V10GTypeFactory.createInlineType(typeDeclaration.name(), typeDeclaration));
    }

    if (typeDeclaration instanceof UnionTypeDeclaration) {
      return putInFoundTypes(typeDeclaration.name(),
                             V10GTypeFactory.createUnion(typeDeclaration.name(), (UnionTypeDeclaration) typeDeclaration));
    }

    if (typeDeclaration instanceof StringTypeDeclaration && !((StringTypeDeclaration) typeDeclaration).enumValues().isEmpty()) {

      return putInFoundTypes(typeDeclaration.name(),
                             V10GTypeFactory.createEnum(typeDeclaration.name(), (StringTypeDeclaration) typeDeclaration));
    }

    if (typeDeclaration instanceof ArrayTypeDeclaration) {

      return putInFoundTypes(typeDeclaration.name(), V10GTypeFactory.createArray(typeDeclaration.name(),
                                                                                 (ArrayTypeDeclaration) typeDeclaration
          ));
    }

    return putInFoundTypes(typeDeclaration.name(), V10GTypeFactory.createScalar(typeDeclaration.name(), typeDeclaration));
  }

  private boolean isInline(TypeDeclaration typeDeclaration) {

    if (typeDeclaration instanceof JSONTypeDeclaration || typeDeclaration instanceof XMLTypeDeclaration) {

      return !foundTypes.containsKey(typeDeclaration.type());
    }

    if (typeDeclaration instanceof ObjectTypeDeclaration
        || typeDeclaration instanceof UnionTypeDeclaration
        || (typeDeclaration instanceof StringTypeDeclaration && !((StringTypeDeclaration) typeDeclaration).enumValues().isEmpty())) {

      return build.fetchRamlToPojoBuilder().isInline(typeDeclaration);
    }

    return false;
  }

  private V10GType createInlineFromResourcesAndSuch(String ramlName, String suggestedJavaName, TypeDeclaration typeDeclaration) {

    if (typeDeclaration instanceof JSONTypeDeclaration) {

      return putInFoundTypes(ramlName, V10GTypeFactory.createJson((JSONTypeDeclaration) typeDeclaration,
                                                                  ramlName, suggestedJavaName, CreationModel.INLINE_FROM_TYPE));
    }

    if (typeDeclaration instanceof XMLTypeDeclaration) {

      return putInFoundTypes(ramlName, V10GTypeFactory.createXml((XMLTypeDeclaration) typeDeclaration,
                                                                 ramlName, suggestedJavaName, CreationModel.INLINE_FROM_TYPE));
    }

    if (typeDeclaration instanceof ObjectTypeDeclaration) {
      return putInFoundTypes(ramlName, V10GTypeFactory.createInlineType(suggestedJavaName, typeDeclaration));
    }

    if (typeDeclaration instanceof UnionTypeDeclaration) {
      return putInFoundTypes(ramlName, V10GTypeFactory.createUnion(suggestedJavaName, (UnionTypeDeclaration) typeDeclaration));
    }

    if (typeDeclaration instanceof StringTypeDeclaration && !((StringTypeDeclaration) typeDeclaration).enumValues().isEmpty()) {

      return putInFoundTypes(ramlName, V10GTypeFactory.createEnum(suggestedJavaName, (StringTypeDeclaration) typeDeclaration));
    }

    if (typeDeclaration instanceof ArrayTypeDeclaration) {

      return putInFoundTypes(ramlName, V10GTypeFactory.createArray(ramlName, (ArrayTypeDeclaration) typeDeclaration
          ));
    }

    return putInFoundTypes(ramlName, V10GTypeFactory.createScalar(ramlName, typeDeclaration));
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

        V10GType type = createTypeFromLibraryPart(typeDeclaration);

        listener.newTypeDeclaration(type);
      }
    }
  }

  @Override
  public void setupConstruction(CurrentBuild currentBuild) {


    List<V10GType> schemaTypes = FluentIterable.from(foundTypes.values()).filter(new Predicate<V10GType>() {

      @Override
      public boolean apply(@Nullable V10GType input) {
        return input.isJson() || input.isXml();
      }
    }).toList();

    for (V10GType schemaType : schemaTypes) {

      try {
        Files.write(schemaType.schema(), new File(currentBuild.getSchemaRepository(), schemaType.name()),
                    Charset.defaultCharset());
      } catch (IOException e) {
        throw new GenerationException("while writing schemas", e);
      }
    }
  }
}
