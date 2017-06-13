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
package org.raml.jaxrs.generator.builders.resources;

import com.google.common.base.Function;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.GenerationException;
import org.raml.jaxrs.generator.HTTPMethods;
import org.raml.jaxrs.generator.Names;
import org.raml.jaxrs.generator.ResourceUtils;
import org.raml.jaxrs.generator.builders.CodeContainer;
import org.raml.jaxrs.generator.builders.JavaPoetTypeGeneratorBase;
import org.raml.jaxrs.generator.builders.extensions.resources.ResourceContextImpl;
import org.raml.jaxrs.generator.extension.resources.ResourceClassExtension;
import org.raml.jaxrs.generator.extension.resources.ResourceContext;
import org.raml.jaxrs.generator.ramltypes.GMethod;
import org.raml.jaxrs.generator.ramltypes.GParameter;
import org.raml.jaxrs.generator.ramltypes.GRequest;
import org.raml.jaxrs.generator.ramltypes.GResource;
import org.raml.jaxrs.generator.ramltypes.GResponse;
import org.raml.jaxrs.generator.ramltypes.GResponseType;
import org.raml.jaxrs.generator.ramltypes.GType;
import org.raml.jaxrs.generator.v10.Annotations;
import org.raml.jaxrs.generator.v10.TypeUtils;

import javax.annotation.Nullable;
import javax.lang.model.element.Modifier;
import javax.ws.rs.Consumes;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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


    TypeSpec.Builder typeSpec =
        build.getResourceClassExtension(new DefaultResourceClassCreator(), Annotations.ON_RESOURCE_CLASS_CREATION,
                                        topResource)
            .onResource(new ResourceContextImpl(build), topResource, null);

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

        createMethodWithoutBody(typeSpec, gMethod, mediaTypesForMethod, methodName, responseSpecs);
      } else {
        Multimap<String, String> ramlTypeToMediaType = accumulateMediaTypesPerType(incomingBodies, gMethod);
        for (GRequest gRequest : gMethod.body()) {

          if (ramlTypeToMediaType.containsKey(gRequest.type().name())) {
            createMethodWithBody(typeSpec, gMethod, ramlTypeToMediaType, methodName, gRequest, responseSpecs);
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
        ramlTypeToMediaType.put(request.type().name(), request.mediaType());
      }
    }
    return ramlTypeToMediaType;
  }

  private void createMethodWithoutBody(TypeSpec.Builder typeSpec, GMethod gMethod, Set<String> mediaTypesForMethod,
                                       String methodName, Map<String, TypeSpec.Builder> responseSpecs) {

    MethodSpec.Builder methodSpec = createMethodBuilder(gMethod, methodName, mediaTypesForMethod, responseSpecs);

    // here I would run my plugins....
    methodSpec = build.getResourceMethodExtension(Annotations.ON_METHOD_FINISH, gMethod)
        .onMethod(new ResourceContextImpl(build),
                  gMethod, methodSpec);

    if (methodSpec != null) {
      typeSpec.addMethod(methodSpec.build());
    }
  }

  private void createMethodWithBody(TypeSpec.Builder typeSpec, GMethod gMethod,
                                    Multimap<String, String> ramlTypeToMediaType, String methodName, GRequest gRequest,
                                    Map<String, TypeSpec.Builder> responseSpec) {

    MethodSpec.Builder methodSpec = createMethodBuilder(gMethod, methodName, new HashSet<String>(), responseSpec);
    TypeName name = gRequest.type().defaultJavaTypeName(build.getModelPackage());
    methodSpec.addParameter(ParameterSpec.builder(name, "entity").build());
    handleMethodConsumer(methodSpec, ramlTypeToMediaType, gRequest.type());

    methodSpec = build.getResourceMethodExtension(Annotations.ON_METHOD_FINISH, gMethod)
        .onMethod(new ResourceContextImpl(build),
                  gMethod, methodSpec);

    if (methodSpec != null) {
      typeSpec.addMethod(methodSpec.build());
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

      responseClass =
          build.getResponseClassExtension(Annotations.ON_RESPONSE_CLASS_CREATION, gMethod)
              .onMethod(new ResourceContextImpl(build),
                        gMethod, responseClass);

      if (responseClass == null) {

        map.put(defaultName, null);
        continue;
      }

      TypeSpec currentClass = responseClass.build();
      for (GResponse gResponse : responses.get(gMethod)) {

        if (gResponse == null) {
          continue;
        }
        if (gResponse.body().size() == 0) {
          String httpCode = gResponse.code();
          MethodSpec.Builder builder = MethodSpec.methodBuilder("respond" + httpCode);
          builder
              .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
              .addStatement("Response.ResponseBuilder responseBuilder = Response.status(" + httpCode + ")")
              .addStatement("return new $N(responseBuilder.build())", currentClass)
              .returns(TypeVariableName.get(currentClass.name))
              .build();
          builder =
              build.getResponseMethodExtension(Annotations.ON_RESPONSE_METHOD_CREATION, gResponse)
                  .onMethod(new ResourceContextImpl(build),
                            gResponse, builder);

          if (builder == null) {
            continue;
          }

          builder =
              build.getResponseMethodExtension(Annotations.ON_RESPONSE_METHOD_FINISH, gResponse)
                  .onMethod(new ResourceContextImpl(build),
                            gResponse, builder);


          if (builder == null) {
            continue;
          }

          responseClass.addMethod(builder.build());
        } else {
          for (GResponseType typeDeclaration : gResponse.body()) {

            String httpCode = gResponse.code();
            MethodSpec.Builder builder =
                MethodSpec.methodBuilder(
                                         Names.methodName("respond", httpCode, "With", typeDeclaration.mediaType()))
                    .addModifiers(Modifier.STATIC, Modifier.PUBLIC);

            builder =
                build.getResponseMethodExtension(Annotations.ON_RESPONSE_METHOD_CREATION, gResponse)
                    .onMethod(new ResourceContextImpl(build),
                              gResponse, builder);

            if (builder == null) {
              continue;
            }

            builder
                .returns(TypeVariableName.get(currentClass.name));

            TypeName typeName = typeDeclaration.type().defaultJavaTypeName(build.getModelPackage());
            if (typeName == null) {
              throw new GenerationException(typeDeclaration + " was not seen before");
            }

            builder.addParameter(ParameterSpec.builder(typeName, "entity").build());

            builder.addStatement("Response.ResponseBuilder responseBuilder = Response.status(" + httpCode
                + ").header(\"Content-Type\", \""
                + typeDeclaration.mediaType() + "\")");
            if (typeDeclaration.type().isArray() && typeDeclaration.mediaType().contains("xml")) {

              builder
                  .addStatement("$T<$T> wrappedEntity = new $T<$T>(entity){}", GenericEntity.class, typeName,
                                GenericEntity.class, typeName)
                  .addStatement("responseBuilder.entity(wrappedEntity)")
                  .addStatement("return new $N(responseBuilder.build(), wrappedEntity)", currentClass);
            } else {

              builder
                  .addStatement("responseBuilder.entity(entity)")
                  .addStatement("return new $N(responseBuilder.build(), entity)", currentClass);
            }


            builder =
                build.getResponseMethodExtension(Annotations.ON_RESPONSE_METHOD_FINISH, gResponse)
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
          build.getResponseClassExtension(Annotations.ON_RESPONSE_CLASS_FINISH, gMethod)
              .onMethod(new ResourceContextImpl(build),
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

  private MethodSpec.Builder createMethodBuilder(GMethod gMethod, String methodName, Set<String> mediaTypesForMethod,
                                                 Map<String, TypeSpec.Builder> responseSpec) {

    MethodSpec.Builder methodSpec = MethodSpec.methodBuilder(methodName)
        .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC);

    methodSpec =
        build.getResourceMethodExtension(Annotations.ON_METHOD_CREATION, gMethod)
            .onMethod(new ResourceContextImpl(build),
                      gMethod, methodSpec);

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
      /*
       * if (TypeUtils.isComposite(typeDeclaration)) { throw new GenerationException("query parameter is composite: " +
       * typeDeclaration); }
       */

      methodSpec.addParameter(
          ParameterSpec
              .builder(
                       gParameter.type().defaultJavaTypeName(build.getModelPackage()),
                       Names.methodName(gParameter.name()))
              .addAnnotation(
                             AnnotationSpec.builder(QueryParam.class).addMember("value", "$S", gParameter.name())
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
                         gMethod.resource().resourcePath()
                             .substring(findTopResource(gMethod.resource().parentResource()).resourcePath().length()))
              .build());
    }

    if (gMethod.responses().size() != 0) {
      TypeSpec.Builder responseSpecForMethod = responseSpec.get(Names.responseClassName(gMethod.resource(), gMethod));
      if (responseSpecForMethod == null) {

        methodSpec.returns(ClassName.get(Response.class));
      } else {
        methodSpec.returns(ClassName.get("", responseSpecForMethod.build().name));
      }
    } else {
      methodSpec.returns(ClassName.VOID);
    }

    if (mediaTypesForMethod.size() > 0) {
      AnnotationSpec.Builder ann = buildAnnotation(mediaTypesForMethod, Produces.class);
      methodSpec.addAnnotation(ann.build());
    }
    return methodSpec;
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

    Collection<String> mediaTypes = ramlTypeToMediaType.get(typeDeclaration.name());

    AnnotationSpec.Builder ann = buildAnnotation(mediaTypes, Consumes.class);
    methodSpec.addAnnotation(ann.build());
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
              .addMember("value", "$S", uri).build());

      buildResource(typeSpec, topResource);

      recurse(typeSpec, topResource);

      typeSpec =
          build.getResourceClassExtension(NULL_EXTENSION, Annotations.ON_RESOURCE_CLASS_FINISH, topResource)
              .onResource(new ResourceContextImpl(build), topResource, typeSpec);
      return typeSpec;
    }
  }
}
