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

import amf.client.model.domain.Operation;
import amf.client.model.domain.Payload;
import amf.client.model.domain.Request;
import amf.client.model.domain.Response;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.GenerationException;
import org.raml.ramltopojo.TypeBasedOperation;
import org.raml.jaxrs.generator.builders.resources.DefaultJavaTypeOperation;
import org.raml.jaxrs.generator.extension.resources.api.GlobalResourceExtension;
import org.raml.jaxrs.generator.extension.resources.api.ResourceContext;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Created. There, you have it.
 */
public class SimpleResponseObjectExtension extends GlobalResourceExtension.Helper {

  private final List<String> arguments;

  public SimpleResponseObjectExtension(List<String> arguments) {
    this.arguments = arguments;
  }

  @Override
  public MethodSpec.Builder onMethod(ResourceContext context, Operation method, Request gRequest, Payload payload,
                                     MethodSpec.Builder methodSpec) {

    if (method.responses().size() == 0) {

      return methodSpec.returns(Void.class);
    }

    methodSpec.addParameter(ParameterSpec.builder(ClassName.get(HttpServletResponse.class), "httpServletResponse")
        .addAnnotation(Context.class).build());

    // Find 200 or 201 response.
    Optional<Response> responseOptional = method.responses().stream().filter(findByCode("200")).findFirst();
    Response response = responseOptional.orElseGet(
        () -> method.responses().stream().filter(findByCode("201"))
            .findFirst()
            .orElse(null));

    if (response == null) {

      methodSpec.returns(ClassName.get(Void.class));
    } else {
      if (response.payloads().size() == 0) {

        return methodSpec.returns(Void.class);
      } else {
        methodSpec.returns(
            TypeBasedOperation.run(
                                   response.payloads().get(0).schema(),
                                   DefaultJavaTypeOperation.defaultJavaType(null/* todo this is wrong...*/))
                .orElseThrow(() -> new GenerationException("could not find type for "
                    + response.payloads().get(0).schema().name())));
      }
    }

    return methodSpec;
  }

  private Predicate<Response> findByCode(final String code) {

    return gResponse -> code.equals(gResponse.statusCode().value());
  }
}
