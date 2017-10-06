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
package org.raml.jaxrs.generator.extension.resources;

import com.squareup.javapoet.*;
import org.raml.jaxrs.generator.ramltypes.GMethod;
import org.raml.jaxrs.generator.ramltypes.GResource;
import org.raml.jaxrs.generator.ramltypes.GResponse;

import javax.lang.model.element.Modifier;
import javax.validation.Valid;

/**
 * Created. There, you have it.
 */
public class Jsr303ResourceExtension implements GlobalResourceExtension {

  @Override
  public TypeSpec.Builder onResource(ResourceContext context, GResource resource, TypeSpec.Builder typeSpec) {
    return typeSpec;
  }

  @Override
  public MethodSpec.Builder onMethod(ResourceContext context, GMethod method, MethodSpec.Builder methodSpec) {

    MethodSpec spec = methodSpec.build();
    MethodSpec.Builder builder = MethodSpec.methodBuilder(spec.name);
    builder.addAnnotations(spec.annotations);

    if (spec.code != null) {
      builder.addCode(spec.code);
    }
    if (spec.defaultValue != null) {
      builder.defaultValue(spec.defaultValue);
    }

    builder.addExceptions(spec.exceptions);
    if (spec.javadoc != null) {
      builder.addJavadoc("$L", spec.javadoc);
    }

    builder.addModifiers(spec.modifiers);

    for (ParameterSpec parameter : spec.parameters) {

      if (!(parameter.type.isPrimitive() || parameter.type.isBoxedPrimitive() || parameter.type.withoutAnnotations().toString()
          .equals("java.lang.String"))) {
        builder.addParameter(parameter.toBuilder().addAnnotation(Valid.class).build());
      } else {
        builder.addParameter(parameter);
      }
    }

    if (spec.returnType != null) {
      builder.returns(spec.returnType);
    }

    builder.addTypeVariables(spec.typeVariables);
    builder.varargs(spec.varargs);
    return builder;
  }

  @Override
  public TypeSpec.Builder onResponseClass(ResourceContext context, GMethod method, TypeSpec.Builder typeSpec) {
    return typeSpec;
  }

  @Override
  public MethodSpec.Builder onMethod(ResourceContext context, GResponse responseMethod, MethodSpec.Builder methodSpec) {
    return methodSpec;
  }
}
