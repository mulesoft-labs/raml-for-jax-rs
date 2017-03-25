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

import org.raml.api.RamlMediaType;
import org.raml.api.RamlResourceMethod;
import org.raml.emitter.plugins.BodiesAndResponses;
import org.raml.utilities.IndentedAppendable;

import java.io.IOException;
import java.util.List;

import static java.lang.String.format;

/**
 * Created by Jean-Philippe Belanger on 3/25/17. Just potential zeroes and ones
 */
class DefaultBodiesAndResponses implements BodiesAndResponses {

  @Override
  public void writeBody(IndentedAppendable writer, RamlResourceMethod method) throws IOException {

    writeBody(writer, method.getConsumedMediaTypes());
  }

  @Override
  public void writeResponses(IndentedAppendable writer, RamlResourceMethod method) throws IOException {

    writeResponses(writer, method.getProducedMediaTypes());
  }

  private void writeBody(IndentedAppendable writer, List<RamlMediaType> mediaTypes) throws IOException {
    writer.appendLine("body:");
    writer.indent();

    for (RamlMediaType mediaType : mediaTypes) {
      writer.appendLine(format("%s:", mediaType.toStringRepresentation()));
    }

    writer.outdent();
  }

  private void writeResponses(IndentedAppendable writer, List<RamlMediaType> producedMediaTypes) throws IOException {
    writer.appendLine("responses:");
    writer.indent();

    // We have no clue what the error responses are, however, we want to generate
    // well formed raml, so we pick one.
    writer.appendLine("200:");
    writer.indent();

    writeBody(writer, producedMediaTypes);

    writer.outdent();
    writer.outdent();
  }

}
