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
package org.raml.emitter;

import com.google.common.base.Optional;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.raml.api.*;
import org.raml.builder.*;
import org.raml.emitter.plugins.DefaultResponseHandler;
import org.raml.emitter.plugins.RamlToPojoTypeHandler;
import org.raml.emitter.plugins.ResponseHandler;
import org.raml.jaxrs.emitters.ModelEmitterAnnotations;
import org.raml.jaxrs.emitters.ParameterEmitter;
import org.raml.jaxrs.plugins.TypeHandler;
import org.raml.jaxrs.plugins.TypeSelector;
import org.raml.jaxrs.types.TypeRegistry;
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
import java.util.*;

import static org.raml.builder.BodyBuilder.body;
import static org.raml.builder.NodeBuilders.property;
import static org.raml.v2.api.model.v10.RamlFragment.Default;
import static org.raml.v2.internal.impl.commons.RamlVersion.RAML_10;

/**
 * Created. There, you have it.
 */
public class ModelEmitter implements Emitter {

  private TypeRegistry typeRegistry = new TypeRegistry();

  private final List<ResponseHandler> responseHandlerAlternatives = Collections
      .<ResponseHandler>singletonList(new DefaultResponseHandler());
  private List<RamlSupportedAnnotation> supportedAnnotations;
  private String topPackage;

  private PrintWriter writer;

  public ModelEmitter(PrintWriter writer) {

    this.writer = writer;
  }

  @Override
  public void emit(RamlApi modelApi) throws RamlEmissionException {

    supportedAnnotations = modelApi.getSupportedAnnotation();
    topPackage = modelApi.getTopPackage();

    org.raml.simpleemitter.Emitter emitter = new org.raml.simpleemitter.Emitter();

    RamlDocumentBuilder documentBuilder = RamlDocumentBuilder.document();
    try {
      documentBuilder
          .title(modelApi.getTitle())
          .baseUri(modelApi.getBaseUri())
          .mediaType(modelApi.getDefaultMediaType().toStringRepresentation())
          .version(modelApi.getVersion());

      annotationTypes(documentBuilder, modelApi);
      resources(documentBuilder, modelApi);

      typeRegistry.writeAll(supportedAnnotations, topPackage != null ? Package.getPackage(topPackage) : null, documentBuilder);

    } catch (IOException e) {

      throw new RamlEmissionException("trying to emit", e);
    }

    Api api = documentBuilder.buildModel();
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

      ResourceBuilder resourceBuilder = handleResource(ramlResource);

      builder.with(resourceBuilder);
    }
  }


  private void resources(ResourceBuilder builder, RamlResource ramlResource) throws IOException {

    ResourceBuilder resourceBuilder = handleResource(ramlResource);

    builder.with(resourceBuilder);
  }


  private ResourceBuilder handleResource(RamlResource ramlResource) throws IOException {
    ResourceBuilder resourceBuilder = ResourceBuilder.resource(ramlResource.getPath());
    Multimap<String, RamlResourceMethod> methods = ArrayListMultimap.create();

    for (RamlResourceMethod method : ramlResource.getMethods()) {
      String key = method.getHttpMethod();
      methods.put(key, method);
    }

    for (String key : methods.keySet()) {

      MethodBuilder methodBuilder = MethodBuilder.method(key);
      writeMethod(resourceBuilder, methods.get(key), methodBuilder);
      resourceBuilder.with(methodBuilder);
    }

    for (RamlResource child : ramlResource.getChildren()) {
      resources(resourceBuilder, child);
    }
    return resourceBuilder;
  }

  private void writeMethod(ResourceBuilder resourceBuilder, Collection<RamlResourceMethod> methods, MethodBuilder methodBuilder)
      throws IOException {

    for (RamlResourceMethod method : methods) {
      ModelEmitterAnnotations.annotate(supportedAnnotations, method, methodBuilder);

      Optional<String> description = method.getDescription();
      if (description.isPresent() && !description.get().isEmpty()) {

        methodBuilder.with(property("description", description.get()));
      }

      if (!method.getConsumedMediaTypes().isEmpty()
          && (method.getConsumedType().isPresent() || !method.getMultiFormDataParameter().isEmpty() || !method
              .getFormParameters()
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
              body.ofType(typeHandler.writeType(typeRegistry, method.getConsumedType().get()));
            }
          }

        }
      }


      if (!method.getHeaderParameters().isEmpty()) {
        writeHeaderParameters(method.getHeaderParameters(), methodBuilder);
      }

      if (!method.getQueryParameters().isEmpty()) {
        writeQueryParameters(method.getQueryParameters(), methodBuilder);
      }
    }

    ResponseHandler handler = pickResponseHandler();
    TypeSelector selector = new TypeSelector() {

      @Override
      public TypeHandler pickTypeWriter(RamlResourceMethod method, RamlMediaType producedMediaType) throws IOException {
        return pickTypeHandler(method.getProducedType().get().getType());
      }
    };
    handler.writeResponses(typeRegistry, methods, selector, methodBuilder);

  }


  private void writeHeaderParameters(Iterable<RamlHeaderParameter> headerParameters, MethodBuilder builder) throws IOException {
    for (RamlHeaderParameter parameter : headerParameters) {

      TypeHandler typeHandler = pickTypeHandler(parameter.getEntity().getType());
      ParameterEmitter parameterEmitter = new ParameterEmitter(typeRegistry, typeHandler);
      ParameterBuilder parameterBuilder = parameterEmitter.emit(parameter);
      builder.withHeaderParameters(parameterBuilder);
    }
  }

  private void writeQueryParameters(Iterable<RamlQueryParameter> queryParameters, MethodBuilder builder)
      throws IOException {

    for (RamlQueryParameter parameter : queryParameters) {

      TypeHandler typeHandler = pickTypeHandler(parameter.getEntity().getType());
      ParameterEmitter parameterEmitter = new ParameterEmitter(typeRegistry, typeHandler);
      ParameterBuilder parameterBuilder = parameterEmitter.emit(parameter);
      builder.withQueryParameter(parameterBuilder);
    }

  }

  private ResponseHandler pickResponseHandler() {

    return responseHandlerAlternatives.get(0);
  }

  private void writeFormParam(RamlResourceMethod method, BodyBuilder body) throws IOException {

    TypeBuilder typeBuilder = TypeBuilder.type("object");

    List<RamlFormParameter> formData = method.getFormParameters();
    for (RamlFormParameter formDatum : formData) {

      typeBuilder.withProperty(TypePropertyBuilder.property(formDatum.getName(), RamlTypes.fromType(formDatum.getType())
          .getRamlSyntax()));
    }

    body.ofType(typeBuilder);
  }

  private void writeMultiPartFormData(RamlResourceMethod method, BodyBuilder body) throws IOException {

    TypeBuilder typeBuilder = TypeBuilder.type("object");

    List<RamlMultiFormDataParameter> formData = method.getMultiFormDataParameter();
    for (RamlMultiFormDataParameter formDatum : formData) {

      Type type = formDatum.getPartEntity().getType();
      TypeHandler typeHandler = pickTypeHandler(type);

      typeBuilder.withProperty(TypePropertyBuilder.property(formDatum.getName(),
                                                            typeHandler.writeType(typeRegistry, formDatum.getPartEntity())));
    }

    body.ofType(typeBuilder);
  }

  private TypeHandler pickTypeHandler(Type type) throws IOException {

    return new RamlToPojoTypeHandler(topPackage != null ? Package.getPackage(topPackage) : null);
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


  private String calculateRamlType(Class<?> type) throws IOException {

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
