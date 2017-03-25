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

import org.raml.api.RamlHeaderParameter;
import org.raml.api.RamlMediaType;
import org.raml.api.RamlApi;
import org.raml.api.RamlQueryParameter;
import org.raml.api.RamlResource;
import org.raml.api.RamlResourceMethod;
import org.raml.api.RamlTypes;
import org.raml.emitter.plugins.BodiesAndResponses;
import org.raml.utilities.IndentedAppendable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

public class IndentedAppendableEmitter implements Emitter {

  private static final Logger logger = LoggerFactory.getLogger(IndentedAppendableEmitter.class);

  private final IndentedAppendable writer;

  private IndentedAppendableEmitter(IndentedAppendable writer) {
    this.writer = writer;
  }

  public static IndentedAppendableEmitter create(IndentedAppendable appendable) {
    checkNotNull(appendable);

    return new IndentedAppendableEmitter(appendable);
  }

  @Override
  public void emit(RamlApi api) throws RamlEmissionException {
    try {
      writeApi(api);
    } catch (IOException e) {
      throw new RamlEmissionException(format("unable to emit api: %s", api), e);
    }
  }

  private void writeApi(RamlApi api) throws IOException {
    writeHeader();
    writeTitle(api.getTitle());
    writeVersion(api.getVersion());
    writeBaseUri(api.getBaseUri());
    writeDefaultMediaType(api.getDefaultMediaType());

    for (RamlResource resource : api.getResources()) {
      writeResource(resource);
    }
  }

  private void writeDefaultMediaType(RamlMediaType defaultMediaType) throws IOException {
    writer.appendLine(format("mediaType: %s", defaultMediaType.toStringRepresentation()));
  }

  private void writeResource(RamlResource resource) throws IOException {
    writer.appendLine(format("%s:", resource.getPath()));
    writer.indent();

    for (RamlResourceMethod method : resource.getMethods()) {
      writeMethod(method);
    }

    for (RamlResource child : resource.getChildren()) {
      writeResource(child);
    }

    writer.outdent();
  }

  private void writeMethod(RamlResourceMethod method) throws IOException {
    writer.appendLine(format("%s:", method.getHttpMethod()));
    writer.indent();

    Optional<String> description = method.getDescription();
    if (description.isPresent()) {
      writeDescription(description.get());
    }

    BodiesAndResponses bodiesAndResponses = pickBodyAndResponseEmitter();
    if (!method.getConsumedMediaTypes().isEmpty()) {

      bodiesAndResponses.writeBody(writer, method);
    }

    if (!method.getProducedMediaTypes().isEmpty()) {
      bodiesAndResponses.writeBody(writer, method);
    }

    if (!method.getHeaderParameters().isEmpty()) {
      writeHeaderParameters(method.getHeaderParameters());
    }

    if (!method.getQueryParameters().isEmpty()) {
      writeQueryParameters(method.getQueryParameters());
    }


    writer.outdent();
  }

  private BodiesAndResponses pickBodyAndResponseEmitter() {

    return new DefaultBodiesAndResponses();
  }

  private void writeDescription(String description) throws IOException {
    writer.appendLine(String.format("description: %s", description));
  }

  private void writeHeaderParameters(Iterable<RamlHeaderParameter> headerParameters)
      throws IOException {
    writer.appendLine("headers:");
    writer.indent();

    for (RamlHeaderParameter parameter : headerParameters) {
      writeHeaderParameter(parameter);
    }

    writer.outdent();
  }

  // TODO: remove this duplicate code
  private void writeHeaderParameter(RamlHeaderParameter parameter) throws IOException {
    writer.appendLine(String.format("%s:", parameter.getName()));
    writer.indent();
    writer.appendLine(format("type: %s", RamlTypes.fromType(parameter.getType()).getRamlSyntax()));

    Optional<String> defaultValue = parameter.getDefaultValue();
    if (defaultValue.isPresent()) {
      writer.appendLine(format("default: %s", defaultValue.get()));
      writer.appendLine("required: false");
    }

    writer.outdent();
  }

  private void writeQueryParameters(Iterable<RamlQueryParameter> queryParameters)
      throws IOException {
    writer.appendLine("queryParameters:");
    writer.indent();
    for (RamlQueryParameter queryParameter : queryParameters) {
      writeQueryParameter(queryParameter);
    }
    writer.outdent();
  }

  private void writeQueryParameter(RamlQueryParameter queryParameter) throws IOException {
    writer.appendLine(String.format("%s:", queryParameter.getName()));
    writer.indent();
    writer.appendLine(format("type: %s", RamlTypes.fromType(queryParameter.getType())
        .getRamlSyntax()));

    Optional<String> defaultValue = queryParameter.getDefaultValue();
    if (defaultValue.isPresent()) {
      writer.appendLine(format("default: %s", defaultValue.get()));
      writer.appendLine("required: false");
    }

    writer.outdent();
  }

  private void writeHeader() throws IOException {
    writer.appendLine("#%RAML 1.0");
  }

  private void writeTitle(String title) throws IOException {
    writer.appendLine(format("title: %s", title));
  }

  private void writeVersion(String version) throws IOException {
    writer.appendLine(format("version: %s", version));
  }

  private void writeBaseUri(String baseUri) throws IOException {
    writer.appendLine(format("baseUri: %s", baseUri));
  }

}
