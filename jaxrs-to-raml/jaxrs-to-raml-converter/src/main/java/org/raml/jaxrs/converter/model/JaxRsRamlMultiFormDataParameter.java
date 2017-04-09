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
package org.raml.jaxrs.converter.model;

import org.raml.api.RamlEntity;
import org.raml.api.RamlMultiFormDataParameter;
import org.raml.jaxrs.model.JaxRsMultiPartFormDataParameter;

/**
 * Created by Jean-Philippe Belanger on 4/8/17. Just potential zeroes and ones
 */
public class JaxRsRamlMultiFormDataParameter implements RamlMultiFormDataParameter {

  private final JaxRsMultiPartFormDataParameter input;

  public JaxRsRamlMultiFormDataParameter(JaxRsMultiPartFormDataParameter input) {

    this.input = input;
  }

  public static RamlMultiFormDataParameter create(JaxRsMultiPartFormDataParameter input) {
    return new JaxRsRamlMultiFormDataParameter(input);
  }

  @Override
  public String getName() {
    return input.getName();
  }

  @Override
  public RamlEntity getPartEntity() {

    return new JaxRsRamlEntity(input.getPartEntity());
  }
}
