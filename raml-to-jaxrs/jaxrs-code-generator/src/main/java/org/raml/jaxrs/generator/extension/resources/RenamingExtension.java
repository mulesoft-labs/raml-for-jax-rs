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
import org.raml.jaxrs.generator.extension.resources.api.GlobalResourceExtension;
import org.raml.jaxrs.generator.extension.resources.api.ResourceContext;

import java.util.Map;

/**
 * Created. There, you have it.
 */
public class RenamingExtension extends GlobalResourceExtension.Helper {

  private final Map<String, String> args;

  public RenamingExtension(Map<String, String> args) {
    this.args = args;
  }

  @Override
  public ClassName resourceClassName(ResourceContext context, EndPoint resource, ClassName originalName) {
    return ClassName.get(context.getResourcePackage(), args.get("resourceClass"));
  }

  @Override
  public String methodName(ResourceContext context, Operation method, Request gRequest, Payload payload, String originalMethodName) {
    return args.get("methodName");
  }

  @Override
  public ClassName responseClassName(ResourceContext context, Operation method, ClassName originalName) {
    return ClassName.get(context.getResourcePackage(), args.get("responseClass"));
  }

  @Override
  public String methodName(ResourceContext context, Response responseMethod, String originalMethodName) {
    return args.get("responseMethod");
  }
}
