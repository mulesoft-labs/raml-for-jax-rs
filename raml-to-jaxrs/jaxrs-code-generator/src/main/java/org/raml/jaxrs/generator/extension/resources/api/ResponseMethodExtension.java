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

import amf.client.model.domain.Response;
import com.squareup.javapoet.MethodSpec;

import java.util.Collection;

/**
 * Created by Jean-Philippe Belanger on 1/12/17. Just potential zeroes and ones
 */
public interface ResponseMethodExtension {

  ResponseMethodExtension NULL_EXTENSION = (context, responseMethod, methodSpec) -> methodSpec;

  class Composite extends AbstractCompositeExtension<ResponseMethodExtension, MethodSpec.Builder> implements
      ResponseMethodExtension {

    public Composite(Collection<ResponseMethodExtension> extensions) {
      super(extensions);
    }

    @Override
    public MethodSpec.Builder onMethod(final ResourceContext context, final Response responseMethod,
                                       MethodSpec.Builder methodSpec) {

      return runList(methodSpec, (e, b) -> e.onMethod(context, responseMethod, b));
    }
  }

  MethodSpec.Builder onMethod(ResourceContext context, Response responseMethod, MethodSpec.Builder methodSpec);
}
