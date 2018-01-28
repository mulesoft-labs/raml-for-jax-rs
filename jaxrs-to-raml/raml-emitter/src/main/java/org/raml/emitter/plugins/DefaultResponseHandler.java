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
package org.raml.emitter.plugins;

import org.raml.api.RamlMediaType;
import org.raml.api.RamlResourceMethod;
import org.raml.builder.BodyBuilder;
import org.raml.builder.MethodBuilder;
import org.raml.builder.ResponseBuilder;
import org.raml.builder.TypeBuilder;
import org.raml.jaxrs.plugins.TypeHandler;
import org.raml.jaxrs.plugins.TypeSelector;
import org.raml.jaxrs.types.TypeRegistry;

import java.io.IOException;

/**
 * Created by Jean-Philippe Belanger on 3/25/17. Just potential zeroes and ones
 */
public class DefaultResponseHandler implements ResponseHandler {


  @Override
  public int handlesResponses(RamlResourceMethod method) {
    return 0;
  }

  @Override
  public void writeResponses(TypeRegistry typeRegistry, RamlResourceMethod method, TypeSelector selector,
                             MethodBuilder methodBuilder)
      throws IOException {

    if (!method.getProducedType().isPresent()) {
      return;
    }

    // We have no clue what the error responses are, however, we want to generate
    // well formed raml, so we pick one.
    ResponseBuilder responseBuilder = ResponseBuilder.response(200);

    for (RamlMediaType producedMediaType : method.getProducedMediaTypes()) {

      BodyBuilder body = BodyBuilder.body(producedMediaType.toStringRepresentation());
      responseBuilder.withBodies(body);

      TypeHandler typeHandler = selector.pickTypeWriter(method, producedMediaType);
      TypeBuilder type = typeHandler.writeType(typeRegistry, method.getProducedType().get());
      body.ofType(type);
    }

    methodBuilder.withResponses(responseBuilder);
  }

}
