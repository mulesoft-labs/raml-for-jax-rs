/*
 * Copyright 2013-2018 (c) MuleSoft, Inc.
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

import amf.client.model.domain.*;
import com.github.jsonldjava.shaded.com.google.common.collect.Streams;
import com.google.common.io.Files;
import org.raml.jaxrs.generator.*;
import org.raml.jaxrs.generator.v10.types.V10GTypeFactory;
import org.w3c.dom.Document;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Jean-Philippe Belanger on 12/6/16. Just potential zeroes and ones
 */
public class V10Finder implements GFinder {

  private final CurrentBuild build;
  private final Document api;

  private Map<String, V10GType> foundTypes = new HashMap<>();

  public V10Finder(CurrentBuild build, Document api) {
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

  private void resourceTypes(List<EndPoint> resources, GFinderListener listener) {

    for (EndPoint resource : resources) {

      for (Parameter parameter : resource.parameters()) {

        if (!isInline(parameter.schema())) {
          continue;
        }

        V10GType type =
            createInlineFromEndPointsAndSuch(Names.ramlTypeName(resource, parameter),
                                             Names.javaTypeName(resource, parameter), parameter.schema());
        listener.newTypeDeclaration(type);
      }

      for (Operation method : resource.operations()) {

        List<Shape> bodyShapes = method.request().payloads().stream().map(Payload::schema).collect(Collectors.toList());
        typesInBodies(resource, method, bodyShapes, listener);
      }
    }
  }

  private void typesInBodies(EndPoint resource, Operation method, List<Shape> body,
                             GFinderListener listener) {

    for (Shape shape : body) {

      if (!isInline(shape)) {
        continue;
      }

      if ("application/x-www-form-urlencoded".equals(shape.name().value())) {

        NodeShape formParameters = (NodeShape) shape;
        for (Shape formParameter : formParameters.properties()) {

          V10GType type =
              createInlineFromEndPointsAndSuch(Names.ramlTypeName(resource, method, (AnyShape)formParameter),
                                               Names.javaTypeName(resource, method, (AnyShape)formParameter), formParameter);
          listener.newTypeDeclaration(type);
        }
      } else {

        V10GType type =
            createInlineFromEndPointsAndSuch(Names.ramlTypeName(resource, method, (AnyShape)shape),
                                             Names.javaTypeName(resource, method, (AnyShape)shape), shape);
        listener.newTypeDeclaration(type);
      }
    }

    Streams.concat(method.request().queryParameters().stream(), method.request().headers().stream())
            .map(Parameter::schema)
            .map(s -> (AnyShape)s)
            .filter(typeDeclaration1 -> !isInline(typeDeclaration1))
            .map(s -> createInlineFromEndPointsAndSuch(Names.ramlTypeName(resource, method, s),
                    Names.javaTypeName(resource, method, s),
                    s)).forEach(listener::newTypeDeclaration);


    method.responses().forEach( response ->
              response.payloads().stream()
              .map(Payload::schema)
                     .map(s -> (AnyShape)s)
                     .filter(this::isInline)
                     .map(s ->  createInlineFromEndPointsAndSuch(Names.ramlTypeName(resource, method, response, s),
                                      Names.javaTypeName(resource, method, response, s), s))
                     .forEach(listener::newTypeDeclaration));
  }

  private void localTypes(List<Shape> types, GFinderListener listener) {

    for (Shape typeDeclaration : types) {


      V10GType type = createTypeFromLibraryPart(typeDeclaration);
      listener.newTypeDeclaration(type);
    }
  }

  private V10GType putInFoundTypes(String name, V10GType type) {

    foundTypes.put(name, type);
    return type;
  }

  private V10GType createTypeFromLibraryPart(Shape typeDeclaration) {

    return TypeBasedOperation.run((AnyShape) typeDeclaration, new CreateType(this));
  }

  private boolean isInline(Shape typeDeclaration) {

    AnyShape actualShape = (AnyShape) typeDeclaration;
    if (actualShape instanceof SchemaShape) {

      return !foundTypes.containsKey(actualShape.name().value());
    } else {

      return build.fetchRamlToPojoBuilder().isInline(actualShape);
    }
  }

  private V10GType createInlineFromEndPointsAndSuch(String ramlName, String suggestedJavaName, Shape typeDeclaration) {

    return TypeBasedOperation.run((AnyShape) typeDeclaration, new CreateTypeInline(this, ramlName, suggestedJavaName));
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
      for (Shape typeDeclaration : library.types()) {

        V10GType type = createTypeFromLibraryPart(typeDeclaration);

        listener.newTypeDeclaration(type);
      }
    }
  }

  @Override
  public void setupConstruction(CurrentBuild currentBuild) {


    List<V10GType> schemaTypes = foundTypes.values().stream().filter(input -> input.isJson() || input.isXml()).collect(Collectors.toList());

    for (V10GType schemaType : schemaTypes) {

      try {
        Files.write(schemaType.schema(), new File(currentBuild.getSchemaRepository(), schemaType.name()),
                    Charset.defaultCharset());

      } catch (IOException e) {
        throw new GenerationException("while writing schemas", e);
      }
    }

    if (currentBuild.shouldCopySchemas()) {
      try {
        FileCopy.fromTo(new File("/funkytown"), currentBuild.getSchemaRepository()); // todo, no....
      } catch (IOException e) {

        throw new GenerationException("while copying schemas", e);
      }
    }

  }


  static class CreateType extends TypeBasedOperation.Default<V10GType> {

    private final V10Finder finder;

    public CreateType(V10Finder finder) {
      super((type) -> finder.putInFoundTypes(type.name().value(), V10GTypeFactory.createRamlToPojo(type.name().value(), type)));
      this.finder = finder;
    }

    // todo should be external standalone class......

    @Override
    public V10GType on(SchemaShape schemaShape) {
      return finder.putInFoundTypes(schemaShape.name().value(),
              V10GTypeFactory.createJson(schemaShape,
                      schemaShape.name().value(), CreationModel.INLINE_FROM_TYPE));
    }
  }

  // this is a bit wrong....
  static class CreateTypeInline extends TypeBasedOperation.Default<V10GType> {

    private final String ramlName;
    private final String suggestedJavaName;

    private final V10Finder finder;

    public CreateTypeInline(V10Finder finder, String ramlName, String suggestedJavaName) {
      super((type) -> finder.putInFoundTypes(ramlName, V10GTypeFactory.createRamlToPojo(suggestedJavaName, type)));
      this.finder = finder;
      this.ramlName = ramlName;
      this.suggestedJavaName = suggestedJavaName;
    }

    @Override
    public V10GType on(SchemaShape schemaShape) {
      return finder.putInFoundTypes(ramlName, V10GTypeFactory.createJson(schemaShape,
              ramlName, suggestedJavaName, CreationModel.INLINE_FROM_TYPE));
    }
  }
}
