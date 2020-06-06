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

import amf.client.model.domain.EndPoint;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeSpec;

import java.util.Collection;

/**
 * Created by Jean-Philippe Belanger on 1/12/17. Just potential zeroes and ones
 */
public interface ResourceClassExtension {

  class Composite extends AbstractCompositeExtension<ResourceClassExtension> implements
      ResourceClassExtension {

    public Composite(Collection<ResourceClassExtension> extensions) {
      super(extensions);
    }

    @Override
    public ClassName resourceClassName(ResourceContext context, EndPoint resource, ClassName originalName) {
      return runList(originalName, (e, b) -> e.resourceClassName(context, resource, b));
    }

    @Override
    public TypeSpec.Builder onResource(final ResourceContext context, final EndPoint resource, TypeSpec.Builder builder) {

      return runList(builder, (e, b) -> e.onResource(context, resource, b));
    }
  }

  ResourceClassExtension NULL_EXTENSION = new ResourceClassExtension() {

    @Override
    public ClassName resourceClassName(ResourceContext context, EndPoint resource, ClassName originalName) {
      return originalName;
    }

    @Override
    public TypeSpec.Builder onResource(ResourceContext context, EndPoint resource, TypeSpec.Builder typeSpec) {
      return typeSpec;
    }
  };

  ClassName resourceClassName(ResourceContext context, EndPoint resource, ClassName originalName);

  TypeSpec.Builder onResource(ResourceContext context, EndPoint resource, TypeSpec.Builder typeSpec);
}
