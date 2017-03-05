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
package org.raml.jaxrs.generator.builders.extensions.types.jackson;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.TypeSpec;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.builders.extensions.types.TypeExtensionHelper;
import org.raml.jaxrs.generator.v10.Annotations;
import org.raml.jaxrs.generator.v10.V10GType;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;

/**
 * Created by Jean-Philippe Belanger on 1/1/17. Just potential zeroes and ones
 */
public class JacksonDiscriminatorInheritanceTypeExtension extends TypeExtensionHelper {

  @Override
  public void onTypeDeclaration(CurrentBuild currentBuild, TypeSpec.Builder typeSpec, V10GType type) {

    ObjectTypeDeclaration otr = (ObjectTypeDeclaration) type.implementation();

    if (otr.discriminator() != null && type.childClasses(type.name()).size() > 0) {

      typeSpec.addAnnotation(AnnotationSpec.builder(JsonTypeInfo.class)
          .addMember("use", "$T.Id.NAME", JsonTypeInfo.class)
          .addMember("include", "$T.As.PROPERTY", JsonTypeInfo.class)
          .addMember("property", "$S", otr.discriminator()).build());

      AnnotationSpec.Builder subTypes = AnnotationSpec.builder(JsonSubTypes.class);
      for (V10GType gType : type.childClasses(type.name())) {

        subTypes.addMember(
                           "value",
                           "$L",
                           AnnotationSpec
                               .builder(JsonSubTypes.Type.class)
                               .addMember("value", "$L",
                                          gType.defaultJavaTypeName(currentBuild.getModelPackage()) + ".class").build());
      }


      typeSpec.addAnnotation(subTypes.build());

    }

    if (otr.discriminatorValue() != null) {

      typeSpec.addAnnotation(AnnotationSpec.builder(JsonTypeName.class)
          .addMember("value", "$S", otr.discriminatorValue()).build());
    }
    if (type.childClasses(type.name()).size() == 0 && !Annotations.ABSTRACT.get(type)) {

      typeSpec.addAnnotation(AnnotationSpec.builder(JsonDeserialize.class)
          .addMember("as", "$L.class", type.javaImplementationName(currentBuild.getModelPackage()))
          .build());
    }
  }
}
