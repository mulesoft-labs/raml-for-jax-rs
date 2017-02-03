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

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.v10.V10GType;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import javax.lang.model.element.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jean-Philippe Belanger on 12/15/16. Just potential zeroes and ones
 */
public class JacksonBasicExtension extends TypeExtensionHelper {

  public static final ParameterizedTypeName ADDITIONAL_PROPERTIES_TYPE = ParameterizedTypeName.get(
                                                                                                   Map.class, String.class,
                                                                                                   Object.class);

  @Override
  public void onTypeImplementation(CurrentBuild currentBuild, TypeSpec.Builder typeSpec,
                                   TypeDeclaration typeDeclaration) {

    ObjectTypeDeclaration obj = (ObjectTypeDeclaration) typeDeclaration;

    typeSpec.addAnnotation(AnnotationSpec.builder(JsonInclude.class)
        .addMember("value", "$T.$L", JsonInclude.Include.class, "NON_NULL").build());

    if (obj.discriminatorValue() != null) {

      typeSpec.addAnnotation(AnnotationSpec.builder(JsonTypeName.class)
          .addMember("value", "$S", obj.discriminatorValue()).build());
    }


    AnnotationSpec.Builder builder = AnnotationSpec.builder(JsonPropertyOrder.class);
    for (TypeDeclaration declaration : obj.properties()) {


      builder.addMember("value", "$S", declaration.name());
    }

    typeSpec.addAnnotation(builder.build());

    typeSpec.addField(FieldSpec
        .builder(ADDITIONAL_PROPERTIES_TYPE, "additionalProperties", Modifier.PRIVATE)
        .addAnnotation(AnnotationSpec.builder(JsonIgnore.class).build())
        .initializer(
                     CodeBlock.of("new $T()",
                                  ParameterizedTypeName.get(HashMap.class, String.class, Object.class))).build());

    typeSpec.addMethod(MethodSpec.methodBuilder("getAdditionalProperties")
        .returns(ADDITIONAL_PROPERTIES_TYPE).addModifiers(Modifier.PUBLIC)
        .addCode("return additionalProperties;\n").addAnnotation(JsonAnyGetter.class).build());

    typeSpec.addMethod(MethodSpec
        .methodBuilder("setAdditionalProperties")
        .returns(TypeName.VOID)
        .addParameter(
                      ParameterSpec.builder(ADDITIONAL_PROPERTIES_TYPE, "additionalProperties").build())
        .addAnnotation(JsonAnySetter.class)
        .addModifiers(Modifier.PUBLIC)
        .addCode(
                 CodeBlock.builder().add("this.additionalProperties = additionalProperties;\n").build())
        .build());

  }

  @Override
  public void onFieldImplementation(CurrentBuild currentBuild, FieldSpec.Builder fieldSpec,
                                    TypeDeclaration typeDeclaration) {

    fieldSpec.addAnnotation(AnnotationSpec.builder(JsonProperty.class)
        .addMember("value", "$S", typeDeclaration.name()).build());
  }


  @Override
  public void onGetterMethodImplementation(CurrentBuild currentBuild,
                                           MethodSpec.Builder methodSpec, TypeDeclaration typeDeclaration) {
    methodSpec.addAnnotation(AnnotationSpec.builder(JsonProperty.class)
        .addMember("value", "$S", typeDeclaration.name()).build());
  }

  @Override
  public void onSetterMethodImplementation(CurrentBuild currentBuild,
                                           MethodSpec.Builder methodSpec, ParameterSpec.Builder param,
                                           TypeDeclaration typeDeclaration) {
    methodSpec.addAnnotation(AnnotationSpec.builder(JsonProperty.class)
        .addMember("value", "$S", typeDeclaration.name()).build());
  }

  @Override
  public void onTypeDeclaration(CurrentBuild currentBuild, TypeSpec.Builder typeSpec, V10GType type) {

    typeSpec.addMethod(MethodSpec.methodBuilder("getAdditionalProperties")
        .returns(ADDITIONAL_PROPERTIES_TYPE).addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
        .build());

    typeSpec.addMethod(MethodSpec
        .methodBuilder("setAdditionalProperties")
        .returns(TypeName.VOID)
        .addParameter(
                      ParameterSpec.builder(ADDITIONAL_PROPERTIES_TYPE, "additionalProperties").build())
        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT).build());
  }

}
