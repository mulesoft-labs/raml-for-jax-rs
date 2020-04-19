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
package org.raml.jaxrs.generator.builders.resources;

import amf.client.model.domain.*;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.squareup.javapoet.*;
import org.raml.jaxrs.generator.*;
import org.raml.jaxrs.generator.builders.CodeContainer;
import org.raml.jaxrs.generator.builders.JavaPoetTypeGeneratorBase;
import org.raml.jaxrs.generator.builders.extensions.resources.ResourceContextImpl;
import org.raml.jaxrs.generator.extension.resources.api.ResourceClassExtension;
import org.raml.jaxrs.generator.extension.resources.api.ResourceContext;
import org.raml.jaxrs.generator.v10.V10GType;

import javax.lang.model.element.Modifier;
import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.*;
import java.util.*;
import java.util.stream.Collectors;

import static org.raml.jaxrs.generator.builders.resources.DefaultJavaTypeOperation.defaultJavaType;

/**
 * Created by Jean-Philippe Belanger on 10/27/16. Abstraction of creation.
 */
public class ResourceBuilder implements ResourceGenerator {

  private final CurrentBuild build;
  private final EndPoint topResource;
  private final String name;
  private final String uri;

  public ResourceBuilder(CurrentBuild build, EndPoint resource, String name, String uri) {

    this.build = build;
    this.topResource = resource;
    this.name = name;
    this.uri = uri;
  }

  @Override
  public void output(CodeContainer<TypeSpec> container) throws IOException {

    TypeSpec.Builder typeSpec = new DefaultResourceClassCreator().onResource(new ResourceContextImpl(build), topResource, null);

    if (typeSpec != null) {
      container.into(typeSpec.build());
    }
  }

  private void buildResource(TypeSpec.Builder typeSpec, EndPoint endPoint) {

    Multimap<Operation, Payload> incomingBodies = ArrayListMultimap.create();
    Multimap<Operation, Response> responses = ArrayListMultimap.create();
    ResourceUtils.fillInBodiesAndResponses(endPoint, incomingBodies, responses);

    Map<String, TypeSpec.Builder> responseSpecs = createResponseClass(typeSpec, endPoint, incomingBodies, responses);

    for (Operation operation : endPoint.operations()) {

      String methodName = Names.resourceMethodName(endPoint, operation);
      Set<String> mediaTypesForMethod = fetchAllMediaTypesForMethodResponses(operation);
      if (operation.request().payloads().size() == 0) {

        createMethodWithoutBody(typeSpec, endPoint, operation, mediaTypesForMethod, HashMultimap.<String, String>create(),
                                methodName,
                                responseSpecs);
      } else {
        Multimap<String, String> ramlTypeToMediaType = accumulateMediaTypesPerType(incomingBodies, operation);
        for (Payload payload : operation.request().payloads()) {

          if (payload.schema() == null) {

            createMethodWithoutBody(typeSpec, endPoint, operation, mediaTypesForMethod, ramlTypeToMediaType, methodName,
                                    responseSpecs);
            continue;
          }

          if (ramlTypeToMediaType.containsKey(payload.schema().name().value())) {
            createMethodWithBody(typeSpec, endPoint, operation, ramlTypeToMediaType, methodName, payload, responseSpecs,
                                 mediaTypesForMethod);
            ramlTypeToMediaType.removeAll(payload.schema().name().value());
          }
        }
      }
    }
  }

  private Multimap<String, String> accumulateMediaTypesPerType(Multimap<Operation, Payload> incomingBodies,
                                                               Operation gMethod) {
    Multimap<String, String> ramlTypeToMediaType = ArrayListMultimap.create();
    for (Payload payload : incomingBodies.get(gMethod)) {
      if (payload != null) {
        if (payload.schema() == null) {
          ramlTypeToMediaType.put(null, payload.mediaType().value());
        } else {
          ramlTypeToMediaType.put(payload.schema().name().value(), payload.mediaType().value());
        }
      }
    }
    return ramlTypeToMediaType;
  }

  private void createMethodWithoutBody(TypeSpec.Builder typeSpec, EndPoint endPoint, Operation operation,
                                       Set<String> mediaTypesForMethod,
                                       Multimap<String, String> ramlTypeToMediaType, String methodName,
                                       Map<String, TypeSpec.Builder> responseSpecs) {

    MethodSpec.Builder methodSpec = createMethodBuilder(endPoint, operation, methodName, mediaTypesForMethod, responseSpecs);
    handleMethodConsumer(methodSpec, ramlTypeToMediaType, null);
    typeSpec.addMethod(methodSpec.build());
    // todo plugin
    // methodSpec =
    // build
    // .pluginsForResourceMethod(new Function<Collection<ResourceMethodExtension<GMethod>>, ResourceMethodExtension<GMethod>>() {
    //
    // @Nullable
    // @Override
    // public ResourceMethodExtension<GMethod> apply(@Nullable Collection<ResourceMethodExtension<GMethod>>
    // resourceMethodExtensions) {
    // return new ResourceMethodExtension.Composite(resourceMethodExtensions);
    // }
    // }, operation)
    // .onMethod(new ResourceContextImpl(build),
    // operation, null, methodSpec);
    //

  }

  private void createMethodWithBody(TypeSpec.Builder typeSpec, EndPoint endPoint, Operation operation,
                                    Multimap<String, String> ramlTypeToMediaType, String methodName, Payload payload,
                                    Map<String, TypeSpec.Builder> responseSpec, Set<String> mediaTypesForMethod) {

    MethodSpec.Builder methodSpec = createMethodBuilder(endPoint, operation, methodName, mediaTypesForMethod, responseSpec);

    createParameter(endPoint, methodSpec, payload, operation);

    handleMethodConsumer(methodSpec, ramlTypeToMediaType, (AnyShape) payload.schema());

    typeSpec.addMethod(methodSpec.build());

    // todo plugin.
    // methodSpec =
    // build
    // .pluginsForResourceMethod(new Function<Collection<ResourceMethodExtension<GMethod>>, ResourceMethodExtension<GMethod>>() {
    //
    // @Nullable
    // @Override
    // public ResourceMethodExtension<GMethod> apply(@Nullable Collection<ResourceMethodExtension<GMethod>>
    // resourceMethodExtensions) {
    // return new ResourceMethodExtension.Composite(resourceMethodExtensions);
    // }
    // }, operation)
    // .onMethod(new ResourceContextImpl(build),
    // operation, payload, methodSpec);
    //
    // if (methodSpec != null) {
    // }
  }

  private void createParameter(EndPoint endPoint, MethodSpec.Builder methodSpec, Payload payload, Operation operation) {

    if ("application/x-www-form-urlencoded".equals(payload.mediaType().value())) {

      if (payload.schema() instanceof NodeShape) {

        NodeShape object = (NodeShape) payload.schema();
        for (PropertyShape shape : object.properties()) {

          AnyShape parameterShape = (AnyShape) shape.range();
          V10GType type =
              build.fetchType(Names.javaTypeName(endPoint,
                                                 operation, parameterShape), parameterShape);
          methodSpec.addParameter(ParameterSpec
              .builder(type.defaultJavaTypeName(build.getModelPackage()), shape.name().value())
              .addAnnotation(AnnotationSpec.builder(FormParam.class).addMember("value", "$S", shape.name()).build())
              .build());
        }
      }
      return;
    }

    if (payload.schema().name().value().equals("any") && "application/octet-stream".equals(payload.mediaType().value())) {

      TypeName typeName = ClassName.get(InputStream.class);
      methodSpec.addParameter(ParameterSpec.builder(typeName, "entity").build());
    } else {

      TypeName typeName =
          TypeBasedOperation.run((AnyShape) payload.schema(), defaultJavaType(build, build.getModelPackage()))
              .orElseThrow(() -> new GenerationException("in " + endPoint.path() + " at operation " + operation.method()
                               + " schema " + payload.schema().name() + " was not seen before"));
      methodSpec.addParameter(ParameterSpec.builder(typeName, "entity").build());
    }
  }


  private Set<String> fetchAllMediaTypesForMethodResponses(Operation operation) {

    Set<String> mediaTypes = new HashSet<>();
    // todo flatMap
    for (Response gResponse : operation.responses()) {

      mediaTypes.addAll(gResponse.payloads().stream().map(input -> input.mediaType().value()).collect(Collectors.toList()));
    }

    return mediaTypes;
  }

  private Map<String, TypeSpec.Builder> createResponseClass(TypeSpec.Builder typeSpec, EndPoint endPoint,
                                                            Multimap<Operation, Payload> bodies,
                                                            Multimap<Operation, Response> responses) {
    if (!build.shouldGenerateResponseClasses()) {
      return Collections.emptyMap();
    }

    Map<String, TypeSpec.Builder> map = new HashMap<>();

    Set<Operation> allMethods = new HashSet<>();
    allMethods.addAll(bodies.keySet());
    allMethods.addAll(responses.keySet());
    for (Operation method : allMethods) {

      if (method.responses().size() == 0) {

        continue;
      }

      String defaultName = Names.responseClassName(endPoint, method);
      TypeSpec.Builder responseClass = TypeSpec
          .classBuilder(defaultName)
          .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
          .superclass(ClassName.get(build.getSupportPackage(), "ResponseDelegate"))
          .addMethod(
                     MethodSpec.constructorBuilder()
                         .addParameter(javax.ws.rs.core.Response.class, "response")
                         .addParameter(Object.class, "entity")
                         .addModifiers(Modifier.PRIVATE)
                         .addCode("super(response, entity);\n").build()
          ).addMethod(
                      MethodSpec.constructorBuilder()
                          .addParameter(javax.ws.rs.core.Response.class, "response")
                          .addModifiers(Modifier.PRIVATE)
                          .addCode("super(response);\n").build()
          );;


      map.put(defaultName, null);

      TypeSpec currentClass = responseClass.build();
      for (Response response : responses.get(method)) {

        if (response == null) {
          continue;
        }

        TypeSpec internalClassForHeaders = null;
        if (!response.headers().isEmpty()) {

          internalClassForHeaders = buildHeadersForResponse(responseClass, response.headers(), response.statusCode().value());
        }

        if (response.payloads().size() == 0) {
          String httpCode = response.statusCode().value();
          MethodSpec.Builder builder = MethodSpec.methodBuilder("respond" + httpCode);
          builder
              .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
              .returns(TypeVariableName.get(currentClass.name));

          if (internalClassForHeaders == null) {

            builder.addStatement("Response.ResponseBuilder responseBuilder = Response.status(" + httpCode + ")")
                .addStatement("return new $N(responseBuilder.build())", currentClass);

          } else {

            builder.addParameter(ParameterSpec.builder(ClassName.get("", internalClassForHeaders.name), "headers").build())
                .addStatement("Response.ResponseBuilder responseBuilder = Response.status(" + httpCode + ")")
                .addStatement("responseBuilder = headers.toResponseBuilder(responseBuilder)")
                .addStatement("return new $N(responseBuilder.build())", currentClass);
          }

          // todo dead plugins. need to do better. haha.
          // builder =
          // build
          // .pluginsForResponseMethod(new Function<Collection<ResponseMethodExtension<GResponse>>,
          // ResponseMethodExtension<GResponse>>() {
          //
          // @Nullable
          // @Override
          // public ResponseMethodExtension<GResponse> apply(@Nullable Collection<ResponseMethodExtension<GResponse>>
          // responseMethodExtensions) {
          // return new ResponseMethodExtension.Composite(responseMethodExtensions);
          // }
          // }, response)
          // .onMethod(new ResourceContextImpl(build),
          // response, builder);
          //
          //
          // if (builder == null) {
          // continue;
          // }

          responseClass.addMethod(builder.build());
        } else {
          for (Payload responseType : response.payloads()) {

            String httpCode = response.statusCode().value();
            MethodSpec.Builder builder =
                MethodSpec.methodBuilder(
                                         Names.methodName("respond", httpCode, "With", responseType.mediaType().value()))
                    .addModifiers(Modifier.STATIC, Modifier.PUBLIC);

            builder
                .returns(TypeVariableName.get(currentClass.name));

            TypeName typeName = createResponseParameter(responseType, builder);

            if (internalClassForHeaders != null) {

              builder.addParameter(ParameterSpec.builder(ClassName.get("", internalClassForHeaders.name), "headers").build());
            }

            builder.addStatement("Response.ResponseBuilder responseBuilder = Response.status(" + httpCode
                + ").header(\"Content-Type\", \""
                + responseType.mediaType() + "\")");

            if (responseType.schema() == null) {

              if (internalClassForHeaders == null) {
                builder
                    .addStatement("responseBuilder.entity(null)")
                    .addStatement("return new $N(responseBuilder.build(), null)", currentClass);
              } else {

                builder
                    .addStatement("responseBuilder.entity(null)")
                    .addStatement("headers.toResponseBuilder(responseBuilder)")
                    .addStatement("return new $N(responseBuilder.build(), null)", currentClass);
              }
            } else {

              if (responseType.schema() instanceof ArrayShape) {

                if (internalClassForHeaders == null) {
                  builder
                      .addStatement("$T<$T> wrappedEntity = new $T<$T>(entity){}", GenericEntity.class, typeName,
                                    GenericEntity.class, typeName)
                      .addStatement("responseBuilder.entity(wrappedEntity)")
                      .addStatement("return new $N(responseBuilder.build(), wrappedEntity)", currentClass);
                } else {

                  builder.addStatement("$T<$T> wrappedEntity = new $T<$T>(entity){}", GenericEntity.class, typeName,
                                       GenericEntity.class, typeName)
                      .addStatement("headers.toResponseBuilder(responseBuilder)")
                      .addStatement("responseBuilder.entity(wrappedEntity)")
                      .addStatement("return new $N(responseBuilder.build(), wrappedEntity)", currentClass);
                }
              } else {

                if (internalClassForHeaders == null) {
                  builder
                      .addStatement("responseBuilder.entity(entity)")
                      .addStatement("return new $N(responseBuilder.build(), entity)", currentClass);
                } else {

                  builder
                      .addStatement("responseBuilder.entity(entity)")
                      .addStatement("headers.toResponseBuilder(responseBuilder)")
                      .addStatement("return new $N(responseBuilder.build(), entity)", currentClass);
                }
              }
            }

            // builder =
            // build
            // .pluginsForResponseMethod(new Function<Collection<ResponseMethodExtension<GResponse>>,
            // ResponseMethodExtension<GResponse>>() {
            //
            // @Nullable
            // @Override
            // public ResponseMethodExtension<GResponse> apply(@Nullable Collection<ResponseMethodExtension<GResponse>>
            // responseMethodExtensions) {
            // return new ResponseMethodExtension.Composite(responseMethodExtensions);
            // }
            // }, response)
            // .onMethod(new ResourceContextImpl(build),
            // response, builder);
            //
            // if (builder == null) {
            // continue;
            // }
            responseClass.addMethod(builder.build());
          }
        }
      }

      // responseClass =
      // build
      // .pluginsForResponseClass(new Function<Collection<ResponseClassExtension<GMethod>>, ResponseClassExtension<GMethod>>() {
      //
      // @Nullable
      // @Override
      // public ResponseClassExtension<GMethod> apply(@Nullable Collection<ResponseClassExtension<GMethod>>
      // responseClassExtensions) {
      // return new ResponseClassExtension.Composite(responseClassExtensions);
      // }
      // }, method)
      // .onResponseClass(new ResourceContextImpl(build),
      // method, responseClass);
      //
      // if (responseClass == null) {
      //
      // map.put(defaultName, null);
      // continue;
      // }

      map.put(defaultName, responseClass);
      typeSpec.addType(responseClass.build());
    }

    return map;
  }

  private TypeName createResponseParameter(Payload responseType, MethodSpec.Builder builder) {

    // todo this check for any is wrong, pretty sure.
    if ("application/octet-stream".equals(responseType.mediaType().value())
        && AnyShape.class.equals(responseType.schema().getClass())) {

      TypeName typeName = ClassName.get(StreamingOutput.class);
      builder.addParameter(ParameterSpec.builder(typeName, "entity").build());

      return typeName;
    } else {
      if (responseType.schema() != null) {
        TypeName typeName =
            TypeBasedOperation.run((AnyShape) responseType.schema(), defaultJavaType(build, build.getModelPackage()))
                .orElseThrow(() -> new GenerationException(responseType.mediaType() + "," + responseType.schema().name()
                                 + " was not seen before"));

        builder.addParameter(ParameterSpec.builder(typeName, "entity").build());
        return typeName;
      } else {

        return null;
      }
    }
  }

  private TypeSpec buildHeadersForResponse(TypeSpec.Builder responseClass, List<Parameter> headers, String code) {

    responseClass.addMethod(MethodSpec.methodBuilder(Names.methodName("headersFor" + code))
        .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
        .returns(ClassName.get("", "HeadersFor" + code))
        .addStatement("return new HeadersFor" + code + "()")
        .build()
        );

    TypeSpec.Builder headerForCode = TypeSpec.classBuilder("HeadersFor" + code).addModifiers(Modifier.STATIC, Modifier.PUBLIC)
        .superclass(ClassName.get("", "HeaderBuilderBase"))
        .addMethod(MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE).build());

    for (Parameter header : headers) {
      TypeName typeName = TypeBasedOperation.run((AnyShape) header.schema(), defaultJavaType(build, build.getModelPackage()))
          .orElseThrow(() -> new GenerationException("schema " + header.schema().name() + " was not seen before"));
      MethodSpec spec = MethodSpec
          .methodBuilder(Names.methodName("with", header.name().value()))
          .addModifiers(Modifier.PUBLIC)
          .returns(ClassName.get("", "HeadersFor" + code))
          .addParameter(ParameterSpec.builder(typeName, "p", Modifier.FINAL)
              .build())
          .addStatement("headerMap.put($S, String.valueOf(p));", header.name())
          .addStatement("return this")
          .build();
      headerForCode.addMethod(spec);
    }

    TypeSpec build = headerForCode.build();
    responseClass.addType(build);
    return build;
  }

  private MethodSpec.Builder createMethodBuilder(EndPoint endPoint, Operation operation, String methodName,
                                                 Set<String> mediaTypesForMethod,
                                                 Map<String, TypeSpec.Builder> responseSpec) {

    MethodSpec.Builder methodSpec = MethodSpec.methodBuilder(methodName)
        .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC);


    for (Parameter parameter : ResourceUtils.accumulateUriParameters(endPoint)) {

      TypeName typeName = TypeBasedOperation.run((AnyShape) parameter.schema(), defaultJavaType(build, build.getModelPackage()))
          .orElseThrow(() -> new GenerationException("schema " + parameter.schema().name() + " was not seen before"));
      methodSpec.addParameter(
          ParameterSpec
              .builder(
                       typeName,
                       Names.methodName(parameter.name().value()))
              .addAnnotation(
                             AnnotationSpec.builder(PathParam.class).addMember("value", "$S", parameter.name())
                                 .build())
              .build());
    }

    for (Parameter parameter : operation.request().queryParameters()) {

      TypeName typeName = TypeBasedOperation.run((AnyShape) parameter.schema(), defaultJavaType(build, build.getModelPackage()))
          .orElseThrow(() -> new GenerationException("schema " + parameter.schema().name() + " was not seen before"));
      ParameterSpec.Builder parameterSpec = ParameterSpec
          .builder(
                   typeName,
                   Names.methodName(parameter.name().value()))
          .addAnnotation(
                         AnnotationSpec.builder(QueryParam.class).addMember("value", "$S", parameter.name())
                             .build());
      if (parameter.schema().defaultValue() != null) {
        parameterSpec.addAnnotation(
            // todo this is wrong
            AnnotationSpec.builder(DefaultValue.class)
                .addMember("value", "$S", parameter.schema().defaultValue().name()).build());
      }
      methodSpec.addParameter(parameterSpec.build());
    }

    for (Parameter parameter : operation.request().headers()) {

      TypeName typeName = TypeBasedOperation.run((AnyShape) parameter.schema(), defaultJavaType(build, build.getModelPackage()))
          .orElseThrow(() -> new GenerationException("schema " + parameter.schema().name() + " was not seen before"));
      methodSpec.addParameter(
          ParameterSpec
              .builder(
                       typeName,
                       Names.methodName(parameter.name().value()))
              .addAnnotation(
                             AnnotationSpec.builder(HeaderParam.class).addMember("value", "$S", parameter.name())
                                 .build())
              .build());
    }

    buildNewWebMethod(operation, methodSpec);

    methodSpec.addAnnotation(
        AnnotationSpec
            .builder(Path.class)
            .addMember("value",
                       "$S",
                       generatePathString(
                                          endPoint
                                              .path().value(),
                                          ResourceUtils.accumulateUriParameters(endPoint)))
            .build());

    if (operation.responses().size() == 0) {
      // There is no response, return void
      methodSpec.returns(ClassName.VOID);
    } else if (build.shouldGenerateResponseClasses()) {
      // Return types are the responses with an entity
      TypeSpec.Builder responseSpecForMethod = responseSpec.get(Names.responseClassName(endPoint, operation));
      if (responseSpecForMethod == null) {

        methodSpec.returns(ClassName.get(Response.class));
      } else {
        methodSpec.returns(ClassName.get("", responseSpecForMethod.build().name));
      }
    } else {
      // Return types are the entities itself
      List<Payload> bodies = operation.responses().get(0).payloads();
      if (bodies == null || bodies.isEmpty()) {
        methodSpec.returns(ClassName.VOID);
      } else {
        TypeName typeName =
            TypeBasedOperation.run((AnyShape) bodies.get(0).schema(), defaultJavaType(build, build.getModelPackage()))
                .orElseThrow(() -> new GenerationException("schema " + bodies.get(0).schema() + " was not seen before"));
        methodSpec.returns(typeName);
      }
    }

    if (mediaTypesForMethod.size() > 0) {
      AnnotationSpec.Builder ann = buildAnnotation(mediaTypesForMethod, Produces.class);
      methodSpec.addAnnotation(ann.build());
    }
    return methodSpec;
  }

  private String generatePathString(final String pathString, final List<Parameter> uriParams) {
    String generatedPathString = pathString;
    // update regex of path string pattern variables (if needed)
    for (Parameter parameter : uriParams) {
      final String name = Names.methodName(parameter.name().value());
      // check if path contains param
      if (pathString.contains("{" + name + "}")) {
        // check if param is a string value
        if (parameter.schema() instanceof ScalarShape) {
          ScalarShape typeDecl = (ScalarShape) parameter.schema();
          // check if this string has a pattern to register
          if (typeDecl.pattern() != null) {
            // remove ^...$ from pattern if existing
            String pattern = typeDecl.pattern().value();
            if (pattern.startsWith("^") && pattern.endsWith("$") && pattern.length() > 2) {
              pattern = pattern.substring(1, pattern.length() - 1);
            }
            generatedPathString = pathString.replace("{" + name + "}", "{" + name + ":" + pattern + "}");
          }
        }
      }
    }
    return generatedPathString;
  }

  private void buildNewWebMethod(Operation operation, MethodSpec.Builder methodSpec) {
    Class<? extends Annotation> type = HTTPMethods.methodNameToAnnotation(operation.name().value());
    if (type == null) {

      String name = operation.method().value().toUpperCase();
      final ClassName className = ClassName.get(build.getSupportPackage(), name);
      final TypeSpec.Builder builder = TypeSpec.annotationBuilder(className);
      builder
          .addModifiers(Modifier.PUBLIC)
          .addAnnotation(AnnotationSpec.builder(Target.class)
              .addMember("value", "{$T.$L}", ElementType.class, "METHOD").build())
          .addAnnotation(AnnotationSpec.builder(Retention.class)
              .addMember("value", "$T.$L", RetentionPolicy.class, "RUNTIME")
              .build())
          .addAnnotation(AnnotationSpec.builder(HttpMethod.class).addMember("value", "$S", name).build());
      build.newSupportGenerator(new JavaPoetTypeGeneratorBase(className) {

        @Override
        public void output(CodeContainer<TypeSpec.Builder> rootDirectory) throws IOException {

          rootDirectory.into(builder);
        }
      });

      methodSpec
          .addAnnotation(AnnotationSpec.builder(className).build());

    } else {

      methodSpec
          .addAnnotation(AnnotationSpec.builder(type).build());
    }
  }

  private void handleMethodConsumer(MethodSpec.Builder methodSpec,
                                    Multimap<String, String> ramlTypeToMediaType,
                                    AnyShape anyShape) {

    Collection<String> mediaTypes = ramlTypeToMediaType.get(anyShape == null ? null : anyShape.name().value());

    if (mediaTypes.size() > 0) {
      AnnotationSpec.Builder ann = buildAnnotation(mediaTypes, Consumes.class);
      methodSpec.addAnnotation(ann.build());
    }
  }

  private AnnotationSpec.Builder buildAnnotation(Collection<String> mediaTypes, Class<? extends Annotation> type) {
    AnnotationSpec.Builder ann = AnnotationSpec.builder(type);
    for (String mediaType : mediaTypes) {

      ann.addMember("value", "$S", mediaType);
    }
    return ann;
  }

  private class DefaultResourceClassCreator implements ResourceClassExtension {

    @Override
    public TypeSpec.Builder onResource(ResourceContext context, EndPoint resource, TypeSpec.Builder nullSpec) {

      TypeSpec.Builder typeSpec = TypeSpec.interfaceBuilder(Names.typeName(name))
          .addModifiers(Modifier.PUBLIC)
          .addAnnotation(AnnotationSpec.builder(Path.class)
              .addMember("value", "$S", generatePathString(uri, resource.parameters())).build());

      buildResource(typeSpec, topResource);
      // typeSpec =
      // build
      // .pluginsForResourceClass(new Function<Collection<ResourceClassExtension<GResource>>, ResourceClassExtension<GResource>>()
      // {
      //
      // @Nullable
      // @Override
      // public ResourceClassExtension<GResource> apply(@Nullable Collection<ResourceClassExtension<GResource>>
      // resourceClassExtensions) {
      // return new ResourceClassExtension.Composite(resourceClassExtensions);
      //
      // }
      // }, topResource)
      // .onResource(new ResourceContextImpl(build), topResource, typeSpec);
      return typeSpec;
    }
  }
}
