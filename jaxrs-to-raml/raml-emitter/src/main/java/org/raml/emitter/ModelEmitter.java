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
import com.google.common.base.Suppliers;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.raml.api.*;
import org.raml.builder.*;
import org.raml.emitter.plugins.DefaultResponseHandler;
import org.raml.emitter.plugins.PojoToRamlTypeHandler;
import org.raml.emitter.plugins.ResponseHandler;
import org.raml.jaxrs.emitters.ModelEmitterAnnotations;
import org.raml.jaxrs.emitters.ParameterEmitter;
import org.raml.jaxrs.plugins.TypeHandler;
import org.raml.jaxrs.plugins.TypeSelector;
import org.raml.jaxrs.types.TypeRegistry;
import org.raml.pojotoraml.PojoToRaml;
import webapi.WebApiDocument;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Supplier;

import static org.raml.builder.PayloadBuilder.body;
import static org.raml.builder.PropertyShapeBuilder.property;

/**
 * Created. There, you have it.
 */
public class ModelEmitter implements Emitter {

  private Supplier<TypeHandler> typeHandler;
  private final TypeRegistry typeRegistry;
  private final Supplier<PojoToRaml> pojoToRaml;

  private final List<ResponseHandler> responseHandlerAlternatives = Collections
      .<ResponseHandler>singletonList(new DefaultResponseHandler());
  private List<RamlSupportedAnnotation> supportedAnnotations;
  private String topPackage;

  private PrintWriter writer;

  public ModelEmitter(PrintWriter writer, Supplier<PojoToRaml> pojoToRaml) {

    this.writer = writer;
    typeRegistry = new TypeRegistry(pojoToRaml);
    this.pojoToRaml = pojoToRaml;
  }

  @Override
  public void emit(RamlApi modelApi) throws RamlEmissionException {

    supportedAnnotations = modelApi.getSupportedAnnotation();
    typeHandler = Suppliers.memoize(() -> new PojoToRamlTypeHandler(pojoToRaml.get()));
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

      typeRegistry.writeAll(supportedAnnotations, topPackage, documentBuilder);

    } catch (IOException e) {

      throw new RamlEmissionException("trying to emit", e);
    }

    WebApiDocument api = documentBuilder.buildModel();
    try {
      emitter.emit(api, writer);
    } catch (IOException e) {
      throw new RamlEmissionException("trying to emit", e);
    }
  }

  private void resources(RamlDocumentBuilder builder, RamlApi modelApi) throws IOException {

    for (RamlResource ramlResource : modelApi.getResources()) {

      ResourceBuilder resourceBuilder = handleResource(builder, ramlResource, "");

      if (resourceBuilder != null) {
        builder.withResources(resourceBuilder);
      }
    }
  }


  private void resources(RamlDocumentBuilder ramlDocumentBuilder, ResourceBuilder builder, RamlResource ramlResource,
                         String parentPath)
      throws IOException {

    ResourceBuilder resourceBuilder = handleResource(ramlDocumentBuilder, ramlResource, parentPath);

    if (resourceBuilder != null) {
      ramlDocumentBuilder.withResources(resourceBuilder);
    }
  }


  private ResourceBuilder handleResource(RamlDocumentBuilder ramlDocumentBuilder, RamlResource ramlResource, String parentPath)
      throws IOException {
    ResourceBuilder resourceBuilder = ResourceBuilder.resource(parentPath + ramlResource.getPath());
    Multimap<String, RamlResourceMethod> methods = ArrayListMultimap.create();

    for (RamlResourceMethod method : ramlResource.getMethods()) {
      String key = method.getHttpMethod();
      methods.put(key, method);
    }

    for (String key : methods.keySet()) {

      OperationBuilder operationBuilder = OperationBuilder.method(key);
      writeMethod(methods.get(key), operationBuilder);
      resourceBuilder.withMethods(operationBuilder);
    }

    for (RamlResource child : ramlResource.getChildren()) {
      resources(ramlDocumentBuilder, resourceBuilder, child, parentPath + ramlResource.getPath());
    }

    if (methods.size() == 0) {
      return null;
    }
    return resourceBuilder;
  }

  private void writeMethod(Collection<RamlResourceMethod> methods, OperationBuilder operationBuilder)
      throws IOException {

    for (RamlResourceMethod method : methods) {
      ModelEmitterAnnotations.annotate(supportedAnnotations, method, operationBuilder);

      Optional<String> description = method.getDescription();
      if (description.isPresent() && !description.get().isEmpty()) {

        operationBuilder.description(description.get());
      }

      if (!method.getConsumedMediaTypes().isEmpty()
          && (method.getConsumedType().isPresent() || !method.getMultiFormDataParameter().isEmpty() || !method
              .getFormParameters()
              .isEmpty())) {

        for (RamlMediaType ramlMediaType : method.getConsumedMediaTypes()) {

          PayloadBuilder body = body(ramlMediaType.toStringRepresentation());
          operationBuilder.withPayloads(body);

          if (ramlMediaType.toStringRepresentation().equals("multipart/form-data")) {


            writeMultiPartFormData(method, body);
          } else {
            if (ramlMediaType.toStringRepresentation().equals("application/x-www-form-urlencoded")) {

              writeFormParam(method, body);
            } else {
              Type type = method.getConsumedType().get().getType();

              TypeHandler typeHandler = pickTypeHandler();
              body.ofType(typeHandler.writeType(typeRegistry, method.getConsumedType().get()));
            }
          }

        }
      }


      if (!method.getHeaderParameters().isEmpty()) {
        writeHeaderParameters(method.getHeaderParameters(), operationBuilder);
      }

      if (!method.getQueryParameters().isEmpty()) {
        writeQueryParameters(method.getQueryParameters(), operationBuilder);
      }
    }

    ResponseHandler handler = pickResponseHandler();
    TypeSelector selector = new TypeSelector() {

      @Override
      public TypeHandler pickTypeWriter(RamlResourceMethod method, RamlMediaType producedMediaType) throws IOException {
        return pickTypeHandler();
      }
    };
    handler.writeResponses(typeRegistry, methods, selector, operationBuilder);

  }


  private void writeHeaderParameters(Iterable<RamlHeaderParameter> headerParameters, OperationBuilder builder) throws IOException {
    for (RamlHeaderParameter parameter : headerParameters) {

      TypeHandler typeHandler = pickTypeHandler();
      ParameterEmitter parameterEmitter = new ParameterEmitter(typeRegistry, typeHandler);
      ParameterBuilder parameterBuilder = parameterEmitter.emit(parameter);
      builder.withHeaderParameters(parameterBuilder);
    }
  }

  private void writeQueryParameters(Iterable<RamlQueryParameter> queryParameters, OperationBuilder builder)
      throws IOException {

    for (RamlQueryParameter parameter : queryParameters) {

      TypeHandler typeHandler = pickTypeHandler();
      ParameterEmitter parameterEmitter = new ParameterEmitter(typeRegistry, typeHandler);
      ParameterBuilder parameterBuilder = parameterEmitter.emit(parameter);
      builder.withQueryParameter(parameterBuilder);
    }

  }

  private ResponseHandler pickResponseHandler() {

    return responseHandlerAlternatives.get(0);
  }

  private void writeFormParam(RamlResourceMethod method, PayloadBuilder body) throws IOException {

    NodeShapeBuilder typeBuilder = NodeShapeBuilder.inheritingObjectFromShapes();

    List<RamlFormParameter> formData = method.getFormParameters();
    for (RamlFormParameter formDatum : formData) {

      // todo cavalier way of doing it
      typeBuilder.withProperty(PropertyShapeBuilder.property(formDatum.getName(),
                                                             org.raml.pojotoraml.types.ScalarType.fromType(formDatum.getType())
                                                                 .get().getRamlSyntax(null).asTypeShapeBuilder()));
    }

    body.ofType(typeBuilder);
  }

  private void writeMultiPartFormData(RamlResourceMethod method, PayloadBuilder body) throws IOException {

    NodeShapeBuilder typeBuilder = TypeShapeBuilder.inheritingObjectFromShapes();

    List<RamlMultiFormDataParameter> formData = method.getMultiFormDataParameter();
    for (RamlMultiFormDataParameter formDatum : formData) {

      Type type = formDatum.getPartEntity().getType();
      TypeHandler typeHandler = pickTypeHandler();

      typeBuilder.withProperty(PropertyShapeBuilder.property(formDatum.getName(),
                                                             typeHandler.writeType(typeRegistry, formDatum.getPartEntity())));
    }

    body.ofType(typeBuilder);
  }

  private TypeHandler pickTypeHandler() throws IOException {

    return typeHandler.get();
  }

  private void annotationTypes(RamlDocumentBuilder builder, RamlApi modelApi) throws IOException {

    for (RamlSupportedAnnotation ramlSupportedAnnotation : modelApi.getSupportedAnnotation()) {

      AnnotationTypeBuilder annotationTypeBuilder =
          AnnotationTypeBuilder.annotationType(ramlSupportedAnnotation.getAnnotation().getSimpleName());

      Class<? extends Annotation> javaAnnotation = ramlSupportedAnnotation.getAnnotation();
      if (javaAnnotation.getDeclaredMethods().length > 0) {
        for (Method method : javaAnnotation.getDeclaredMethods()) {

          /*
           * if (method.getReturnType().isArray()) { annotationTypeBuilder.withProperty(property(method.getName(),
           * calculateRamlType(method.getReturnType() .getComponentType()) + "[]")); } else {
           * annotationTypeBuilder.withProperty(property(method.getName(), calculateRamlType(method.getReturnType()) )); }
           */
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
