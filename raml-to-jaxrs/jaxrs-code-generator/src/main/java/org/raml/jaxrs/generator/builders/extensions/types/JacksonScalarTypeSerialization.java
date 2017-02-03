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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeSpec;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.v2.api.model.v10.datamodel.DateTimeOnlyTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.DateTimeTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.DateTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TimeOnlyTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

/**
 * Created by Jean-Philippe Belanger on 1/8/17. Just potential zeroes and ones
 */
public class JacksonScalarTypeSerialization extends TypeExtensionHelper {

  @Override
  public void onFieldImplementation(CurrentBuild currentBuild, FieldSpec.Builder builder,
                                    TypeDeclaration typeDeclaration) {

    if (typeDeclaration instanceof DateTimeOnlyTypeDeclaration) {

      builder.addAnnotation(AnnotationSpec.builder(JsonFormat.class)
          .addMember("shape", "$T.STRING", JsonFormat.Shape.class)
          .addMember("pattern", "$S", "yyyy-MM-dd'T'HH:mm:ss.SSSZ").build());
    }

    if (typeDeclaration instanceof TimeOnlyTypeDeclaration) {

      builder.addAnnotation(AnnotationSpec.builder(JsonFormat.class)
          .addMember("shape", "$T.STRING", JsonFormat.Shape.class)
          .addMember("pattern", "$S", "HH:mm:ss").build());
    }

    if (typeDeclaration instanceof DateTypeDeclaration) {

      builder.addAnnotation(AnnotationSpec.builder(JsonFormat.class)
          .addMember("shape", "$T.STRING", JsonFormat.Shape.class)
          .addMember("pattern", "$S", "yyyy-MM-dd").build());
    }

    if (typeDeclaration instanceof DateTimeTypeDeclaration) {

      String format = ((DateTimeTypeDeclaration) typeDeclaration).format();
      if (format != null && "rfc2616".equals(format)) {

        builder.addAnnotation(AnnotationSpec.builder(JsonFormat.class)
            .addMember("shape", "$T.STRING", JsonFormat.Shape.class)
            .addMember("pattern", "$S", "EEE, dd MMM yyyy HH:mm:ss z").build());
      } else {
        builder.addAnnotation(AnnotationSpec.builder(JsonFormat.class)
            .addMember("shape", "$T.STRING", JsonFormat.Shape.class)
            .addMember("pattern", "$S", "yyyy-MM-dd'T'HH:mm:ssZ").build());
      }
    }
  }

  @Override
  public void onEnumConstant(CurrentBuild currentBuild, TypeSpec.Builder builder,
                             TypeDeclaration typeDeclaration, String name) {


    builder.addAnnotation(AnnotationSpec.builder(JsonProperty.class).addMember("value", "$S", name)
        .build());
  }
}
