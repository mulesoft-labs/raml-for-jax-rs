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

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.squareup.javapoet.*;
import org.raml.jaxrs.generator.extension.resources.api.GlobalResourceExtension;
import org.raml.jaxrs.generator.extension.resources.api.ResourceContext;
import org.raml.jaxrs.generator.ramltypes.*;
import org.raml.jaxrs.generator.v10.V10GMethod;
import org.raml.ramltopojo.extensions.jsr303.AnnotationAdder;
import org.raml.ramltopojo.extensions.jsr303.FacetValidation;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import javax.annotation.Nullable;
import javax.validation.Valid;

/**
 * Created. There, you have it.
 */
public class Jsr303ResourceExtension implements GlobalResourceExtension {

  @Override
  public TypeSpec.Builder onResource(ResourceContext context, GResource resource, TypeSpec.Builder typeSpec) {
    return typeSpec;
  }

  @Override
  public MethodSpec.Builder onMethod(ResourceContext context, GMethod method, GRequest gRequest, MethodSpec.Builder methodSpec) {

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
      Optional<GParameter> declaration = getQueryParameters((V10GMethod) method, parameter.name);
      if (declaration.isPresent()) {
        FacetValidation.addAnnotations((TypeDeclaration) declaration.get().implementation(), new AnnotationAdder() {

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
          FacetValidation.addAnnotations((TypeDeclaration) gRequest.type().implementation(), new AnnotationAdder() {

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
          // builder.addParameter(parameterBuilder.addAnnotation(Valid.class).build());
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

  Optional<GParameter> getQueryParameters(V10GMethod request, final String name) {
    return FluentIterable.from(request.queryParameters()).firstMatch(new Predicate<GParameter>() {

      @Override
      public boolean apply(@Nullable GParameter gParameter) {
        return gParameter.name().equals(name);
      }
    });
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
