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
package org.raml.emitter;

import com.google.common.base.Optional;
import org.raml.api.RamlMediaType;
import org.raml.api.RamlResourceMethod;
import org.raml.api.ScalarType;
import org.raml.emitter.plugins.BodiesAndResponses;
import org.raml.utilities.IndentedAppendable;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import static java.lang.String.format;

/**
 * Created by Jean-Philippe Belanger on 3/25/17. Just potential zeroes and ones
 */
class DefaultBodiesAndResponses implements BodiesAndResponses {

  @Override
  public void writeBody(IndentedAppendable writer, RamlResourceMethod method) throws IOException {

    writeBody(writer, method, method.getConsumedMediaTypes(), method.getConsumedType());
  }

  @Override
  public void writeResponses(IndentedAppendable writer, RamlResourceMethod method) throws IOException {

    writeResponses(writer, method, method.getProducedMediaTypes());
  }

  private void writeBody(IndentedAppendable writer, RamlResourceMethod method, List<RamlMediaType> mediaTypes,
                         Optional<Type> bodyType) throws IOException {
    writer.appendLine("body:");
    writer.indent();

    for (RamlMediaType mediaType : mediaTypes) {
      writer.appendLine(format("%s:", mediaType.toStringRepresentation()));
      if (bodyType.isPresent()) {

        Type type = bodyType.get();
        if (ScalarType.fromType(type).isPresent()) {

          writer.indent();
          writer.appendLine("type: " + ScalarType.fromType(type).get().getRamlSyntax());
          writer.outdent();
        } else {

          throw new IOException(type + " is not a primitive type");
        }

      }
    }

    writer.outdent();
  }

  private void writeResponses(IndentedAppendable writer, RamlResourceMethod method,
                              List<RamlMediaType> producedMediaTypes) throws IOException {
    writer.appendLine("responses:");
    writer.indent();

    // We have no clue what the error responses are, however, we want to generate
    // well formed raml, so we pick one.
    writer.appendLine("200:");
    writer.indent();

    writeBody(writer, method, producedMediaTypes, method.getProducedType());

    writer.outdent();
    writer.outdent();
  }

}
