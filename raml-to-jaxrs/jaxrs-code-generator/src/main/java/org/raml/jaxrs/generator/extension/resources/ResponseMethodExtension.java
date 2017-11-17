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
import org.raml.jaxrs.generator.extension.AbstractCompositeExtension;
import org.raml.jaxrs.generator.ramltypes.GMethod;
import org.raml.jaxrs.generator.ramltypes.GResponse;

import java.util.List;

/**
 * Created by Jean-Philippe Belanger on 1/12/17. Just potential zeroes and ones
 */
public interface ResponseMethodExtension<T extends GResponse> {

  ResponseMethodExtension<GResponse> NULL_EXTENSION = new ResponseMethodExtension<GResponse>() {

    @Override
    public MethodSpec.Builder onMethod(ResourceContext context, GResponse responseMethod, MethodSpec.Builder methodSpec) {
      return methodSpec;
    }
  };

  class Composite extends AbstractCompositeExtension<ResponseMethodExtension<GResponse>, MethodSpec.Builder> implements
      ResponseMethodExtension<GResponse> {

    public Composite(List<ResponseMethodExtension<GResponse>> extensions) {
      super(extensions);
    }

    @Override
    public MethodSpec.Builder onMethod(final ResourceContext context, final GResponse responseMethod,
                                       MethodSpec.Builder methodSpec) {

      return runList(methodSpec, new ElementJob<ResponseMethodExtension<GResponse>, MethodSpec.Builder>() {

        @Override
        public MethodSpec.Builder doElement(ResponseMethodExtension<GResponse> e, MethodSpec.Builder builder) {
          return e.onMethod(context, responseMethod, builder);
        }
      });
    }
  }

  MethodSpec.Builder onMethod(ResourceContext context, T responseMethod, MethodSpec.Builder methodSpec);
}
