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

import amf.client.model.document.Document;
import amf.client.model.document.Module;
import amf.client.model.domain.*;
import com.google.common.io.Files;
import org.raml.jaxrs.generator.*;
import org.raml.jaxrs.generator.GenerationException;
import org.raml.jaxrs.generator.Names;
import org.raml.jaxrs.generator.v10.types.V10GTypeFactory;
import org.raml.ramltopojo.*;
import org.raml.ramltopojo.amf.ExtraInformation;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Jean-Philippe Belanger on 12/6/16. Just potential zeroes and ones
 */
public class V10Finder implements GFinder {

  private Map<String, V10GType> foundTypes = new HashMap<>();

  @Override
  public ExtendedFoundCallback foundCallback(CurrentBuild currentBuild) {


    return (path, type, typeCreator) -> {

      // todo hide.
      ExtraInformation extraInformation = ExtraInformation.extraInformation();

      if (path.endMatches(
                          NamedElementPath.ANY_NAME, EndPoint.class,
                          NamedElementPath.ANY_NAME, Operation.class,
                          "application/x-www-form-urlencoded", Payload.class,
                          NamedElementPath.ANY_NAME) && type instanceof NodeShape) {

        NodeShape formParameters = (NodeShape) type;
        for (PropertyShape formParameter : formParameters.properties()) {

          // todo fix this
          if (extraInformation.isInline(formParameter.range())) {

            NamedElementPath newPath = path.removeEnd().append(formParameter);
            typeCreator.newTypeDeclaration(Names.ramlRawTypeName(newPath.names().toArray(new String[0])), type);
          }
        }
      }

      if (path.endMatches(EndPoint.class, Parameter.class)) {

        typeCreator.newTypeDeclaration(Names.ramlRawTypeName(path.names().toArray(new String[0])), type);
      }

      if (path.endMatches(EndPoint.class, Operation.class, Payload.class)) {

        typeCreator.newTypeDeclaration(Names.ramlRawTypeName(path.names().toArray(new String[0])), type);
        return;
      }

      if (path.endMatches(EndPoint.class, Operation.class, Parameter.class)) {

        // todo: check for path param or query param ?
        typeCreator.newTypeDeclaration(Names.ramlRawTypeName(path.names().toArray(new String[0])), type);
        return;
      }

      if (path.endMatches(EndPoint.class, Operation.class, Response.class, Payload.class)) {

        typeCreator.newTypeDeclaration(Names.ramlRawTypeName(path.names().toArray(new String[0])), type);
        return;
      }

      if (path.endMatches(EndPoint.class, Operation.class, Response.class, Parameter.class)) {

        typeCreator.newTypeDeclaration(Names.ramlRawTypeName(path.names().toArray(new String[0])), type);
      }
    };
  }

  private V10GType putInFoundTypes(String name, V10GType type) {

    foundTypes.put(name, type);
    return type;
  }

  @Override
  public void setupConstruction(CurrentBuild currentBuild) {


    List<V10GType> schemaTypes =
        foundTypes.values().stream().filter(input -> input.isJson() || input.isXml()).collect(Collectors.toList());

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
      return finder.putInFoundTypes(ramlName,
                                    V10GTypeFactory.createJson(schemaShape,
                                                               ramlName, suggestedJavaName, CreationModel.INLINE_FROM_TYPE));
    }
  }
}
