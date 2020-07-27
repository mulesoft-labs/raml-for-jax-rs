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

import com.google.common.base.Function;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.squareup.javapoet.*;

import org.raml.jaxrs.generator.*;
import org.raml.jaxrs.generator.builders.CodeContainer;
import org.raml.jaxrs.generator.builders.JavaPoetTypeGeneratorBase;
import org.raml.jaxrs.generator.builders.extensions.resources.ResourceContextImpl;
import org.raml.jaxrs.generator.extension.resources.api.*;
import org.raml.jaxrs.generator.ramltypes.*;
import org.raml.jaxrs.generator.v10.V10GType;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.StringTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

import javax.annotation.Nullable;
import javax.lang.model.element.Modifier;
import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.*;
import java.util.*;

/**
 * Created by Jean-Philippe Belanger on 10/27/16. Abstraction of creation.
 */
public class ResourceBuilder implements ResourceGenerator {

  private final CurrentBuild build;
  private final GResource topResource;
  private final String name;
  private final String uri;

  public ResourceBuilder(CurrentBuild build, GResource resource, String name, String uri) {

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


  private void recurse(TypeSpec.Builder typeSpec, GResource parentResource) {

    for (GResource resource : parentResource.resources()) {

      buildResource(typeSpec, resource);
      recurse(typeSpec, resource);
    }
  }

  private void buildResource(TypeSpec.Builder typeSpec, GResource currentResource) {

    Multimap<GMethod, GRequest> incomingBodies = ArrayListMultimap.create();
    Multimap<GMethod, GResponse> responses = ArrayListMultimap.create();
    ResourceUtils.fillInBodiesAndResponses(currentResource, incomingBodies, responses);

    Map<String, TypeSpec.Builder> responseSpecs = createResponseClass(typeSpec, incomingBodies, responses);

    for (GMethod gMethod : currentResource.methods()) {

      String methodName = Names.resourceMethodName(gMethod.resource(), gMethod);
      Set<String> mediaTypesForMethod = fetchAllMediaTypesForMethodResponses(gMethod);
      if (gMethod.body().size() == 0) {

        createMethodWithoutBody(typeSpec, gMethod, mediaTypesForMethod, HashMultimap.<String, String>create(), methodName,
                                responseSpecs);
      } else {
        Multimap<String, String> ramlTypeToMediaType = accumulateMediaTypesPerType(incomingBodies, gMethod);
        for (GRequest gRequest : gMethod.body()) {

          if (gRequest.type() == null) {

            createMethodWithoutBody(typeSpec, gMethod, mediaTypesForMethod, ramlTypeToMediaType, methodName, responseSpecs);
            continue;
          }

          if (ramlTypeToMediaType.containsKey(gRequest.type().name())) {
            createMethodWithBody(typeSpec, gMethod, ramlTypeToMediaType, methodName, gRequest, responseSpecs, mediaTypesForMethod);
            ramlTypeToMediaType.removeAll(gRequest.type().name());
          }
        }
      }
    }
  }

  private Multimap<String, String> accumulateMediaTypesPerType(Multimap<GMethod, GRequest> incomingBodies,
                                                               GMethod gMethod) {
    Multimap<String, String> ramlTypeToMediaType = ArrayListMultimap.create();
    for (GRequest request : incomingBodies.get(gMethod)) {
      if (request != null) {
        if (request.type() == null) {
          ramlTypeToMediaType.put(null, request.mediaType());
        } else {
          ramlTypeToMediaType.put(request.type().name(), request.mediaType());
        }
      }
    }
    return ramlTypeToMediaType;
  }

  private void createMethodWithoutBody(TypeSpec.Builder typeSpec, GMethod gMethod, Set<String> mediaTypesForMethod,
                                       Multimap<String, String> ramlTypeToMediaType, String methodName,
                                       Map<String, TypeSpec.Builder> responseSpecs) {

    MethodSpec.Builder methodSpec = createMethodBuilder(gMethod, methodName, mediaTypesForMethod, responseSpecs);


    methodSpec =
        build
            .pluginsForResourceMethod(new Function<Collection<ResourceMethodExtension<GMethod>>, ResourceMethodExtension<GMethod>>() {

                                        @Nullable
                                        @Override
                                        public ResourceMethodExtension<GMethod> apply(@Nullable Collection<ResourceMethodExtension<GMethod>> resourceMethodExtensions) {
                                          return new ResourceMethodExtension.Composite(resourceMethodExtensions);
                                        }
                                      }, gMethod)
            .onMethod(new ResourceContextImpl(build),
                      gMethod, null, methodSpec);
    handleMethodConsumer(methodSpec, ramlTypeToMediaType, null);

    if (methodSpec != null) {
      typeSpec.addMethod(methodSpec.build());
    }
  }

  private void createMethodWithBody(TypeSpec.Builder typeSpec, GMethod gMethod,
                                    Multimap<String, String> ramlTypeToMediaType, String methodName, GRequest gRequest,
                                    Map<String, TypeSpec.Builder> responseSpec, Set<String> mediaTypesForMethod) {

    MethodSpec.Builder methodSpec = createMethodBuilder(gMethod, methodName, mediaTypesForMethod, responseSpec);

    createParamteter(methodSpec, gRequest, gMethod);

    handleMethodConsumer(methodSpec, ramlTypeToMediaType, gRequest.type());

    methodSpec =
        build
            .pluginsForResourceMethod(new Function<Collection<ResourceMethodExtension<GMethod>>, ResourceMethodExtension<GMethod>>() {

                                        @Nullable
                                        @Override
                                        public ResourceMethodExtension<GMethod> apply(@Nullable Collection<ResourceMethodExtension<GMethod>> resourceMethodExtensions) {
                                          return new ResourceMethodExtension.Composite(resourceMethodExtensions);
                                        }
                                      }, gMethod)
            .onMethod(new ResourceContextImpl(build),
                      gMethod, gRequest, methodSpec);

    if (methodSpec != null) {
      typeSpec.addMethod(methodSpec.build());
    }
  }

  private void createParamteter(MethodSpec.Builder methodSpec, GRequest gRequest, GMethod gMethod) {

    if ("application/x-www-form-urlencoded".equals(gRequest.mediaType())) {

      if (gRequest.type().implementation() instanceof ObjectTypeDeclaration) {

        ObjectTypeDeclaration object = (ObjectTypeDeclaration) gRequest.type().implementation();
        for (TypeDeclaration typeDeclaration : object.properties()) {

          V10GType type =
              build.fetchType(Names.javaTypeName((Resource) gMethod.resource().implementation(),
                                                 (Method) gMethod.implementation(), typeDeclaration), typeDeclaration);
          methodSpec.addParameter(ParameterSpec
              .builder(type.defaultJavaTypeName(build.getModelPackage()), typeDeclaration.name())
              .addAnnotation(AnnotationSpec.builder(FormParam.class).addMember("value", "$S", typeDeclaration.name()).build())
              .build());
        }
      }
      return;
    }

    if (gRequest.type().name().equals("any")) {
      TypeName typeName = ClassName.get(InputStream.class);
      methodSpec.addParameter(ParameterSpec.builder(typeName, "entity").build());
    } else {

      TypeName typeName = gRequest.type().defaultJavaTypeName(build.getModelPackage());
      methodSpec.addParameter(ParameterSpec.builder(typeName, "entity").build());
    }
  }


  private Set<String> fetchAllMediaTypesForMethodResponses(GMethod gMethod) {

    Set<String> mediaTypes = new HashSet<>();
    for (GResponse gResponse : gMethod.responses()) {

      mediaTypes.addAll(Lists.transform(gResponse.body(), new Function<GResponseType, String>() {

        @Nullable
        @Override
        public String apply(@Nullable GResponseType input) {
          return input.mediaType();
        }
      }));
    }

    return mediaTypes;
  }

  private Map<String, TypeSpec.Builder> createResponseClass(TypeSpec.Builder typeSpec, Multimap<GMethod, GRequest> bodies,
                                                            Multimap<GMethod, GResponse> responses) {
    if (!build.shouldGenerateResponseClasses()) {
      return Collections.emptyMap();
    }

    Map<String, TypeSpec.Builder> map = new HashMap<>();

    Set<GMethod> allMethods = new HashSet<>();
    allMethods.addAll(bodies.keySet());
    allMethods.addAll(responses.keySet());
    for (GMethod gMethod : allMethods) {

      if (gMethod.responses().size() == 0) {

        continue;
      }

      String defaultName = Names.responseClassName(gMethod.resource(), gMethod);
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
      for (GResponse gResponse : responses.get(gMethod)) {

        if (gResponse == null) {
          continue;
        }

        TypeSpec internalClassForHeaders = null;
        if (!gResponse.headers().isEmpty()) {

          internalClassForHeaders = buildHeadersForResponse(responseClass, gResponse.headers(), gResponse.code());
        }

        if (gResponse.body().size() == 0) {
          String httpCode = gResponse.code();
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


          builder =
              build
                  .pluginsForResponseMethod(new Function<Collection<ResponseMethodExtension<GResponse>>, ResponseMethodExtension<GResponse>>() {

                                              @Nullable
                                              @Override
                                              public ResponseMethodExtension<GResponse> apply(@Nullable Collection<ResponseMethodExtension<GResponse>> responseMethodExtensions) {
                                                return new ResponseMethodExtension.Composite(responseMethodExtensions);
                                              }
                                            }, gResponse)
                  .onMethod(new ResourceContextImpl(build),
                            gResponse, builder);


          if (builder == null) {
            continue;
          }

          responseClass.addMethod(builder.build());
        } else {
          for (GResponseType responseType : gResponse.body()) {

            String httpCode = gResponse.code();
            MethodSpec.Builder builder =
                MethodSpec.methodBuilder(
                                         Names.methodName("respond", httpCode, "With", responseType.mediaType()))
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

            if (responseType.type() == null) {

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

              if (responseType.type().isArray()) {

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

            builder =
                build
                    .pluginsForResponseMethod(new Function<Collection<ResponseMethodExtension<GResponse>>, ResponseMethodExtension<GResponse>>() {

                                                @Nullable
                                                @Override
                                                public ResponseMethodExtension<GResponse> apply(@Nullable Collection<ResponseMethodExtension<GResponse>> responseMethodExtensions) {
                                                  return new ResponseMethodExtension.Composite(responseMethodExtensions);
                                                }
                                              }, gResponse)
                    .onMethod(new ResourceContextImpl(build),
                              gResponse, builder);

            if (builder == null) {
              continue;
            }
            responseClass.addMethod(builder.build());
          }
        }
      }

      responseClass =
          build
              .pluginsForResponseClass(new Function<Collection<ResponseClassExtension<GMethod>>, ResponseClassExtension<GMethod>>() {

                                         @Nullable
                                         @Override
                                         public ResponseClassExtension<GMethod> apply(@Nullable Collection<ResponseClassExtension<GMethod>> responseClassExtensions) {
                                           return new ResponseClassExtension.Composite(responseClassExtensions);
                                         }
                                       }, gMethod)
              .onResponseClass(new ResourceContextImpl(build),
                               gMethod, responseClass);

      if (responseClass == null) {

        map.put(defaultName, null);
        continue;
      }

      map.put(defaultName, responseClass);
      typeSpec.addType(responseClass.build());
    }

    return map;
  }

  private TypeName createResponseParameter(GResponseType responseType, MethodSpec.Builder builder) {

    if ("any".equals(responseType.type().name())) {

      TypeName typeName = ClassName.get(StreamingOutput.class);
      builder.addParameter(ParameterSpec.builder(typeName, "entity").build());

      return typeName;
    } else {
      if (responseType.type() != null) {
        TypeName typeName = responseType.type().defaultJavaTypeName(build.getModelPackage());
        if (typeName == null) {
          throw new GenerationException(responseType.mediaType() + "," + responseType.type().name() + " was not seen before");
        }

        builder.addParameter(ParameterSpec.builder(typeName, "entity").build());
        return typeName;
      } else {

        return null;
      }
    }
  }

  private TypeSpec buildHeadersForResponse(TypeSpec.Builder responseClass, List<GParameter> headers, String code) {

    responseClass.addMethod(MethodSpec.methodBuilder(Names.methodName("headersFor" + code))
        .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
        .returns(ClassName.get("", "HeadersFor" + code))
        .addStatement("return new HeadersFor" + code + "()")
        .build()
        );

    TypeSpec.Builder headerForCode = TypeSpec.classBuilder("HeadersFor" + code).addModifiers(Modifier.STATIC, Modifier.PUBLIC)
        .superclass(ClassName.get("", "HeaderBuilderBase"))
        .addMethod(MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE).build());

    for (GParameter header : headers) {
      MethodSpec spec = MethodSpec
          .methodBuilder(Names.methodName("with", header.name()))
          .addModifiers(Modifier.PUBLIC)
          .returns(ClassName.get("", "HeadersFor" + code))
          .addParameter(ParameterSpec.builder(header.type().defaultJavaTypeName(build.getModelPackage()), "p", Modifier.FINAL)
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

  private MethodSpec.Builder createMethodBuilder(GMethod gMethod, String methodName, Set<String> mediaTypesForMethod,
                                                 Map<String, TypeSpec.Builder> responseSpec) {

    MethodSpec.Builder methodSpec = MethodSpec.methodBuilder(methodName)
        .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC);


    for (GParameter parameter : ResourceUtils.accumulateUriParameters(gMethod.resource())) {

      methodSpec.addParameter(
          ParameterSpec
              .builder(
                       parameter.type().defaultJavaTypeName(build.getModelPackage()),
                       Names.methodName(parameter.name()))
              .addAnnotation(
                             AnnotationSpec.builder(PathParam.class).addMember("value", "$S", parameter.name())
                                 .build())
              .build());
    }

    for (GParameter gParameter : gMethod.queryParameters()) {

      ParameterSpec.Builder parameterSpec = ParameterSpec
          .builder(
                   gParameter.type().defaultJavaTypeName(this.build.getModelPackage()),
                   Names.methodName(gParameter.name()))
          .addAnnotation(
                         AnnotationSpec.builder(QueryParam.class).addMember("value", "$S", gParameter.name())
                             .build());
      if (gParameter.defaultValue() != null) {
        parameterSpec.addAnnotation(
            AnnotationSpec.builder(DefaultValue.class)
                .addMember("value", "$S", gParameter.defaultValue()).build());
      }
      methodSpec.addParameter(parameterSpec.build());
    }

    for (GParameter gParameter : gMethod.headers()) {

      methodSpec.addParameter(
          ParameterSpec
              .builder(
                       gParameter.type().defaultJavaTypeName(build.getModelPackage()),
                       Names.methodName(gParameter.name()))
              .addAnnotation(
                             AnnotationSpec.builder(HeaderParam.class).addMember("value", "$S", gParameter.name())
                                 .build())
              .build());
    }

    buildNewWebMethod(gMethod, methodSpec);


    if (gMethod.resource().parentResource() != null) {

      methodSpec.addAnnotation(
          AnnotationSpec
              .builder(Path.class)
              .addMember("value",
                         "$S",
                         generatePathString(
                                            gMethod
                                                .resource()
                                                .resourcePath()
                                                .substring(findTopResource(gMethod.resource().parentResource()).resourcePath()
                                                    .length()),
                                            ResourceUtils.accumulateUriParameters(gMethod.resource())))
              .build());
    }

    if (gMethod.responses().size() == 0) {
      // There is no response, return void
      methodSpec.returns(ClassName.VOID);
    } else if (build.shouldGenerateResponseClasses()) {
      // Return types are the responses with an entity
      TypeSpec.Builder responseSpecForMethod = responseSpec.get(Names.responseClassName(gMethod.resource(), gMethod));
      if (responseSpecForMethod == null) {

        methodSpec.returns(ClassName.get(Response.class));
      } else {
        methodSpec.returns(ClassName.get("", responseSpecForMethod.build().name));
      }
    } else {
      // Return types are the entities itself
      List<GResponseType> bodies = gMethod.responses().get(0).body();
      if (bodies == null || bodies.isEmpty()) {
        methodSpec.returns(ClassName.VOID);
      } else {
        methodSpec.returns(bodies.get(0).type().defaultJavaTypeName(build.getModelPackage()));
      }
    }

    if (mediaTypesForMethod.size() > 0) {
      AnnotationSpec.Builder ann = buildAnnotation(mediaTypesForMethod, Produces.class);
      methodSpec.addAnnotation(ann.build());
    }
    return methodSpec;
  }

  private String generatePathString(final String pathString, final List<GParameter> uriParams) {
    String generatedPathString = pathString;
    // update regex of path string pattern variables (if needed)
    for (GParameter parameter : uriParams) {
      final String name = Names.methodName(parameter.name());
      // check if path contains param
      if (pathString.contains("{" + name + "}")) {
        // check if param is a string value
        if (parameter.implementation() instanceof StringTypeDeclaration) {
          StringTypeDeclaration typeDecl = (StringTypeDeclaration) parameter.implementation();
          // check if this string has a pattern to register
          if (typeDecl.pattern() != null) {
            // remove ^...$ from pattern if existing
            String pattern = typeDecl.pattern();
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

  private GResource findTopResource(GResource gResource) {

    if (gResource.parentResource() == null) {
      return gResource;
    } else {

      return findTopResource(gResource.parentResource());
    }
  }

  private void buildNewWebMethod(GMethod gMethod, MethodSpec.Builder methodSpec) {
    Class<? extends Annotation> type = HTTPMethods.methodNameToAnnotation(gMethod.method());
    if (type == null) {

      String name = gMethod.method().toUpperCase();
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
                                    GType typeDeclaration) {

    Collection<String> mediaTypes = ramlTypeToMediaType.get(typeDeclaration == null ? null : typeDeclaration.name());

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

  private class DefaultResourceClassCreator implements ResourceClassExtension<GResource> {

    @Override
    public TypeSpec.Builder onResource(ResourceContext context, GResource resource, TypeSpec.Builder nullSpec) {

      TypeSpec.Builder typeSpec = TypeSpec.interfaceBuilder(Names.typeName(name))
          .addModifiers(Modifier.PUBLIC)
          .addAnnotation(AnnotationSpec.builder(Path.class)
              .addMember("value", "$S", generatePathString(uri, resource.uriParameters())).build());

      buildResource(typeSpec, topResource);

      recurse(typeSpec, topResource);

      typeSpec =
          build
              .pluginsForResourceClass(new Function<Collection<ResourceClassExtension<GResource>>, ResourceClassExtension<GResource>>() {

                                         @Nullable
                                         @Override
                                         public ResourceClassExtension<GResource> apply(@Nullable Collection<ResourceClassExtension<GResource>> resourceClassExtensions) {
                                           return new ResourceClassExtension.Composite(resourceClassExtensions);

                                         }
                                       }, topResource)
              .onResource(new ResourceContextImpl(build), topResource, typeSpec);
      return typeSpec;
    }
  }
}
