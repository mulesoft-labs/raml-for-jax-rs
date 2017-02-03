/*
 * Copyright ${licenseYear} (c) MuleSoft, Inc.
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
package org.raml.jaxrs.generator.builders.extensions.types;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import joptsimple.internal.Strings;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.Names;
import org.raml.jaxrs.generator.builders.CodeContainer;
import org.raml.jaxrs.generator.builders.JavaPoetTypeGenerator;
import org.raml.jaxrs.generator.v10.Annotations;
import org.raml.jaxrs.generator.v10.V10GType;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.UnionTypeDeclaration;

import javax.annotation.Nullable;
import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by Jean-Philippe Belanger on 1/2/17. Just potential zeroes and ones
 */
public class UnionDeserializationGenerator implements JavaPoetTypeGenerator {

  private final CurrentBuild currentBuild;
  private final V10GType unionTypeDeclaration;
  private final ClassName name;

  public UnionDeserializationGenerator(CurrentBuild currentBuild, V10GType unionTypeDeclaration,
                                       ClassName name) {
    this.currentBuild = currentBuild;
    this.unionTypeDeclaration = unionTypeDeclaration;
    this.name = name;
  }

  @Override
  public void output(CodeContainer<TypeSpec.Builder> rootDirectory) throws IOException {

    UnionTypeDeclaration union = (UnionTypeDeclaration) unionTypeDeclaration.implementation();

    ClassName unionTypeName =
        ClassName.get(currentBuild.getModelPackage(),
                      Annotations.CLASS_NAME.get(Names.typeName(union.name()), unionTypeDeclaration));
    TypeSpec.Builder builder =
        TypeSpec
            .classBuilder(name)
            .superclass(
                        ParameterizedTypeName.get(ClassName.get(StdDeserializer.class), unionTypeName))
            .addMethod(
                       MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC)
                           .addCode("super($T.class);", unionTypeName).build()

            ).addModifiers(Modifier.PUBLIC);

    MethodSpec.Builder deserialize =
        MethodSpec
            .methodBuilder("deserialize")
            .addModifiers(Modifier.PUBLIC)
            .addParameter(
                          ParameterSpec.builder(ClassName.get(JsonParser.class), "jsonParser").build())
            .addParameter(
                          ParameterSpec.builder(ClassName.get(DeserializationContext.class), "jsonContext")
                              .build())
            .addException(IOException.class)
            .addException(JsonProcessingException.class)
            .returns(unionTypeName)
            .addStatement("$T mapper  = new $T()", ObjectMapper.class, ObjectMapper.class)
            .addStatement("$T<String, Object> map = mapper.readValue(jsonParser, Map.class)",
                          Map.class);

    for (TypeDeclaration typeDeclaration : union.of()) {

      ClassName unionPossibility =
          ClassName.get(currentBuild.getModelPackage(), Names.typeName(typeDeclaration.name()));

      String fieldName = typeDeclaration.name();
      deserialize.addStatement("if ( looksLike" + fieldName
          + "(map) ) return new $T(mapper.convertValue(map, $T.class))", unionTypeName,
                               unionPossibility);
      buildLooksLike(builder, typeDeclaration);
    }

    deserialize.addStatement("throw new $T($S + map)", IOException.class,
                             "Can't figure out type of object");
    builder.addMethod(deserialize.build());

    rootDirectory.into(builder);
  }

  private void buildLooksLike(TypeSpec.Builder builder, TypeDeclaration typeDeclaration) {

    String name = Names.methodName("looksLike", typeDeclaration.name());
    MethodSpec.Builder spec =
        MethodSpec.methodBuilder(name).addParameter(
                                                    ParameterizedTypeName.get(ClassName.get(Map.class),
                                                                              ClassName.get(String.class),
                                                                              ClassName.get(Object.class)), "map");
    if (typeDeclaration instanceof ObjectTypeDeclaration) {

      ObjectTypeDeclaration otd = (ObjectTypeDeclaration) typeDeclaration;
      List<String> names =
          Lists.transform(otd.properties(), new Function<TypeDeclaration, String>() {

            @Nullable
            @Override
            public String apply(@Nullable TypeDeclaration input) {
              return "\"" + input.name() + "\"";
            }
          });

      spec.addStatement("return map.keySet().containsAll($T.asList($L))", Arrays.class,
                        Strings.join(names, ","));
    }

    spec.addModifiers(Modifier.PRIVATE).returns(TypeName.BOOLEAN);
    builder.addMethod(spec.build());
  }

  @Override
  public void output(CodeContainer<TypeSpec.Builder> rootDirectory, TYPE type) throws IOException {

    output(rootDirectory);
  }

  @Override
  public TypeName getGeneratedJavaType() {
    return name;
  }
}
