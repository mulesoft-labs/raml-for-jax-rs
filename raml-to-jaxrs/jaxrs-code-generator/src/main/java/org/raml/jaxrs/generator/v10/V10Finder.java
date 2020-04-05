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
import com.google.common.io.Files;
import org.raml.jaxrs.generator.*;
import org.raml.jaxrs.generator.v10.types.V10GTypeFactory;
import org.w3c.dom.Document;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;
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

      for (Parameter parameterTypeDeclaration : resource.parameters()) {

        if (!isInline(parameterTypeDeclaration.schema())) {
          continue;
        }

        V10GType type =
            createInlineFromEndPointsAndSuch(Names.ramlTypeName(resource, parameterTypeDeclaration),
                                             Names.javaTypeName(resource, parameterTypeDeclaration), parameterTypeDeclaration);
        listener.newTypeDeclaration(type);
      }

      for (Operation method : resource.methods()) {

        typesInBodies(resource, method, method.body(), listener);
      }
    }
  }

  private void typesInBodies(EndPoint resource, Operation method, List<Shape> body,
                             GFinderListener listener) {

    for (Shape typeDeclaration : body) {

      if (!isInline(typeDeclaration)) {
        continue;
      }

      if ("application/x-www-form-urlencoded".equals(typeDeclaration.name())) {

        ObjectTypeDeclaration formParameters = (ObjectTypeDeclaration) typeDeclaration;
        for (Shape formParameter : formParameters.properties()) {

          V10GType type =
              createInlineFromEndPointsAndSuch(Names.ramlTypeName(resource, method, formParameter),
                                               Names.javaTypeName(resource, method, formParameter), formParameter);
          listener.newTypeDeclaration(type);
        }
      } else {

        V10GType type =
            createInlineFromEndPointsAndSuch(Names.ramlTypeName(resource, method, typeDeclaration),
                                             Names.javaTypeName(resource, method, typeDeclaration), typeDeclaration);
        listener.newTypeDeclaration(type);
      }
    }

    for (Shape parameterTypeDeclaration : method.queryParameters()) {

      if (!isInline(parameterTypeDeclaration)) {
        continue;
      }

      V10GType type =
          createInlineFromEndPointsAndSuch(Names.ramlTypeName(resource, method, parameterTypeDeclaration),
                                           Names.javaTypeName(resource, method, parameterTypeDeclaration),
                                           parameterTypeDeclaration);
      listener.newTypeDeclaration(type);
    }

    for (Shape headerType : method.headers()) {

      if (!isInline(headerType)) {
        continue;
      }

      V10GType type =
          createInlineFromEndPointsAndSuch(Names.ramlTypeName(resource, method, headerType),
                                           Names.javaTypeName(resource, method, headerType), headerType);
      listener.newTypeDeclaration(type);
    }

    for (Response response : method.responses()) {
      for (Shape typeDeclaration : response.body()) {

        if (!isInline(typeDeclaration)) {
          continue;
        }

        V10GType type =
            createInlineFromEndPointsAndSuch(Names.ramlTypeName(resource, method, response, typeDeclaration),
                                             Names.javaTypeName(resource, method, response, typeDeclaration), typeDeclaration);
        listener.newTypeDeclaration(type);
      }
    }
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

    return TypeBasedOperation.run((AnyShape) typeDeclaration, new CreateType());
  }

  private boolean isInline(Shape typeDeclaration) {

    AnyShape actualShape = (AnyShape) typeDeclaration;
    if (actualShape instanceof SchemaShape) {

      return !foundTypes.containsKey(actualShape.name().value());
    } else {

      return build.fetchRamlToPojoBuilder().isInline(actualShape);
    }
// todo this seems not ok, and remove instaceofs
//    if (actualShape instanceof ObjectTypeDeclaration
//        || actualShape instanceof UnionTypeDeclaration
//        || (actualShape instanceof StringTypeDeclaration && !((StringTypeDeclaration) actualShape).enumValues().isEmpty())
//        || (actualShape instanceof NumberTypeDeclaration && !((NumberTypeDeclaration) actualShape).enumValues().isEmpty())) {
//
  }

  private V10GType createInlineFromEndPointsAndSuch(String ramlName, String suggestedJavaName, Shape typeDeclaration) {

    return TypeBasedOperation.run((AnyShape) typeDeclaration, new CreateTypeInline(ramlName, suggestedJavaName));
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


  class CreateType implements TypeBasedOperation<V10GType> { // todo should be external standalone class......
    @Override
    public V10GType on(AnyShape anyShape) {
      return null;
    }

    @Override
    public V10GType on(NodeShape nodeShape) {
      return putInFoundTypes(nodeShape.name().value(), V10GTypeFactory.createInlineType(nodeShape.name().value(), nodeShape));
    }

    @Override
    public V10GType on(ArrayShape arrayShape) {
      return putInFoundTypes(arrayShape.name().value(), V10GTypeFactory.createArray(arrayShape.name().value(),
              arrayShape
      ));
    }

    @Override
    public V10GType on(UnionShape unionShape) {
      return putInFoundTypes(unionShape.name().value(),
              V10GTypeFactory.createUnion(unionShape.name().value(), unionShape));
    }

    @Override
    public V10GType on(FileShape anyShape) {
      return null; // todo
    }

    @Override
    public V10GType on(ScalarShape scalarShape) {
      if ( scalarShape.values().size() != 0) {
        return putInFoundTypes(scalarShape.name().value(),
                V10GTypeFactory.createEnum(scalarShape.name().value(), scalarShape));
      } else {

        return putInFoundTypes(scalarShape.name().value(), V10GTypeFactory.createScalar(scalarShape.name().value(), scalarShape));
      }
    }

    @Override
    public V10GType on(SchemaShape schemaShape) {
      return putInFoundTypes(schemaShape.name().value(),
              V10GTypeFactory.createJson(schemaShape,
                      schemaShape.name().value(), CreationModel.INLINE_FROM_TYPE));
    }

    @Override
    public V10GType on(NilShape nilShape) {
      return null; // todo!
    }
  }

  // this is a bit wrong....
  class CreateTypeInline implements TypeBasedOperation<V10GType> {

    private final String ramlName;
    private final String suggestedJavaName;

    public CreateTypeInline(String ramlName, String suggestedJavaName) {
      this.ramlName = ramlName;
      this.suggestedJavaName = suggestedJavaName;
    }

    @Override
    public V10GType on(SchemaShape schemaShape) {
      return putInFoundTypes(ramlName, V10GTypeFactory.createJson(schemaShape,
              ramlName, suggestedJavaName, CreationModel.INLINE_FROM_TYPE));
    }

    @Override
    public V10GType on(NodeShape nodeShape) {
      return putInFoundTypes(ramlName, V10GTypeFactory.createInlineType(suggestedJavaName, nodeShape));
    }

    @Override
    public V10GType on(UnionShape unionShape) {

      return putInFoundTypes(ramlName, V10GTypeFactory.createUnion(suggestedJavaName, unionShape));
    }

    @Override
    public V10GType on(ScalarShape scalarShape) {
      if (! scalarShape.values().isEmpty()) {

        return putInFoundTypes(ramlName, V10GTypeFactory.createEnum(suggestedJavaName, scalarShape));
      } else {
        return putInFoundTypes(ramlName, V10GTypeFactory.createScalar(suggestedJavaName, scalarShape));
      }
    }

    @Override
    public V10GType on(ArrayShape arrayShape) {
      return putInFoundTypes(ramlName, V10GTypeFactory.createArray(ramlName,  arrayShape));
    }

    @Override
    public V10GType on(AnyShape anyShape) {
      return null;
    }

    @Override
    public V10GType on(FileShape anyShape) {
      return null;
    }

    @Override
    public V10GType on(NilShape nilShape) {
      return null;
    }
  }
}
