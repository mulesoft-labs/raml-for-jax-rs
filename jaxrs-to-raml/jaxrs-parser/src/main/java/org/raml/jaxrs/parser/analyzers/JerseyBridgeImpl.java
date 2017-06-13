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
package org.raml.jaxrs.parser.analyzers;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.server.model.RuntimeResource;
import org.glassfish.jersey.server.model.RuntimeResourceModel;

import java.util.List;

import javax.annotation.Nullable;

class JerseyBridgeImpl implements JerseyBridge {

  @Override
  public FluentIterable<Resource> resourcesFrom(Iterable<Class<?>> jaxRsClasses) {
    return FluentIterable.from(jaxRsClasses).transform(new Function<Class<?>, Resource>() {

      @Nullable
      @Override
      public Resource apply(@Nullable Class<?> aClass) {
        return Resource.from(aClass);
      }
    }).filter(Resource.class); // remove nulls from list
  }

  @Override
  public List<RuntimeResource> runtimeResourcesFrom(FluentIterable<Resource> resources) {
    RuntimeResourceModel resourceModel = new RuntimeResourceModel(resources.toList());
    return resourceModel.getRuntimeResources();
  }
}
