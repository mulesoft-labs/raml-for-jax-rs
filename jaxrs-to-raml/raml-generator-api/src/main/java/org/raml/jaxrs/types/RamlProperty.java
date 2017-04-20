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
import org.raml.jaxrs.common.Example;
import org.raml.jaxrs.emitters.AnnotationInstanceEmitter;
import org.raml.jaxrs.emitters.Emittable;
import org.raml.jaxrs.emitters.LocalEmitter;
import org.raml.utilities.IndentedAppendable;

import java.io.IOException;
import java.lang.annotation.Annotation;

/**
 * Created by Jean-Philippe Belanger on 3/26/17. Just potential zeroes and ones
 */
public class RamlProperty implements Emittable, Annotable {

  private final RamlType ramlType;
  private final String name;
  private final Annotable source;

  public RamlProperty(String name, RamlType ramlType, Annotable source) {
    this.ramlType = ramlType;
    this.name = name;
    this.source = source;
  }

  public String getName() {
    return name;
  }

  public static RamlProperty createProperty(Annotable source, String name, RamlType ramlType) {
    return new RamlProperty(name, ramlType, source);
  }

  public void write(AnnotationInstanceEmitter emitter, IndentedAppendable writer) throws IOException {

    writer.appendLine(name + ": ");
    writer.indent();
    writer.appendLine("type", ramlType.getTypeName());

    emitter.emit(this);

    writer.outdent();
  }

  public void writeExample(IndentedAppendable writer) throws IOException {

    if (!ramlType.isRamlScalarType()) {

      writer.appendLine(name + ":");
      writer.indent();

      ramlType.writeExample(writer);

      writer.outdent();
    } else {

      Optional<Example> e = source.getAnnotation(Example.class);
      if (!e.isPresent()) {

        return;
      }

      writer.appendLine(name + ": " + e.get().value());
    }

  }

  public <T extends Annotation> Optional<T> getAnnotation(Class<T> annotationType) {

    return source.getAnnotation(annotationType);
  }

  public void emit(LocalEmitter emitter) throws IOException {

    emitter.emit(this);
  }

  public RamlType getRamlType() {
    return ramlType;
  }
}
