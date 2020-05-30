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
package org.raml.jaxrs.generator.extension.resources.api;

import amf.client.model.domain.*;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jean-Philippe Belanger on 1/22/17. Just potential zeroes and ones
 *
 * QUick and dirty
 */
public interface GlobalResourceExtension extends
    ResponseClassExtension,
    ResourceClassExtension,
    ResponseMethodExtension,
    ResourceMethodExtension {

  GlobalResourceExtension NULL_EXTENSION =
      new GlobalResourceExtension() {

        @Override
        public TypeSpec.Builder onResource(ResourceContext context, EndPoint resource, TypeSpec.Builder typeSpec) {
          return typeSpec;
        }

        @Override
        public MethodSpec.Builder onMethod(ResourceContext context, Operation method, Request gRequest,
                                           Payload payload, MethodSpec.Builder methodSpec) {
          return methodSpec;
        }

        @Override
        public TypeSpec.Builder onResponseClass(ResourceContext context, Operation method, TypeSpec.Builder typeBuilder) {
          return typeBuilder;
        }

        @Override
        public MethodSpec.Builder onMethod(ResourceContext context, Response responseMethod, MethodSpec.Builder methodSpec) {
          return methodSpec;
        }

        @Override
        public String resourceClassName(ResourceContext context, EndPoint resource, String originalName) {
          return originalName;
        }

        @Override
        public String methodName(ResourceContext context, Operation method, Request gRequest, Payload payload,
                                 String originalMethodName) {
          return originalMethodName;
        }

        @Override
        public String responseClassName(ResourceContext context, Operation method, String originalName) {
          return originalName;
        }

        @Override
        public String methodName(ResourceContext context, Response responseMethod, String originalMethodName) {
          return originalMethodName;
        }
      };

  class Composite implements GlobalResourceExtension {

    private List<GlobalResourceExtension> extensions = new ArrayList<>();

    @Override
    public TypeSpec.Builder onResource(ResourceContext context, EndPoint resource, TypeSpec.Builder typeSpec) {

      for (GlobalResourceExtension extension : extensions) {
        typeSpec = extension.onResource(context, resource, typeSpec);
      }
      return typeSpec;
    }

    @Override
    public MethodSpec.Builder onMethod(ResourceContext context, Operation method, Request gRequest, Payload payload,
                                       MethodSpec.Builder methodSpec) {
      for (GlobalResourceExtension extension : extensions) {
        methodSpec = extension.onMethod(context, method, gRequest, payload, methodSpec);
      }
      return methodSpec;
    }

    @Override
    public TypeSpec.Builder onResponseClass(ResourceContext context, Operation method, TypeSpec.Builder typeBuilder) {

      for (GlobalResourceExtension extension : extensions) {
        typeBuilder = extension.onResponseClass(context, method, typeBuilder);
      }
      return typeBuilder;
    }

    @Override
    public MethodSpec.Builder onMethod(ResourceContext context, Response responseMethod, MethodSpec.Builder methodSpec) {
      for (GlobalResourceExtension extension : extensions) {
        methodSpec = extension.onMethod(context, responseMethod, methodSpec);
      }
      return methodSpec;
    }

    @Override
    public String resourceClassName(ResourceContext context, EndPoint resource, String originalName) {
      for (GlobalResourceExtension extension : extensions) {
        originalName = extension.resourceClassName(context, resource, originalName);
      }
      return originalName;
    }

    @Override
    public String methodName(ResourceContext context, Operation method, Request gRequest, Payload payload,
                             String originalMethodName) {
      for (GlobalResourceExtension extension : extensions) {
        originalMethodName = extension.methodName(context, method, gRequest, payload, originalMethodName);
      }
      return originalMethodName;
    }

    @Override
    public String responseClassName(ResourceContext context, Operation method, String originalName) {
      for (GlobalResourceExtension extension : extensions) {
        originalName = extension.responseClassName(context, method, originalName);
      }
      return originalName;
    }

    @Override
    public String methodName(ResourceContext context, Response responseMethod, String originalMethodName) {
      for (GlobalResourceExtension extension : extensions) {
        originalMethodName = extension.methodName(context, responseMethod, originalMethodName);
      }
      return originalMethodName;
    }

    public void addExtension(GlobalResourceExtension extension) {

      extensions.add(extension);
    }
  }

  class Helper implements GlobalResourceExtension {

    @Override
    public TypeSpec.Builder onResource(ResourceContext context, EndPoint resource, TypeSpec.Builder typeSpec) {
      return typeSpec;
    }

    @Override
    public MethodSpec.Builder onMethod(ResourceContext context, Operation method, Request gRequest,
                                       Payload payload, MethodSpec.Builder methodSpec) {
      return methodSpec;
    }

    @Override
    public TypeSpec.Builder onResponseClass(ResourceContext context, Operation method, TypeSpec.Builder typeBuilder) {
      return typeBuilder;
    }

    @Override
    public MethodSpec.Builder onMethod(ResourceContext context, Response responseMethod, MethodSpec.Builder methodSpec) {
      return methodSpec;
    }

    @Override
    public String resourceClassName(ResourceContext context, EndPoint resource, String originalName) {
      return originalName;
    }

    @Override
    public String methodName(ResourceContext context, Operation method, Request gRequest, Payload payload,
                             String originalMethodName) {
      return originalMethodName;
    }

    @Override
    public String responseClassName(ResourceContext context, Operation method, String originalName) {
      return originalName;
    }

    @Override
    public String methodName(ResourceContext context, Response responseMethod, String originalMethodName) {
      return originalMethodName;
    }
  }
}
