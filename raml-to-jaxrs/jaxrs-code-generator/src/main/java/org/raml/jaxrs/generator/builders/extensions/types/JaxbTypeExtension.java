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
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeSpec;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.builders.BuildPhase;
import org.raml.jaxrs.generator.extension.types.FieldType;
import org.raml.jaxrs.generator.extension.types.PredefinedFieldType;
import org.raml.jaxrs.generator.extension.types.TypeContext;
import org.raml.jaxrs.generator.v10.V10GProperty;
import org.raml.jaxrs.generator.v10.V10GType;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.UnionTypeDeclaration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Jean-Philippe Belanger on 11/30/16. Just potential zeroes and ones
 */
public class JaxbTypeExtension extends TypeExtensionHelper {

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
  public TypeSpec.Builder onType(TypeContext context, TypeSpec.Builder builder, V10GType type, BuildPhase buildPhase) {

    String namespace = type.xml().namespace() != null ? type.xml().namespace() : "##default";
    String name = type.xml().name() != null ? type.xml().name() : type.name();

    if (buildPhase == BuildPhase.IMPLEMENTATION) {
      builder.addAnnotation(AnnotationSpec.builder(XmlAccessorType.class)
          .addMember("value", "$T.$L", XmlAccessType.class, "FIELD").build());

      AnnotationSpec.Builder annotation = AnnotationSpec.builder(XmlRootElement.class)
          .addMember("namespace", "$S", namespace)
          .addMember("name", "$S", name);

      builder.addAnnotation(annotation.build());
    } else {

      builder.addAnnotation(AnnotationSpec.builder(XmlRootElement.class)
          .addMember("namespace", "$S", namespace)
          .addMember("name", "$S", name).build());
    }

    return builder;
  }



  @Override
  public FieldSpec.Builder onField(TypeContext context, TypeSpec.Builder typeSpec, FieldSpec.Builder fieldSpec,
                                   V10GType containingType,
                                   V10GProperty property, BuildPhase buildPhase, FieldType fieldType) {

    if (fieldType == PredefinedFieldType.PROPERTY && buildPhase == BuildPhase.IMPLEMENTATION) {

      String namespace = property.xml().namespace() != null ? property.xml().namespace() : "##default";
      String name = property.xml().name() != null ? property.xml().name() : property.name();

      if (property.xml().wrapped() != null && property.xml().wrapped() && property.type().isArray()) {

        fieldSpec.addAnnotation(
            AnnotationSpec.builder(XmlElementWrapper.class).addMember("name", "$S", name).build()
            );

        if (property.xml().attribute() != null && property.xml().attribute()) {
          fieldSpec.addAnnotation(
              AnnotationSpec.builder(XmlAttribute.class)
                  .addMember("name", "$S", property.type().arrayContents().name())
                  .addMember("namespace", "$S", namespace)
                  .build());
        } else {
          fieldSpec.addAnnotation(
              AnnotationSpec.builder(XmlElement.class)
                  .addMember("name", "$S", property.type().arrayContents().name())
                  .addMember("namespace", "$S", namespace)
                  .build());
        }
      } else {

        if (property.xml().attribute()) {
          fieldSpec.addAnnotation(
              AnnotationSpec.builder(XmlAttribute.class)
                  .addMember("name", "$S", name)
                  .addMember("namespace", "$S", namespace)
                  .build());
        } else {
          fieldSpec.addAnnotation(
              AnnotationSpec.builder(XmlElement.class)
                  .addMember("name", "$S", name)
                  .addMember("namespace", "$S", namespace)
                  .build());
        }
      }
    }


    if (fieldType == PredefinedFieldType.UNION) {

      UnionTypeDeclaration union = (UnionTypeDeclaration) containingType.implementation();

      AnnotationSpec.Builder elementsAnnotation = AnnotationSpec.builder(XmlElements.class);
      for (TypeDeclaration typeDeclaration : union.of()) {

        elementsAnnotation.addMember("value",
                                     "$L",
                                     AnnotationSpec
                                         .builder(XmlElement.class)
                                         .addMember("name", "$S", typeDeclaration.name())
                                         .addMember("type",
                                                    "$T.class",
                                                    ClassName.get(context.getModelPackage(), typeDeclaration.name()
                                                        + "Impl"))
                                         .build());
      }

      fieldSpec.addAnnotation(elementsAnnotation.build());
    }

    return fieldSpec;
  }
}
