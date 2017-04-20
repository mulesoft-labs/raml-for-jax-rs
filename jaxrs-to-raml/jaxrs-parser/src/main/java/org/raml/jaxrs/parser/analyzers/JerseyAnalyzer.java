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

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;

import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.server.model.RuntimeResource;
import org.raml.jaxrs.model.JaxRsApplication;
import org.raml.jaxrs.model.JaxRsSupportedAnnotation;
import org.raml.jaxrs.parser.model.JerseyJaxRsApplication;
import org.raml.jaxrs.parser.source.SourceParser;
import org.raml.utilities.format.Joiners;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link Analyzer} implementation leveraging Jersey code to extract a JaxRsApplication from a set of {@link Class} that contain
 * JAX-RS code.
 */
class JerseyAnalyzer implements Analyzer {

  private static final Logger logger = LoggerFactory.getLogger(JerseyAnalyzer.class);

  private final ImmutableSet<Class<?>> jaxRsClasses;
  private final JerseyBridge jerseyBridge;
  private final SourceParser sourceParser;
  private final Set<JaxRsSupportedAnnotation> supportedAnnotations;

  private JerseyAnalyzer(ImmutableSet<Class<?>> jaxRsClasses, JerseyBridge jerseyBridge,
                         SourceParser sourceParser, Set<JaxRsSupportedAnnotation> supportedAnnotations) {
    this.jaxRsClasses = jaxRsClasses;
    this.jerseyBridge = jerseyBridge;
    this.sourceParser = sourceParser;
    this.supportedAnnotations = supportedAnnotations;
  }

  static JerseyAnalyzer create(Iterable<Class<?>> classes, JerseyBridge jerseyBridge,
                               SourceParser sourceParser, Set<JaxRsSupportedAnnotation> supportedAnnotations) {
    checkNotNull(classes);
    checkNotNull(jerseyBridge);
    checkNotNull(sourceParser);

    return new JerseyAnalyzer(ImmutableSet.copyOf(classes), jerseyBridge, sourceParser, supportedAnnotations);
  }

  @Override
  public JaxRsApplication analyze() {
    logger.debug("analyzing...");

    // The first step is to extract the Jersey resources from the classes.
    FluentIterable<Resource> jerseyResources = jerseyBridge.resourcesFrom(jaxRsClasses);

    if (logger.isDebugEnabled()) {
      logger.debug("found jersey resources: \n{}",
                   Joiners.squareBracketsPerLineJoiner().join(jerseyResources));
    }

    // We then transform them into what they call RuntimeResources, which are basically
    // the resolved resources.
    List<RuntimeResource> runtimeResources = jerseyBridge.runtimeResourcesFrom(jerseyResources);

    if (logger.isDebugEnabled()) {
      logger.debug("found runtime resources: \n{}",
                   Joiners.squareBracketsPerLineJoiner().join(runtimeResources));
    }

    return JerseyJaxRsApplication.fromRuntimeResources(runtimeResources, sourceParser, supportedAnnotations);
  }
}
