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
import org.raml.jaxrs.model.JaxRsQueryParameter;

import java.lang.reflect.Type;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

class JerseyJaxRsQueryParameter implements JaxRsQueryParameter {

  private final Parameter parameter;

  private JerseyJaxRsQueryParameter(Parameter parameter) {
    this.parameter = parameter;
  }

  static JerseyJaxRsQueryParameter create(Parameter parameter) {
    checkNotNull(parameter);
    checkArgument(Utilities.isQueryParameterPredicate().apply(parameter),
                  "invalid query parameter %s", parameter);

    return new JerseyJaxRsQueryParameter(parameter);
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
  public Type getType() {
    return this.parameter.getType();
  }
}
