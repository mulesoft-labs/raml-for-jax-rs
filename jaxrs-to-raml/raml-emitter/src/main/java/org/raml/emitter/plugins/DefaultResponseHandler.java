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
package org.raml.emitter.plugins;

import org.raml.api.RamlMediaType;
import org.raml.api.RamlResourceMethod;
import org.raml.builder.PayloadBuilder;
import org.raml.builder.OperationBuilder;
import org.raml.builder.ResponseBuilder;
import org.raml.builder.TypeShapeBuilder;
import org.raml.jaxrs.plugins.TypeHandler;
import org.raml.jaxrs.plugins.TypeSelector;
import org.raml.jaxrs.types.TypeRegistry;

import java.io.IOException;
import java.util.Collection;

/**
 * Created by Jean-Philippe Belanger on 3/25/17. Just potential zeroes and ones
 */
public class DefaultResponseHandler implements ResponseHandler {


  @Override
  public int handlesResponses(RamlResourceMethod method) {
    return 0;
  }

  @Override
  public void writeResponses(TypeRegistry typeRegistry, Collection<RamlResourceMethod> methods, TypeSelector selector,
                             OperationBuilder operationBuilder)
      throws IOException {

    // We have no clue what the error responses are, however, we want to generate
    // well formed raml, so we pick one.
    ResponseBuilder responseBuilder = null;
    for (RamlResourceMethod method : methods) {

      if (!method.getProducedType().isPresent()) {
        continue;
      }

      for (RamlMediaType producedMediaType : method.getProducedMediaTypes()) {

        if (responseBuilder == null) {

          responseBuilder = ResponseBuilder.response(200);
        }

        PayloadBuilder body = PayloadBuilder.body(producedMediaType.toStringRepresentation());
        responseBuilder.withBodies(body);

        TypeHandler typeHandler = selector.pickTypeWriter(method, producedMediaType);
        TypeShapeBuilder type = typeHandler.writeType(typeRegistry, method.getProducedType().get());
        body.ofType(type);
      }

    }

    if (responseBuilder != null) {
      operationBuilder.withResponses(responseBuilder);
    }
  }

}
