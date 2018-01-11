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
import org.raml.jaxrs.generator.ramltypes.GResource;

import java.util.List;

/**
 * Created by Jean-Philippe Belanger on 1/12/17. Just potential zeroes and ones
 */
public interface ResourceClassExtension<T extends GResource> {

  class Composite extends AbstractCompositeExtension<ResourceClassExtension<GResource>, TypeSpec.Builder> implements
      ResourceClassExtension<GResource> {

    public Composite(List<ResourceClassExtension<GResource>> extensions) {
      super(extensions);
    }

    @Override
    public TypeSpec.Builder onResource(final ResourceContext context, final GResource resource, TypeSpec.Builder builder) {

      return runList(builder, new ElementJob<ResourceClassExtension<GResource>, TypeSpec.Builder>() {

        @Override
        public TypeSpec.Builder doElement(ResourceClassExtension<GResource> e, TypeSpec.Builder builder) {
          return e.onResource(context, resource, builder);
        }
      });
    }
  }

  ResourceClassExtension<GResource> NULL_EXTENSION = new ResourceClassExtension<GResource>() {

    @Override
    public TypeSpec.Builder onResource(ResourceContext context, GResource resource, TypeSpec.Builder typeSpec) {
      return typeSpec;
    }
  };

  TypeSpec.Builder onResource(ResourceContext context, T resource, TypeSpec.Builder typeSpec);
}
