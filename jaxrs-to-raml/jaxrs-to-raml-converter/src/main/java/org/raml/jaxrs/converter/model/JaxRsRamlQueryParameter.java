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
import org.raml.jaxrs.model.JaxRsQueryParameter;
import org.raml.api.RamlQueryParameter;

import java.lang.annotation.Annotation;

import static com.google.common.base.Preconditions.checkNotNull;

class JaxRsRamlQueryParameter implements RamlQueryParameter {

  private final JaxRsQueryParameter queryParameter;

  private JaxRsRamlQueryParameter(JaxRsQueryParameter queryParameter) {
    this.queryParameter = queryParameter;
  }

  public static RamlQueryParameter create(JaxRsQueryParameter queryParameter) {
    checkNotNull(queryParameter);

    return new JaxRsRamlQueryParameter(queryParameter);
  }

  @Override
  public String getName() {
    return this.queryParameter.getName();
  }

  @Override
  public Optional<String> getDefaultValue() {
    return this.queryParameter.getDefaultValue();
  }

  @Override
  public RamlEntity getEntity() {
    return JaxRsRamlEntity.create(this.queryParameter.getEntity().get());
  }

  @Override
  public <T extends Annotation> Optional<T> getAnnotation(Class<T> annotationType) {
    return queryParameter.getAnnotation(annotationType);
  }


  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder(this.getClass().getSimpleName());
    builder.append("{ ");
    builder.append("name: ").append(this.getName());
    builder.append(" }");
    return builder.toString();
  }
}
