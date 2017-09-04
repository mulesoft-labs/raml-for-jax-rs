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
import org.raml.api.*;
import org.raml.builder.*;
import org.raml.emitter.plugins.DefaultTypeHandler;
import org.raml.emitter.plugins.ResponseHandler;
import org.raml.jaxrs.common.RamlGenerator;
import org.raml.jaxrs.plugins.TypeHandler;
import org.raml.jaxrs.plugins.TypeSelector;
import org.raml.jaxrs.types.TypeRegistry;
import org.raml.utilities.types.Cast;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.internal.impl.commons.RamlHeader;
import org.raml.yagi.framework.model.NodeModel;
import org.raml.yagi.framework.nodes.ErrorNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.phase.GrammarPhase;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

import static java.lang.String.format;
import static org.raml.builder.BodyBuilder.body;
import static org.raml.builder.NodeBuilders.key;
import static org.raml.builder.NodeBuilders.property;
import static org.raml.builder.ResourceBuilder.resource;
import static org.raml.v2.api.model.v10.RamlFragment.Default;
import static org.raml.v2.internal.impl.commons.RamlVersion.RAML_10;

/**
 * Created. There, you have it.
 */
public class ModelEmitter implements Emitter {

  private TypeRegistry typeRegistry = new TypeRegistry();

  private PrintWriter writer;

  public ModelEmitter(PrintWriter writer) {

    this.writer = writer;
  }

  @Override
  public void emit(RamlApi modelApi) throws RamlEmissionException {

    org.raml.simpleemitter.Emitter emitter = new org.raml.simpleemitter.Emitter();

    RamlDocumentBuilder builder = RamlDocumentBuilder.document();
    try {
      builder
          .with(

                property("title", modelApi.getTitle()),
                property("baseUri", modelApi.getBaseUri()),
                property("version", modelApi.getVersion()),
                property("mediaType", modelApi.getDefaultMediaType().toStringRepresentation())
          );
      annotationTypes(builder, modelApi);
      resources(builder, modelApi);
    } catch (IOException e) {

      throw new RamlEmissionException("trying to emit", e);
    }

    Api api = builder.build();
    final GrammarPhase grammarPhase =
        new GrammarPhase(RamlHeader.getFragmentRule(new RamlHeader(RAML_10, Default).getFragment()));
    Node node = ((NodeModel) api).getNode();
    grammarPhase.apply(node);

    List<ErrorNode> errors = node.findDescendantsWith(ErrorNode.class);
    for (ErrorNode error : errors) {
      System.err.println("error: " + error.getErrorMessage());
    }
    if (errors.size() == 0) {
      try {
        emitter.emit(api, writer);
      } catch (IOException e) {
        throw new RamlEmissionException("trying to emit", e);
      }
    }

  }

  private void resources(RamlDocumentBuilder builder, RamlApi modelApi) throws IOException {

    for (RamlResource ramlResource : modelApi.getResources()) {

      ResourceBuilder resourceBuilder = ResourceBuilder.resource(ramlResource.getPath());
      for (RamlResourceMethod method : ramlResource.getMethods()) {
        writeMethod(resourceBuilder, method);
      }

      for (RamlResource child : ramlResource.getChildren()) {
        // writeResource(resourceBuilder, child);
      }

      builder.with(resourceBuilder);
    }
  }

  private void writeMethod(ResourceBuilder resourceBuilder, RamlResourceMethod method) throws IOException {

    MethodBuilder methodBuilder = MethodBuilder.method(method.getHttpMethod());

    // annotationInstanceEmitter.emit(method);

    Optional<String> description = method.getDescription();
    if (description.isPresent() && !description.get().isEmpty()) {

      methodBuilder.with(property("description", description.get()));
    }

    if (!method.getConsumedMediaTypes().isEmpty()
        && (method.getConsumedType().isPresent() || !method.getMultiFormDataParameter().isEmpty() || !method.getFormParameters()
            .isEmpty())) {

      for (RamlMediaType ramlMediaType : method.getConsumedMediaTypes()) {

        BodyBuilder body = body(ramlMediaType.toStringRepresentation());
        methodBuilder.withBodies(body);
        if (ramlMediaType.toStringRepresentation().equals("multipart/form-data")) {


          writeMultiPartFormData(method, body);
        } else {
          if (ramlMediaType.toStringRepresentation().equals("application/x-www-form-urlencoded")) {

            writeFormParam(method, body);
          } else {
            Type type = method.getConsumedType().get().getType();

            TypeHandler typeHandler = pickTypeHandler(type);
            typeHandler.writeType(typeRegistry, method.getConsumedType().get());
          }
        }

        resourceBuilder.with(methodBuilder);
      }
    }
    /*
     * ResponseHandler handler = pickResponseHandler(method); TypeSelector selector = new TypeSelector() {
     * 
     * @Override public TypeHandler pickTypeWriter(RamlResourceMethod method, RamlMediaType producedMediaType) throws IOException
     * { return pickTypeHandler(method.getProducedType().get().getType()); } };
     * 
     * if (!method.getProducedMediaTypes().isEmpty()) {
     * 
     * writer.appendLine("responses:"); writer.indent(); handler.writeResponses(typeRegistry, writer, method, selector);
     * writer.outdent(); }
     * 
     * if (!method.getHeaderParameters().isEmpty()) { writeHeaderParameters(method.getHeaderParameters()); }
     * 
     * if (!method.getQueryParameters().isEmpty()) { writeQueryParameters(method.getQueryParameters()); }
     * 
     * 
     * writer.outdent();
     */
  }

  private void writeFormParam(RamlResourceMethod method, BodyBuilder body) throws IOException {

    TypeBuilder typeBuilder = TypeBuilder.type("");

    List<RamlFormParameter> formData = method.getFormParameters();
    for (RamlFormParameter formDatum : formData) {

      typeBuilder.withProperty(property(formDatum.getName(), RamlTypes.fromType(formDatum.getType())
          .getRamlSyntax()));
    }

    body.withTypes(typeBuilder);
  }

  private void writeMultiPartFormData(RamlResourceMethod method, BodyBuilder body) throws IOException {

    TypeBuilder typeBuilder = TypeBuilder.type("");

    List<RamlMultiFormDataParameter> formData = method.getMultiFormDataParameter();
    for (RamlMultiFormDataParameter formDatum : formData) {

      Type type = formDatum.getPartEntity().getType();
      TypeHandler typeHandler = pickTypeHandler(type);

      typeBuilder.withProperty(property(formDatum.getName(), typeHandler.writeType(typeRegistry, formDatum.getPartEntity())));
    }

    body.withTypes(typeBuilder);
  }

  private TypeHandler pickTypeHandler(Type type) throws IOException {

    Class castClass = Cast.toClass(type);

    RamlGenerator generatorAnnotation = ((Class<?>) castClass).getAnnotation(RamlGenerator.class);

    if (generatorAnnotation != null) {

      try {
        return generatorAnnotation.value().newInstance();
      } catch (InstantiationException | IllegalAccessException e) {
        throw new IOException("enable to create generator", e);
      }
    } else {

      return new DefaultTypeHandler();
    }
  }

  private void annotationTypes(RamlDocumentBuilder builder, RamlApi modelApi) throws IOException {

    for (RamlSupportedAnnotation ramlSupportedAnnotation : modelApi.getSupportedAnnotation()) {

      AnnotationTypeBuilder annotationTypeBuilder =
          AnnotationTypeBuilder.annotationType(ramlSupportedAnnotation.getAnnotation().getSimpleName());

      Class<? extends Annotation> javaAnnotation = ramlSupportedAnnotation.getAnnotation();
      if (javaAnnotation.getDeclaredMethods().length > 0) {
        for (Method method : javaAnnotation.getDeclaredMethods()) {

          if (method.getReturnType().isArray()) {
            annotationTypeBuilder.withProperty(property(method.getName(), calculateRamlType(method.getReturnType()
                .getComponentType()) + "[]"));
          } else {
            annotationTypeBuilder.withProperty(property(method.getName(), calculateRamlType(method.getReturnType())
                ));
          }
        }

        builder.withAnnotationTypes(annotationTypeBuilder);
      }
    }
  }

  public String calculateRamlType(Class<?> type) throws IOException {

    if (Class.class.equals(type)) {

      return "string";
    }
    Optional<ScalarType> scalarType = ScalarType.fromType(type);
    if (scalarType.isPresent()) {

      return scalarType.get().getRamlSyntax();
    }

    throw new IOException("invalid type for annotation: " + type);
  }

}
