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

import org.raml.jaxrs.generator.ramltypes.GParameter;
import org.raml.jaxrs.generator.ramltypes.GType;
import org.raml.v2.api.model.v08.parameters.Parameter;

/**
 * Created by Jean-Philippe Belanger on 12/11/16. Just potential zeroes and ones
 */
public class V08GParameter implements GParameter {

  private final Parameter input;
  private final V08GType type;

  public V08GParameter(org.raml.v2.api.model.v08.parameters.Parameter input) {

    this.input = input;
    this.type = new V08GType(input.type());
  }

  @Override
  public String defaultValue() {
    return input.defaultValue();
  }

  @Override
  public Parameter implementation() {
    return input;
  }

  @Override
  public String name() {
    return input.name();
  }

  @Override
  public GType type() {
    return type;
  }
}
