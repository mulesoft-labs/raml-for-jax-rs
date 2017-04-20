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
import org.raml.jaxrs.model.JaxRsEntity;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Jean-Philippe Belanger on 3/26/17. Just potential zeroes and ones
 */
public class JaxRsRamlEntity implements RamlEntity {

  private final JaxRsEntity entity;

  public JaxRsRamlEntity(JaxRsEntity entity) {
    this.entity = entity;
  }

  public static JaxRsRamlEntity create(JaxRsEntity entity) {
    checkNotNull(entity);

    return new JaxRsRamlEntity(entity);
  }

  @Override
  public Type getType() {
    return entity.getType();
  }

  @Override
  public Optional<String> getDescription() {
    return entity.getDescription();
  }

  public RamlEntity createDependent(Type type) {

    return new JaxRsRamlEntity(entity.createJaxRsEntity(type));
  }

  @Override
  public <T extends Annotation> Optional<T> getAnnotation(Class<T> annotationType) {
    return entity.getAnnotation(annotationType);
  }

  @Override
  public String toString() {
    return "[entity of type: " + entity.getType() + "]";
  }
}
