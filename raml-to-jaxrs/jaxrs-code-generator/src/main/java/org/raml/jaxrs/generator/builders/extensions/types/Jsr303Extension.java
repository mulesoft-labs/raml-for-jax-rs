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
import com.squareup.javapoet.TypeName;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.v2.api.model.v10.datamodel.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Created by Jean-Philippe Belanger on 12/12/16. Just potential zeroes and ones
 */
public class Jsr303Extension extends TypeExtensionHelper {

  @Override
  public void onFieldImplementation(CurrentBuild currentBuild, FieldSpec.Builder typeSpec,
                                    TypeDeclaration typeDeclaration) {


    addFacetsForAll(typeSpec, typeDeclaration);

    if (typeDeclaration instanceof NumberTypeDeclaration) {

      addFacetsForNumbers(typeSpec, (NumberTypeDeclaration) typeDeclaration);
      return;
    }

    if (typeDeclaration instanceof StringTypeDeclaration) {

      addFacetsForString(typeSpec, (StringTypeDeclaration) typeDeclaration);
    }

    if (typeDeclaration instanceof ArrayTypeDeclaration) {

      addFacetsForArray(typeSpec, (ArrayTypeDeclaration) typeDeclaration);
    }

    if ( typeDeclaration instanceof ObjectTypeDeclaration ) {

      addFacetsForObject(typeSpec);
    }
  }

  private void addFacetsForObject(FieldSpec.Builder typeSpec) {

      typeSpec.addAnnotation(Valid.class);
  }

  private void addFacetsForAll(FieldSpec.Builder typeSpec, TypeDeclaration typeDeclaration) {

    if (typeDeclaration.required() != null && typeDeclaration.required()) {

      typeSpec.addAnnotation(AnnotationSpec.builder(NotNull.class).build());
    }
  }

  private void addFacetsForArray(FieldSpec.Builder typeSpec, ArrayTypeDeclaration typeDeclaration) {
    AnnotationSpec.Builder minMax = null;
    if (typeDeclaration.minItems() != null) {

      minMax =
          AnnotationSpec.builder(Size.class).addMember("min", "$L", typeDeclaration.minItems());
    }

    if (typeDeclaration.maxItems() != null) {

      if (minMax == null) {
        minMax =
            AnnotationSpec.builder(Size.class).addMember("max", "$L", typeDeclaration.maxItems());
      } else {

        minMax.addMember("max", "$L", typeDeclaration.maxItems());
      }
    }

    if (minMax != null) {
      typeSpec.addAnnotation(minMax.build());
    }
  }

  private void addFacetsForString(FieldSpec.Builder typeSpec, StringTypeDeclaration typeDeclaration) {

    AnnotationSpec.Builder minMax = null;
    if (typeDeclaration.minLength() != null) {

      minMax =
          AnnotationSpec.builder(Size.class).addMember("min", "$L", typeDeclaration.minLength());
    }

    if (typeDeclaration.maxLength() != null) {

      if (minMax == null) {
        minMax =
            AnnotationSpec.builder(Size.class).addMember("max", "$L", typeDeclaration.maxLength());
      } else {

        minMax.addMember("max", "$L", typeDeclaration.maxLength());
      }
    }

    if (minMax != null) {
      typeSpec.addAnnotation(minMax.build());
    }
  }


  private void addFacetsForNumbers(FieldSpec.Builder typeSpec, NumberTypeDeclaration typeDeclaration) {

    FieldSpec t = typeSpec.build();
    if (typeDeclaration.minimum() != null) {
      if (isInteger(t.type)) {

        typeSpec.addAnnotation(AnnotationSpec.builder(Min.class)
            .addMember("value", "$L", typeDeclaration.minimum().intValue()).build());
      }
    }

    if (typeDeclaration.maximum() != null) {
      if (isInteger(t.type)) {

        typeSpec.addAnnotation(AnnotationSpec.builder(Max.class)
            .addMember("value", "$L", typeDeclaration.maximum().intValue()).build());
      }
    }
  }

  private boolean isInteger(TypeName type) {

    return type.box().toString().equals(Integer.class.getName())
        || type.box().toString().equals(Short.class.getName())
        || type.box().toString().equals(Byte.class.getName())
        || type.box().toString().equals(BigDecimal.class.getName())
        || type.box().toString().equals(Long.class.getName())
        || type.box().toString().equals(BigInteger.class.getName());
  }
}
