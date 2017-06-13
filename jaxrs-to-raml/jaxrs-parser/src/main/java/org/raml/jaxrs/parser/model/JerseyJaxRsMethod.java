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

import com.google.common.base.Optional;

import com.google.common.collect.FluentIterable;
import org.glassfish.jersey.server.model.ResourceMethod;
import org.raml.jaxrs.model.HttpVerb;
import org.raml.jaxrs.model.JaxRsEntity;
import org.raml.jaxrs.model.JaxRsFormParameter;
import org.raml.jaxrs.model.JaxRsHeaderParameter;
import org.raml.jaxrs.model.JaxRsMethod;
import org.raml.jaxrs.model.JaxRsMultiPartFormDataParameter;
import org.raml.jaxrs.model.JaxRsQueryParameter;
import org.raml.jaxrs.parser.source.SourceParser;

import java.lang.annotation.Annotation;
import java.util.List;

import javax.ws.rs.core.MediaType;

import static com.google.common.base.Preconditions.checkNotNull;

class JerseyJaxRsMethod implements JaxRsMethod {

  private final ResourceMethod resourceMethod;
  private final SourceParser sourceParser;

  private JerseyJaxRsMethod(ResourceMethod resourceMethod, SourceParser sourceParser) {
    this.resourceMethod = resourceMethod;
    this.sourceParser = sourceParser;
  }

  public static JerseyJaxRsMethod create(ResourceMethod resourceMethod, SourceParser sourceParser) {
    checkNotNull(resourceMethod);
    checkNotNull(sourceParser);

    return new JerseyJaxRsMethod(resourceMethod, sourceParser);
  }

  @Override
  public HttpVerb getHttpVerb() {
    return HttpVerb.fromStringUnchecked(resourceMethod.getHttpMethod());
  }

  @Override
  public List<MediaType> getConsumedMediaTypes() {
    return resourceMethod.getConsumedTypes();
  }

  @Override
  public List<MediaType> getProducedMediaTypes() {
    return resourceMethod.getProducedTypes();
  }

  @Override
  public List<JaxRsQueryParameter> getQueryParameters() {
    return Utilities.toJaxRsQueryParameters(Utilities.getQueryParameters(resourceMethod), sourceParser).toList();
  }

  @Override
  public List<JaxRsHeaderParameter> getHeaderParameters() {
    return Utilities.toJaxRsHeaderParameters(Utilities.getHeaderParameters(resourceMethod), sourceParser)
        .toList();
  }

  @Override
  public List<JaxRsFormParameter> getFormParameters() {
    return Utilities.toJaxRsFormParameters(Utilities.getFormParameters(resourceMethod))
        .toList();
  }

  @Override
  public List<JaxRsMultiPartFormDataParameter> getMultiPartFormDataParameters() {
    return Utilities.toJaxRsMultiPartFormDataParameter(Utilities.getMultiPartFormDataParameter(resourceMethod), sourceParser)
        .toList();
  }

  @Override
  public Optional<JaxRsEntity> getConsumedEntity() {

    return Utilities.toJaxRsEntityParameters(Utilities.getConsumedParameter(resourceMethod), sourceParser);
  }

  @Override
  public Optional<JaxRsEntity> getProducedEntity() {

    return Utilities.getReturnValue(resourceMethod, sourceParser);
  }

  @Override
  public Optional<String> getDescription() {
    return sourceParser.getDocumentationFor(resourceMethod.getInvocable().getDefinitionMethod());
  }

  @Override
  public <T extends Annotation> Optional<T> getJavaAnnotation(Class<T> annotationType) {

    return Optional.fromNullable(resourceMethod.getInvocable().getHandlingMethod().getAnnotation(annotationType));
  }
}
