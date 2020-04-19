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
package org.raml.jaxrs.generator.extension.resources;

import amf.client.model.domain.*;
import com.squareup.javapoet.*;
import org.raml.jaxrs.generator.extension.resources.api.GlobalResourceExtension;
import org.raml.jaxrs.generator.extension.resources.api.ResourceContext;

import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import java.util.List;

/**
 * Created. There, you have it.
 */
public class ListEntityValueResourceExtension implements GlobalResourceExtension {

  private final List<String> arguments;

  public ListEntityValueResourceExtension(List<String> arguments) {
    this.arguments = arguments;
  }

  @Override
  public TypeSpec.Builder onResource(ResourceContext context, EndPoint resource, TypeSpec.Builder typeSpec) {
    return typeSpec;
  }

  @Override
  public MethodSpec.Builder onMethod(ResourceContext context, Operation method, Request gRequest, Payload payload, MethodSpec.Builder methodSpec) {
    MethodSpec old = methodSpec.build();
    MethodSpec.Builder newMethod = MethodSpec.methodBuilder(old.name)
            .returns(old.returnType);

    addParameters(old, newMethod);

    for (AnnotationSpec annotation : old.annotations) {
      newMethod.addAnnotation(annotation);
    }

    newMethod.addJavadoc("$L", old.javadoc);
    newMethod.addModifiers(old.modifiers);
    if (old.javadoc != null) {
      newMethod.addJavadoc("$S", old.javadoc);
    }

    for (TypeName exception : old.exceptions) {

      newMethod.addException(exception);
    }

    return newMethod;
  }

  @Override
  public TypeSpec.Builder onResponseClass(ResourceContext context, Operation method, TypeSpec.Builder typeSpec) {
    return typeSpec;
  }

  @Override
  public MethodSpec.Builder onMethod(ResourceContext context, Response responseMethod, MethodSpec.Builder methodSpec) {
    MethodSpec old = methodSpec.build();
    MethodSpec.Builder newMethod = MethodSpec.methodBuilder(old.name)
            .returns(old.returnType);

    addParameters(old, newMethod);

    for (AnnotationSpec annotation : old.annotations) {
      newMethod.addAnnotation(annotation);
    }

    newMethod.addJavadoc("$L", old.javadoc);
    newMethod.addModifiers(old.modifiers);
    if (old.javadoc != null) {
      newMethod.addJavadoc("$S", old.javadoc);
    }

    for (TypeName exception : old.exceptions) {

      newMethod.addException(exception);
    }

    newMethod.addCode(old.code);
    return newMethod;
  }

  private void addParameters(MethodSpec old, MethodSpec.Builder newMethod) {
    for (ParameterSpec parameter : old.parameters) {
      if ((int) parameter.annotations.stream().filter(annotationSpec -> annotationSpec.type.equals(ClassName.get(PathParam.class))
              || annotationSpec.type.equals(ClassName.get(QueryParam.class))).count() > 0) {
        newMethod.addParameter(parameter);
      }
    }

    for (ParameterSpec parameter : old.parameters) {
      if ((int) parameter.annotations.stream().filter(annotationSpec -> annotationSpec.type.equals(ClassName.get(PathParam.class))
              || annotationSpec.type.equals(ClassName.get(QueryParam.class))).count() == 0) {

        newMethod.addParameter(ParameterSpec.builder(ParameterizedTypeName.get(ClassName.get(List.class),
                                                                               ClassName.bestGuess(arguments.get(0))), "entity")
            .build());
      }
    }
  }
}
