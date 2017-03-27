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

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Ordering;
import org.raml.api.RamlHeaderParameter;
import org.raml.api.RamlMediaType;
import org.raml.api.RamlApi;
import org.raml.api.RamlQueryParameter;
import org.raml.api.RamlResource;
import org.raml.api.RamlResourceMethod;
import org.raml.api.RamlTypes;
import org.raml.emitter.plugins.DefaultTypeHandler;
import org.raml.emitter.plugins.BeanLikeTypes;
import org.raml.emitter.plugins.ResponseHandler;
import org.raml.emitter.plugins.DefaultResponseHandler;
import org.raml.emitter.plugins.SimpleJaxbTypes;
import org.raml.emitter.plugins.TypeHandler;
import org.raml.emitter.plugins.TypeSelector;
import org.raml.emitter.types.TypeRegistry;
import org.raml.utilities.IndentedAppendable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

public class IndentedAppendableEmitter implements Emitter {

  private static final Logger logger = LoggerFactory.getLogger(IndentedAppendableEmitter.class);

  private TypeRegistry typeRegistry = new TypeRegistry();

  private List<TypeHandler> bodyAlternatives =
      Arrays.asList(
                    new SimpleJaxbTypes(), new BeanLikeTypes(), new DefaultTypeHandler()
          );

  private List<ResponseHandler> responseHandlerAlternatives = Arrays.<ResponseHandler>asList(new DefaultResponseHandler());

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
      throw new RamlEmissionException(format("unable to emit api: %s", api.getBaseUri()), e);
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

    writeTypes();
  }

  private void writeTypes() throws IOException {
    writer.appendLine("types:");
    writer.indent();
    typeRegistry.writeAll(writer);
    writer.outdent();
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

    if (!method.getConsumedMediaTypes().isEmpty() && method.getConsumedType().isPresent()) {

      Type type = method.getConsumedType().get().getType();
      writer.appendLine("body:");
      writer.indent();

      for (RamlMediaType ramlMediaType : method.getConsumedMediaTypes()) {

        TypeHandler typeHandler = pickTypeHandler(method, ramlMediaType, type);
        typeHandler.writeType(typeRegistry, writer, ramlMediaType, method, method.getConsumedType().get().getType());
      }
    }

    ResponseHandler handler = pickResponseHandler(method);
    TypeSelector selector = new TypeSelector() {

      @Override
      public TypeHandler pickTypeWriter(RamlResourceMethod method, RamlMediaType producedMediaType) {
        return pickTypeHandler(method, producedMediaType, method.getProducedType().get().getType());
      }
    };

    if (!method.getProducedMediaTypes().isEmpty()) {

      writer.appendLine("responses:");
      writer.indent();
      handler.writeResponses(typeRegistry, writer, method, selector);
    }

    if (!method.getHeaderParameters().isEmpty()) {
      writeHeaderParameters(method.getHeaderParameters());
    }

    if (!method.getQueryParameters().isEmpty()) {
      writeQueryParameters(method.getQueryParameters());
    }


    writer.outdent();
  }

  private TypeHandler pickTypeHandler(final RamlResourceMethod method, RamlMediaType ramlMediaType,
                                      final Type type) {

    return FluentIterable.from(bodyAlternatives).filter(new Predicate<TypeHandler>() {

      @Override
      public boolean apply(TypeHandler input) {
        return input.handlesType(method, type);
      }
    }).first().get();
  }

  private ResponseHandler pickResponseHandler(final RamlResourceMethod method) {

    Ordering<ResponseHandler> bodies = new Ordering<ResponseHandler>() {

      @Override
      public int compare(ResponseHandler left, ResponseHandler right) {
        return left.handlesResponses(method) - right.handlesResponses(method);
      }
    };

    return bodies.max(this.responseHandlerAlternatives);
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
