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
package org.raml.jaxrs.parser.model;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;

import org.glassfish.jersey.server.model.RuntimeResource;
import org.raml.jaxrs.model.JaxRsApplication;
import org.raml.jaxrs.model.JaxRsResource;
import org.raml.jaxrs.model.JaxRsSupportedAnnotation;
import org.raml.jaxrs.parser.source.SourceParser;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import static com.google.common.base.Preconditions.checkNotNull;

public class JerseyJaxRsApplication implements JaxRsApplication {

  private final Set<JaxRsResource> resources;
  private final Set<JaxRsSupportedAnnotation> supportedAnnotations;

  private JerseyJaxRsApplication(Set<JaxRsResource> resources, Set<JaxRsSupportedAnnotation> supportedAnnotations) {
    this.resources = resources;
    this.supportedAnnotations = supportedAnnotations;
  }

  private static JerseyJaxRsApplication create(Iterable<JaxRsResource> resources,
                                               Set<JaxRsSupportedAnnotation> supportedAnnotations) {
    checkNotNull(resources);

    return new JerseyJaxRsApplication(ImmutableSet.copyOf(resources), supportedAnnotations);
  }

  public static JerseyJaxRsApplication fromRuntimeResources(
                                                            Iterable<RuntimeResource> runtimeResources,
                                                            final SourceParser sourceParser,
                                                            Set<JaxRsSupportedAnnotation> supportedAnnotations) {
    return create(FluentIterable.from(runtimeResources).transform(
                                                                  new Function<RuntimeResource, JaxRsResource>() {

                                                                    @Nullable
                                                                    @Override
                                                                    public JaxRsResource apply(@Nullable RuntimeResource runtimeResource) {
                                                                      return JerseyJaxRsResource.create(runtimeResource,
                                                                                                        sourceParser);
                                                                    }
                                                                  }), supportedAnnotations);
  }

  @Override
  public Set<JaxRsResource> getResources() {
    return resources;
  }

  @Override
  public Set<JaxRsSupportedAnnotation> getSupportedAnnotations() {
    return supportedAnnotations;
  }
}
