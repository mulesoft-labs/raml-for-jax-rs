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
package org.raml.jaxrs.handlers;

import com.google.common.base.Optional;
import org.raml.api.Annotable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * Created by Jean-Philippe Belanger on 4/17/17. Just potential zeroes and ones
 */
class FieldAnnotable implements Annotable {

  private final Field field;

  public FieldAnnotable(Field field) {
    this.field = field;
  }

  @Override
  public <T extends Annotation> Optional<T> getAnnotation(Class<T> annotationType) {
    return Optional.fromNullable(field.getAnnotation(annotationType));
  }
}
