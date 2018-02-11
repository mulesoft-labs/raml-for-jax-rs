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
package org.raml.jaxrs.types;

import com.google.common.base.Optional;
import org.raml.api.Annotable;
import org.raml.api.RamlSupportedAnnotation;
import org.raml.builder.TypeBuilder;
import org.raml.builder.TypePropertyBuilder;
import org.raml.jaxrs.emitters.Emittable;
import org.raml.jaxrs.emitters.LocalEmitter;
import org.raml.jaxrs.emitters.ModelEmitterAnnotations;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Collection;

/**
 * Created by Jean-Philippe Belanger on 3/26/17. Just potential zeroes and ones
 */
public class RamlProperty implements Emittable, Annotable {

  private final String name;
  private final Annotable source;
  private final boolean isScalar;

  public RamlProperty(String name, Annotable source, boolean isScalar) {
    this.name = name;
    this.source = source;
    this.isScalar = isScalar;
  }

  public String getName() {
    return name;
  }

  public <T extends Annotation> Optional<T> getAnnotation(Class<T> annotationType) {

    return source.getAnnotation(annotationType);
  }

  public void emit(LocalEmitter emitter) throws IOException {

    emitter.emit(this);
  }

  public boolean isRamlScalarType() {
    return isScalar;
  }
}
