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
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import org.raml.jaxrs.generator.extension.resources.api.GlobalResourceExtension;
import org.raml.jaxrs.generator.extension.resources.api.ResourceContext;
import org.raml.ramltopojo.extensions.jsr303.AnnotationAdder;
import org.raml.ramltopojo.extensions.jsr303.FacetValidation;

import javax.validation.Valid;
import java.util.Optional;

/**
 * Created. There, you have it.
 */
public class Jsr303ResourceExtension extends GlobalResourceExtension.Helper {


  @Override
  public MethodSpec.Builder onMethod(ResourceContext context, Operation method, Request request, Payload payload,
                                     MethodSpec.Builder methodSpec) {
    MethodSpec spec = methodSpec.build();
    MethodSpec.Builder builder = MethodSpec.methodBuilder(spec.name);
    builder.addAnnotations(spec.annotations);

    if (spec.code != null) {
      builder.addCode(spec.code);
    }
    if (spec.defaultValue != null) {
      builder.defaultValue(spec.defaultValue);
    }

    builder.addExceptions(spec.exceptions);
    if (spec.javadoc != null) {
      builder.addJavadoc("$L", spec.javadoc);
    }

    builder.addModifiers(spec.modifiers);

    for (final ParameterSpec parameter : spec.parameters) {

      final ParameterSpec.Builder parameterBuilder = parameter.toBuilder();
      Optional<Parameter> declaration = getQueryParameters(method, parameter.name);
      if (declaration.isPresent()) {
        FacetValidation.addAnnotations((AnyShape) declaration.get().schema(), new AnnotationAdder() {

          @Override
          public TypeName typeName() {
            return parameterBuilder.build().type;
          }

          @Override
          public void addAnnotation(AnnotationSpec spec) {
            parameterBuilder.addAnnotation(spec);
          }
        });

        builder.addParameter(parameterBuilder.build());
      } else {

        if ("entity".equals(parameter.name)) {
          FacetValidation.addAnnotations((AnyShape) payload.schema(), new AnnotationAdder() {

            @Override
            public TypeName typeName() {
              return parameterBuilder.build().type;
            }

            @Override
            public void addAnnotation(AnnotationSpec spec) {
              parameterBuilder.addAnnotation(spec);
            }
          });

          builder.addParameter(parameterBuilder.build());
          builder.addParameter(parameterBuilder.addAnnotation(Valid.class).build());
        } else {

          builder.addParameter(parameter);
        }
      }
    }

    if (spec.returnType != null) {
      builder.returns(spec.returnType);
    }

    builder.addTypeVariables(spec.typeVariables);
    builder.varargs(spec.varargs);
    return builder;
  }


  Optional<Parameter> getQueryParameters(Operation operation, final String name) {
    return operation.request().queryParameters().stream().filter(gParameter -> gParameter.name().equals(name)).findFirst();
  }
}
