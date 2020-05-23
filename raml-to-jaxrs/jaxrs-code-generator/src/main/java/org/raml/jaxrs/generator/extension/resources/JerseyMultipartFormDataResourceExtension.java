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

import amf.client.model.domain.*;
import com.squareup.javapoet.*;
import org.raml.jaxrs.generator.GenerationException;
import org.raml.jaxrs.generator.extension.resources.api.GlobalResourceExtension;
import org.raml.jaxrs.generator.extension.resources.api.ResourceContext;

import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import java.io.InputStream;

/**
 * Created. There, you have it.
 */
public class JerseyMultipartFormDataResourceExtension extends GlobalResourceExtension.Helper {


  @Override
  public MethodSpec.Builder onMethod(ResourceContext context, Operation method, Request gRequest, Payload payload,
                                     MethodSpec.Builder methodSpec) {

    if (gRequest != null && "multipart/form-data".equals(payload.mediaType().value())) {

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

      NodeShape declaration = (NodeShape) payload.schema();
      for (PropertyShape property : declaration.properties()) {

        if (property.range() instanceof FileShape) {
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
          TypeName typeName =
              context.fetchRamlToPojoBuilder().fetchTypeName((AnyShape) property.range())
                  .orElseThrow(() -> new GenerationException("can't get type " + property.range().id()));
          newMethod.addParameter(
              ParameterSpec
                  .builder(typeName, property.name().value())
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
      if ((int) parameter.annotations.stream()
          .filter(annotationSpec -> annotationSpec.type.equals(ClassName.get(PathParam.class))
              || annotationSpec.type.equals(ClassName.get(QueryParam.class))).count() > 0) {
        newMethod.addParameter(parameter);
      }
    }
  }
}
