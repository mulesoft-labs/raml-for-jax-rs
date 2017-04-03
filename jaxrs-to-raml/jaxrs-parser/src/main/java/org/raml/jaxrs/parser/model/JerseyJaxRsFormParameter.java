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
import org.raml.jaxrs.model.JaxRsFormParameter;

import java.lang.reflect.Type;

/**
 * Created by Jean-Philippe Belanger on 4/2/17. Just potential zeroes and ones
 */
public class JerseyJaxRsFormParameter implements JaxRsFormParameter {

  private final Parameter parameter;

  public JerseyJaxRsFormParameter(Parameter parameter) {
    this.parameter = parameter;
  }

  public static JaxRsFormParameter create(Parameter parameter) {

    return new JerseyJaxRsFormParameter(parameter);
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
