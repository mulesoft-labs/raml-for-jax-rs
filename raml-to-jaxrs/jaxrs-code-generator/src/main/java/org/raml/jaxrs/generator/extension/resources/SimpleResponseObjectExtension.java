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
import com.google.common.base.Supplier;
import com.google.common.collect.FluentIterable;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import org.raml.jaxrs.generator.extension.resources.api.GlobalResourceExtension;
import org.raml.jaxrs.generator.extension.resources.api.ResourceContext;
import org.raml.jaxrs.generator.ramltypes.GMethod;
import org.raml.jaxrs.generator.ramltypes.GRequest;
import org.raml.jaxrs.generator.ramltypes.GResource;
import org.raml.jaxrs.generator.ramltypes.GResponse;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import java.util.List;

/**
 * Created. There, you have it.
 */
public class SimpleResponseObjectExtension implements GlobalResourceExtension {

  private final List<String> arguments;

  public SimpleResponseObjectExtension(List<String> arguments) {
    this.arguments = arguments;
  }

  @Override
  public TypeSpec.Builder onResource(ResourceContext context, GResource resource, TypeSpec.Builder typeSpec) {
    return typeSpec;
  }

  @Override
  public MethodSpec.Builder onMethod(ResourceContext context, final GMethod method, GRequest gRequest,
                                     MethodSpec.Builder methodSpec) {

    if (method.responses().size() == 0) {

      return methodSpec.returns(Void.class);
    }

    methodSpec.addParameter(ParameterSpec.builder(ClassName.get(HttpServletResponse.class), "httpServletResponse")
        .addAnnotation(Context.class).build());

    // Find 200 or 201 response.
    Optional<GResponse> responseOptional = FluentIterable.from(method.responses()).firstMatch(findByCode("200"));
    GResponse response = responseOptional.or(new Supplier<GResponse>() {

      @Override
      public GResponse get() {
        return FluentIterable.from(method.responses()).firstMatch(findByCode("201")).orNull();
      }
    });

    if (response == null) {

      methodSpec.returns(ClassName.get(Void.class));
    } else {
      if (response.body().size() == 0) {

        return methodSpec.returns(Void.class);
      } else {
        methodSpec.returns(response.body().get(0).type().defaultJavaTypeName(context.getModelPackage()));
      }
    }

    return methodSpec;
  }

  private Predicate<GResponse> findByCode(final String code) {

    return new Predicate<GResponse>() {

      @Override
      public boolean apply(@Nullable GResponse gResponse) {
        return code.equals(gResponse.code());
      }
    };
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
