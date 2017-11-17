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

import com.squareup.javapoet.TypeSpec;
import org.raml.jaxrs.generator.extension.AbstractCompositeExtension;
import org.raml.jaxrs.generator.ramltypes.GMethod;

import java.util.List;

/**
 * Created by Jean-Philippe Belanger on 1/12/17. Just potential zeroes and ones
 */
public interface ResponseClassExtension<T extends GMethod> {

  ResponseClassExtension<GMethod> NULL_EXTENSION = new ResponseClassExtension<GMethod>() {

    @Override
    public TypeSpec.Builder onResponseClass(ResourceContext context, GMethod method, TypeSpec.Builder typeSpec) {
      return typeSpec;
    }
  };

  class Composite extends AbstractCompositeExtension<ResponseClassExtension<GMethod>, TypeSpec.Builder> implements
      ResponseClassExtension<GMethod> {

    public Composite(List<ResponseClassExtension<GMethod>> extensions) {
      super(extensions);
    }

    @Override
    public TypeSpec.Builder onResponseClass(final ResourceContext context, final GMethod method, TypeSpec.Builder typeSpec) {

      return runList(typeSpec, new ElementJob<ResponseClassExtension<GMethod>, TypeSpec.Builder>() {

        @Override
        public TypeSpec.Builder doElement(ResponseClassExtension<GMethod> e, TypeSpec.Builder builder) {
          return e.onResponseClass(context, method, builder);
        }
      });
    }
  }

  TypeSpec.Builder onResponseClass(ResourceContext context, T method, TypeSpec.Builder typeSpec);

}
