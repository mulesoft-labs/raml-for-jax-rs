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

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import org.raml.jaxrs.generator.extension.resources.api.GlobalResourceExtension;
import org.raml.jaxrs.generator.extension.resources.api.ResourceContext;
import org.raml.jaxrs.generator.ramltypes.GMethod;
import org.raml.jaxrs.generator.ramltypes.GResource;
import org.raml.jaxrs.generator.ramltypes.GResponse;
import org.raml.jaxrs.generator.v10.V10GResource;
import org.raml.ramltopojo.EventType;
import org.raml.ramltopojo.extensions.ObjectPluginContext;
import org.raml.ramltopojo.extensions.javadoc.JavadocObjectTypeHandlerPlugin;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.datamodel.ExampleSpec;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

import javax.validation.Valid;

/**
 * Created. There, you have it.
 */
public class JavadocResourceExtension implements GlobalResourceExtension {

  @Override
  public TypeSpec.Builder onResource(ResourceContext context, GResource resource, final TypeSpec.Builder typeSpec) {

    if (resource.getDescription() != null) {
      typeSpec.addJavadoc("$L\n", resource.getDescription());
    }

    return typeSpec;
  }

  @Override
  public MethodSpec.Builder onMethod(ResourceContext context, GMethod method, MethodSpec.Builder methodSpec) {

    if (method.getDescription() != null) {
      methodSpec.addJavadoc("$L\n", method.getDescription());
    }

    return methodSpec;
  }

  @Override
  public TypeSpec.Builder onResponseClass(ResourceContext context, GMethod method, TypeSpec.Builder typeSpec) {

    return typeSpec;
  }

  @Override
  public MethodSpec.Builder onMethod(ResourceContext context, GResponse responseMethod, MethodSpec.Builder methodSpec) {
    if (responseMethod.getDescription() != null) {
      methodSpec.addJavadoc("$L\n", responseMethod.getDescription());
    }

    return methodSpec;
  }
}
