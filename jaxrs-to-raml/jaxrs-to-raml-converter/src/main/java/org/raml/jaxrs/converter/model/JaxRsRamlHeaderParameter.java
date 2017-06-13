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

import com.google.common.base.Optional;

import org.raml.api.RamlEntity;
import org.raml.api.RamlHeaderParameter;
import org.raml.jaxrs.model.JaxRsHeaderParameter;

import java.lang.annotation.Annotation;

import static com.google.common.base.Preconditions.checkNotNull;

class JaxRsRamlHeaderParameter implements RamlHeaderParameter {

  private final JaxRsHeaderParameter parameter;

  private JaxRsRamlHeaderParameter(JaxRsHeaderParameter parameter) {
    this.parameter = parameter;
  }

  public static RamlHeaderParameter create(JaxRsHeaderParameter jaxRsHeaderParameter) {
    checkNotNull(jaxRsHeaderParameter);

    return new JaxRsRamlHeaderParameter(jaxRsHeaderParameter);
  }

  @Override
  public String getName() {
    return parameter.getName();
  }

  @Override
  public Optional<String> getDefaultValue() {
    return parameter.getDefaultValue();
  }

  @Override
  public RamlEntity getEntity() {
    return JaxRsRamlEntity.create(parameter.getEntity().get());
  }

  @Override
  public <T extends Annotation> Optional<T> getAnnotation(Class<T> annotationType) {
    return parameter.getAnnotation(annotationType);
  }
}
