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

import org.glassfish.jersey.server.model.Parameter;
import org.raml.jaxrs.model.JaxRsEntity;
import org.raml.jaxrs.model.JaxRsHeaderParameter;
import org.raml.jaxrs.parser.source.SourceParser;

import java.lang.annotation.Annotation;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

class JerseyJaxRsHeaderParameter implements JaxRsHeaderParameter {

  private final Parameter parameter;
  private final SourceParser sourceParser;

  private JerseyJaxRsHeaderParameter(Parameter parameter, SourceParser sourceParser) {
    this.parameter = parameter;
    this.sourceParser = sourceParser;
  }

  static JerseyJaxRsHeaderParameter create(Parameter parameter, SourceParser sourceParser) {
    checkNotNull(parameter);
    checkArgument(Utilities.isHeaderParameterPredicate().apply(parameter),
                  "invalid header parameter %s", parameter);

    return new JerseyJaxRsHeaderParameter(parameter, sourceParser);
  }

  @Override
  public String getName() {
    return this.parameter.getSourceName();
  }

  @Override
  public Optional<String> getDefaultValue() {
    return Optional.fromNullable(this.parameter.getDefaultValue());
  }

  @Override
  public Optional<JaxRsEntity> getEntity() {
    return JerseyJaxRsEntity.create(this.parameter.getType(), sourceParser);
  }

  @Override
  public <T extends Annotation> Optional<T> getAnnotation(Class<T> annotationType) {

    return Optional.fromNullable((parameter).getAnnotation(annotationType));
  }
}
