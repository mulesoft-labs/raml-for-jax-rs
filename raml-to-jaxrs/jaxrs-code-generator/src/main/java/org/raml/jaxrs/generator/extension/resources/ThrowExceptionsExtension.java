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
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.raml.jaxrs.generator.extension.resources.api.GlobalResourceExtension;
import org.raml.jaxrs.generator.extension.resources.api.ResourceContext;
import org.raml.jaxrs.generator.ramltypes.GMethod;
import org.raml.jaxrs.generator.ramltypes.GRequest;
import org.raml.jaxrs.generator.ramltypes.GResource;
import org.raml.jaxrs.generator.ramltypes.GResponse;

import java.util.List;

/**
 * Created. There, you have it.
 */
public class ThrowExceptionsExtension implements GlobalResourceExtension {

  private final List<String> arguments;

  public ThrowExceptionsExtension(List<String> arguments) {
    this.arguments = arguments;
  }

  @Override
  public TypeSpec.Builder onResource(ResourceContext context, EndPoint resource, TypeSpec.Builder typeSpec) {
    return typeSpec;
  }

  @Override
  public MethodSpec.Builder onMethod(ResourceContext context, Operation method, Request gRequest, Payload payload, MethodSpec.Builder methodSpec) {
    if (arguments.size() > 0) {
      for (String argument : arguments) {

        methodSpec.addException(ClassName.bestGuess(argument));
      }
    } else {

      methodSpec.addException(ClassName.get(Exception.class));
    }

    return methodSpec;
  }

  @Override
  public TypeSpec.Builder onResponseClass(ResourceContext context, Operation method, TypeSpec.Builder typeSpec) {
    return typeSpec;
  }

  @Override
  public MethodSpec.Builder onMethod(ResourceContext context, Response responseMethod, MethodSpec.Builder methodSpec) {
    return methodSpec;
  }
}
