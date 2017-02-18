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
package org.raml.jaxrs.generator.v08;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.raml.jaxrs.generator.GAbstractionFactory;
import org.raml.jaxrs.generator.ramltypes.GMethod;
import org.raml.jaxrs.generator.ramltypes.GParameter;
import org.raml.jaxrs.generator.ramltypes.GResource;
import org.raml.v2.api.model.v08.methods.Method;
import org.raml.v2.api.model.v08.parameters.Parameter;
import org.raml.v2.api.model.v08.resources.Resource;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

/**
 * Created by Jean-Philippe Belanger on 12/11/16. Just potential zeroes and ones
 */
public class V08GResource implements GResource {

  private final GAbstractionFactory factory;
  private final GResource parent;
  private final Resource resource;
  private final V08TypeRegistry registry;
  private final List<GResource> subResources;
  private final List<GParameter> uriParameters;
  private final List<GMethod> methods;

  public V08GResource(GAbstractionFactory factory, Resource resource, Set<String> globalSchemas,
                      V08TypeRegistry registry) {

    this(factory, null, resource, globalSchemas, registry);
  }

  public V08GResource(final GAbstractionFactory factory, GResource parent, Resource resource,
                      final Set<String> globalSchemas, final V08TypeRegistry registry) {
    this.factory = factory;
    this.parent = parent;
    this.resource = resource;
    this.registry = registry;
    this.subResources = Lists.transform(resource.resources(), new Function<Resource, GResource>() {

      @Nullable
      @Override
      public GResource apply(@Nullable Resource input) {

        return factory.newResource(globalSchemas, registry, V08GResource.this, input);
      }
    });

    this.uriParameters =
        Lists.transform(resource.uriParameters(), new Function<Parameter, GParameter>() {

          @Nullable
          @Override
          public GParameter apply(@Nullable Parameter input) {
            return new V08GParameter(input);
          }
        });

    this.methods = Lists.transform(resource.methods(), new Function<Method, GMethod>() {

      @Nullable
      @Override
      public GMethod apply(@Nullable Method input) {
        return new V08Method(V08GResource.this, input, globalSchemas, registry);
      }
    });

  }

  @Override
  public Resource implementation() {
    return resource;
  }

  @Override
  public List<GResource> resources() {
    return subResources;
  }

  @Override
  public List<GMethod> methods() {
    return methods;
  }

  @Override
  public List<GParameter> uriParameters() {
    return uriParameters;
  }

  @Override
  public String resourcePath() {
    return resource.resourcePath();
  }

  @Override
  public GResource parentResource() {
    return parent;
  }

  @Override
  public String relativePath() {
    return resource.relativeUri().value();
  }
}
