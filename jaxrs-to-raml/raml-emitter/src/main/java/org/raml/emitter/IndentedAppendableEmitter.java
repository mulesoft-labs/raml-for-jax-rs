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

import com.google.common.collect.Ordering;
import org.raml.api.*;
import org.raml.emitter.plugins.DefaultTypeHandler;
import org.raml.emitter.plugins.ResponseHandler;
import org.raml.emitter.plugins.DefaultResponseHandler;
import org.raml.jaxrs.emitters.AnnotationInstanceEmitter;
import org.raml.jaxrs.emitters.AnnotationTypeEmitter;
import org.raml.jaxrs.emitters.ParameterEmitter;
import org.raml.jaxrs.plugins.TypeHandler;
import org.raml.jaxrs.plugins.TypeSelector;
import org.raml.jaxrs.types.TypeRegistry;
import org.raml.jaxrs.common.RamlGenerator;
import org.raml.utilities.IndentedAppendable;
import org.raml.utilities.types.Cast;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

public class IndentedAppendableEmitter implements Emitter {

  private static final Logger logger = LoggerFactory.getLogger(IndentedAppendableEmitter.class);

  private TypeRegistry typeRegistry = new TypeRegistry();

  private List<ResponseHandler> responseHandlerAlternatives = Arrays.<ResponseHandler>asList(new DefaultResponseHandler());

  private final IndentedAppendable writer;
  private AnnotationTypeEmitter annotationTypeEmitter;
  private AnnotationInstanceEmitter annotationInstanceEmitter;

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
      this.annotationTypeEmitter = new AnnotationTypeEmitter(writer, api.getSupportedAnnotation());
      this.annotationInstanceEmitter = new AnnotationInstanceEmitter(writer, api.getSupportedAnnotation());
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
    writeSupportedAnnotations(api.getSupportedAnnotation());
    writer.deferAppends();
    for (RamlResource resource : api.getResources()) {
      writeResource(resource);
    }
    writer.stopDeferAppends();
    writeTypes();
    writer.flushDeferredContent();
  }

  private void writeSupportedAnnotations(List<RamlSupportedAnnotation> supportedAnnotation) throws IOException {

    if (supportedAnnotation.size() == 0) {
      return;
    }

    annotationTypeEmitter.emitAnnotations();
  }

  private void writeTypes() throws IOException {
    writer.appendLine("types:");
    writer.indent();
    typeRegistry.writeAll(annotationInstanceEmitter, writer);
    writer.outdent();
  }

  private void writeDefaultMediaType(RamlMediaType defaultMediaType) throws IOException {
    writer.appendEscapedLine("mediaType", defaultMediaType.toStringRepresentation());
  }

  private void writeResource(RamlResource resource)
      throws IOException {
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
    annotationInstanceEmitter.emit(method);

    Optional<String> description = method.getDescription();
    if (description.isPresent() && !description.get().isEmpty()) {
      writeDescription(description.get());
    }

    if (!method.getConsumedMediaTypes().isEmpty()
        && (method.getConsumedType().isPresent() || !method.getMultiFormDataParameter().isEmpty() || !method.getFormParameters()
            .isEmpty())) {

      writer.appendLine("body:");
      writer.indent();

      for (RamlMediaType ramlMediaType : method.getConsumedMediaTypes()) {

        if (ramlMediaType.toStringRepresentation().equals("multipart/form-data")) {

          writer.appendLine(ramlMediaType.toStringRepresentation() + ":");
          writer.indent();
          writeMultiPartFormData(method);
          writer.outdent();
        } else {
          if (ramlMediaType.toStringRepresentation().equals("application/x-www-form-urlencoded")) {

            writer.appendLine(ramlMediaType.toStringRepresentation() + ":");
            writer.indent();

            writeFormParam(method);

            writer.outdent();
          } else {
            Type type = method.getConsumedType().get().getType();

            TypeHandler typeHandler = pickTypeHandler(type);
            typeHandler.writeType(typeRegistry, writer, method.getConsumedType().get());
          }
        }
      }
      writer.outdent();
    }

    ResponseHandler handler = pickResponseHandler(method);
    TypeSelector selector = new TypeSelector() {

      @Override
      public TypeHandler pickTypeWriter(RamlResourceMethod method, RamlMediaType producedMediaType) throws IOException {
        return pickTypeHandler(method.getProducedType().get().getType());
      }
    };

    if (!method.getProducedMediaTypes().isEmpty()) {

      writer.appendLine("responses:");
      writer.indent();
      handler.writeResponses(typeRegistry, writer, method, selector);
      writer.outdent();
    }

    if (!method.getHeaderParameters().isEmpty()) {
      writeHeaderParameters(method.getHeaderParameters());
    }

    if (!method.getQueryParameters().isEmpty()) {
      writeQueryParameters(method.getQueryParameters());
    }


    writer.outdent();
  }

  private void writeMultiPartFormData(RamlResourceMethod method) throws IOException {

    writer.appendLine("type:");
    writer.indent();
    writer.appendLine("properties:");
    writer.indent();

    List<RamlMultiFormDataParameter> formData = method.getMultiFormDataParameter();
    for (RamlMultiFormDataParameter formDatum : formData) {

      Type type = formDatum.getPartEntity().getType();

      writer.appendLine(formDatum.getName() + ":");
      writer.indent();
      TypeHandler typeHandler = pickTypeHandler(type);
      typeHandler.writeType(typeRegistry, writer, formDatum.getPartEntity());
      writer.outdent();
    }

    writer.outdent();
    writer.outdent();
  }

  private void writeFormParam(RamlResourceMethod method) throws IOException {
    writer.appendLine("type:");
    writer.indent();
    writer.appendLine("properties:");
    writer.indent();

    List<RamlFormParameter> formData = method.getFormParameters();
    for (RamlFormParameter formDatum : formData) {

      writer.appendLine(formDatum.getName(), RamlTypes.fromType(formDatum.getType())
          .getRamlSyntax());
    }
    writer.outdent();
    writer.outdent();
  }

  private TypeHandler pickTypeHandler(Type type) throws IOException {

    Class castClass = Cast.toClass(type);

    RamlGenerator generatorAnnotation = ((Class<?>) castClass).getAnnotation(RamlGenerator.class);

    if (generatorAnnotation != null) {

      try {
        return generatorAnnotation.value().newInstance();
      } catch (InstantiationException | IllegalAccessException e) {
        logger.error("unable to create generator", e);
        throw new IOException("enable to create generator", e);
      }
    } else {

      return new DefaultTypeHandler();
    }
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
    writer.appendEscapedLine("description", description);
  }

  private void writeHeaderParameters(Iterable<RamlHeaderParameter> headerParameters)
      throws IOException {
    writer.appendLine("headers:");
    writer.indent();

    for (RamlHeaderParameter parameter : headerParameters) {

      TypeHandler typeHandler = pickTypeHandler(parameter.getEntity().getType());
      ParameterEmitter parameterEmitter = new ParameterEmitter(writer, typeRegistry, typeHandler);
      parameterEmitter.emit(parameter);
    }

    writer.outdent();
  }

  private void writeQueryParameters(Iterable<RamlQueryParameter> queryParameters)
      throws IOException {

    writer.appendLine("queryParameters:");
    writer.indent();
    for (RamlQueryParameter parameter : queryParameters) {

      TypeHandler typeHandler = pickTypeHandler(parameter.getEntity().getType());
      // typeHandler.writeType(typeRegistry, writer,parameter.getEntity() );

      ParameterEmitter parameterEmitter = new ParameterEmitter(writer, typeRegistry, typeHandler);
      parameterEmitter.emit(parameter);
    }

    writer.outdent();
  }

  private void writeHeader() throws IOException {
    writer.appendLine("#%RAML 1.0");
  }

  private void writeTitle(String title) throws IOException {
    writer.appendEscapedLine("title", title);
  }

  private void writeVersion(String version) throws IOException {
    writer.appendEscapedLine("version", version);
  }

  private void writeBaseUri(String baseUri) throws IOException {
    writer.appendEscapedLine("baseUri", baseUri);
  }

}
