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
package org.raml.jaxrs.generator.extension.resources;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.squareup.javapoet.*;
import org.raml.jaxrs.generator.extension.resources.api.GlobalResourceExtension;
import org.raml.jaxrs.generator.extension.resources.api.ResourceContext;
import org.raml.jaxrs.generator.ramltypes.GMethod;
import org.raml.jaxrs.generator.ramltypes.GRequest;
import org.raml.jaxrs.generator.ramltypes.GResource;
import org.raml.jaxrs.generator.ramltypes.GResponse;
import org.raml.v2.api.model.v10.datamodel.FileTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import javax.annotation.Nullable;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import java.io.InputStream;

/**
 * Created. There, you have it.
 */
public class JerseyMultipartFormDataResourceExtension implements GlobalResourceExtension {

  @Override
  public TypeSpec.Builder onResource(ResourceContext context, GResource resource, TypeSpec.Builder typeSpec) {
    return typeSpec;
  }

  @Override
  public MethodSpec.Builder onMethod(ResourceContext context, GMethod method, GRequest gRequest, MethodSpec.Builder methodSpec) {

    if (gRequest != null && "multipart/form-data".equals(gRequest.mediaType())) {

      MethodSpec old = methodSpec.build();
      MethodSpec.Builder newMethod = MethodSpec.methodBuilder(old.name)
          .returns(old.returnType);

      addExistingParameters(old, newMethod);

      for (AnnotationSpec annotation : old.annotations) {
        newMethod.addAnnotation(annotation);
      }

      for (TypeName exception : old.exceptions) {

        newMethod.addException(exception);
      }

      newMethod.addJavadoc("$L", old.javadoc);
      newMethod.addModifiers(old.modifiers);

      ObjectTypeDeclaration declaration = (ObjectTypeDeclaration) gRequest.type().implementation();
      for (TypeDeclaration property : declaration.properties()) {

        if (property instanceof FileTypeDeclaration) {
          newMethod.addParameter(
              ParameterSpec
                  .builder(ClassName.get(InputStream.class), property.name() + "Stream")
                  .addAnnotation(
                                 AnnotationSpec
                                     .builder(ClassName.bestGuess("org.glassfish.jersey.media.multipart.FormDataParam"))
                                     .addMember("value", "$S", property.name()).build()).build());
          newMethod.addParameter(
              ParameterSpec
                  .builder(ClassName.bestGuess("org.glassfish.jersey.media.multipart.FormDataContentDisposition"),
                           property.name() + "Disposition")
                  .addAnnotation(
                                 AnnotationSpec
                                     .builder(ClassName.bestGuess("org.glassfish.jersey.media.multipart.FormDataParam"))
                                     .addMember("value", "$S", property.name()).build()).build());

        } else {
          TypeName typeName = context.fetchRamlToPojoBuilder().fetchType(property.type(), property);
          methodSpec.addParameter(
              ParameterSpec
                  .builder(typeName, property.name())
                  .addAnnotation(AnnotationSpec
                      .builder(ClassName.bestGuess("org.glassfish.jersey.media.multipart.FormDataParam"))
                      .addMember("value", "$S", property.name()).build())
                  .build());
        }
      }

      return newMethod;
    } else {
      return methodSpec;
    }
  }

  private void addExistingParameters(MethodSpec old, MethodSpec.Builder newMethod) {
    for (ParameterSpec parameter : old.parameters) {
      if (FluentIterable.from(parameter.annotations).filter(new Predicate<AnnotationSpec>() {

        @Override
        public boolean apply(@Nullable AnnotationSpec annotationSpec) {

          return annotationSpec.type.equals(ClassName.get(PathParam.class))
              || annotationSpec.type.equals(ClassName.get(QueryParam.class));
        }
      }).size() > 0) {
        newMethod.addParameter(parameter);
      }
    }
  }

  @Override
  public TypeSpec.Builder onResponseClass(ResourceContext context, GMethod method, TypeSpec.Builder typeSpec) {
    return typeSpec;
  }

  @Override
  public MethodSpec.Builder onMethod(ResourceContext context, GResponse responseMethod, MethodSpec.Builder methodSpec) {
    return methodSpec;
  }
}
