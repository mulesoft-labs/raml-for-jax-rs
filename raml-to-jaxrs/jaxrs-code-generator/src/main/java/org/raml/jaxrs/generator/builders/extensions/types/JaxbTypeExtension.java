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
package org.raml.jaxrs.generator.builders.extensions.types;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.v10.V10GType;
import org.raml.jaxrs.generator.v10.types.V10GTypeUnion;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.UnionTypeDeclaration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Jean-Philippe Belanger on 11/30/16. Just potential zeroes and ones
 */
public class JaxbTypeExtension extends TypeExtensionHelper {

  @Override
  public void onTypeImplementation(CurrentBuild currentBuild, TypeSpec.Builder typeSpec,
                                   TypeDeclaration typeDeclaration) {

    typeSpec.addAnnotation(AnnotationSpec.builder(XmlRootElement.class)
        .addMember("name", "$S", typeDeclaration.name()).build());
    typeSpec.addAnnotation(AnnotationSpec.builder(XmlAccessorType.class)
        .addMember("value", "$T.$L", XmlAccessType.class, "FIELD").build());
  }

  @Override
  public void onFieldImplementation(CurrentBuild currentBuild, FieldSpec.Builder fieldSpec,
                                    TypeDeclaration typeDeclaration) {

    fieldSpec.addAnnotation(AnnotationSpec.builder(XmlElement.class)
        .addMember("name", "$S", typeDeclaration.name()).build());
  }

  @Override
  public void onGetterMethodImplementation(CurrentBuild currentBuild, MethodSpec.Builder typeSpec,
                                           TypeDeclaration typeDeclaration) {

  }

  @Override
  public void onEnumConstant(CurrentBuild currentBuild, TypeSpec.Builder builder,
                             TypeDeclaration typeDeclaration, String name) {

    builder.addAnnotation(AnnotationSpec.builder(XmlEnumValue.class).addMember("value", "$S", name)
        .build());
  }

  @Override
  public void onEnumerationClass(CurrentBuild currentBuild, TypeSpec.Builder builder,
                                 TypeDeclaration typeDeclaration) {

    builder.addAnnotation(AnnotationSpec.builder(XmlEnum.class).build());
  }

  @Override
  public void onUnionType(CurrentBuild currentBuild, TypeSpec.Builder builder, V10GType typeDeclaration) {

    V10GTypeUnion union = (V10GTypeUnion) typeDeclaration;
    UnionTypeDeclaration utd = (UnionTypeDeclaration) union.implementation();
  }
}
