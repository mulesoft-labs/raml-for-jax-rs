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

import amf.client.model.domain.Operation;
import com.squareup.javapoet.TypeSpec;

import java.util.Collection;

/**
 * Created by Jean-Philippe Belanger on 1/12/17. Just potential zeroes and ones
 */
public interface ResponseClassExtension {

  ResponseClassExtension NULL_EXTENSION = (context, method, typeSpec) -> typeSpec;

  class Composite extends AbstractCompositeExtension<ResponseClassExtension, TypeSpec.Builder> implements
      ResponseClassExtension {

    public Composite(Collection<ResponseClassExtension> extensions) {
      super(extensions);
    }

    @Override
    public TypeSpec.Builder onResponseClass(final ResourceContext context, final Operation method, TypeSpec.Builder typeSpec) {

      return runList(typeSpec, (e, b) -> e.onResponseClass(context, method, b));
    }
  }

  TypeSpec.Builder onResponseClass(ResourceContext context, Operation method, TypeSpec.Builder typeSpec);

}
