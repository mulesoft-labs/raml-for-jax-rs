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
import com.squareup.javapoet.TypeSpec;
import org.raml.jaxrs.generator.ramltypes.GMethod;
import org.raml.jaxrs.generator.ramltypes.GResource;
import org.raml.jaxrs.generator.ramltypes.GResponse;
import org.raml.jaxrs.generator.v08.V08GResource;
import org.raml.jaxrs.generator.v08.V08Method;
import org.raml.jaxrs.generator.v08.V08Response;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jean-Philippe Belanger on 1/22/17. Just potential zeroes and ones
 *
 * QUick and dirty
 */
public interface GlobalResourceExtension<M extends GMethod, R extends GResource, S extends GResponse> extends
    ResponseClassExtension<M>,
    ResourceClassExtension<R>,
    ResponseMethodExtension<S>,
    ResourceMethodExtension<M> {

  GlobalResourceExtension<V08Method, V08GResource, V08Response> NULL_EXTENSION =
      new GlobalResourceExtension<V08Method, V08GResource, V08Response>() {

        @Override
        public TypeSpec.Builder onResource(ResourceContext context, V08GResource resource, TypeSpec.Builder typeSpec) {
          return typeSpec;
        }

        @Override
        public MethodSpec.Builder onMethod(ResourceContext context, V08Method method, MethodSpec.Builder methodSpec) {
          return methodSpec;
        }

        @Override
        public TypeSpec.Builder onResponseClass(ResourceContext context, V08Method method, TypeSpec.Builder typeSpec) {
          return typeSpec;
        }

        @Override
        public MethodSpec.Builder onMethod(ResourceContext context, V08Response responseMethod, MethodSpec.Builder methodSpec) {
          return methodSpec;
        }
      };

  public class Composite<M extends GMethod, R extends GResource, S extends GResponse> implements GlobalResourceExtension<M, R, S> {

    private List<GlobalResourceExtension<M, R, S>> extensions = new ArrayList<>();

    @Override
    public TypeSpec.Builder onResource(ResourceContext context, R resource, TypeSpec.Builder typeSpec) {

      for (GlobalResourceExtension<M, R, S> extension : extensions) {
        typeSpec = extension.onResource(context, resource, typeSpec);
      }
      return typeSpec;
    }

    @Override
    public MethodSpec.Builder onMethod(ResourceContext context, M method, MethodSpec.Builder methodSpec) {
      for (GlobalResourceExtension<M, R, S> extension : extensions) {
        methodSpec = extension.onMethod(context, method, methodSpec);
      }
      return methodSpec;
    }

    @Override
    public TypeSpec.Builder onResponseClass(ResourceContext context, M method, TypeSpec.Builder typeSpec) {

      for (GlobalResourceExtension<M, R, S> extension : extensions) {
        typeSpec = extension.onResponseClass(context, method, typeSpec);
      }
      return typeSpec;
    }

    @Override
    public MethodSpec.Builder onMethod(ResourceContext context, S responseMethod, MethodSpec.Builder methodSpec) {
      for (GlobalResourceExtension<M, R, S> extension : extensions) {
        methodSpec = extension.onMethod(context, responseMethod, methodSpec);
      }
      return methodSpec;
    }

    public void addExtension(GlobalResourceExtension<M, R, S> extension) {

      extensions.add(extension);
    }
  }
}
