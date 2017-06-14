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
package org.raml.jaxrs.emitters;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import org.raml.api.RamlResourceMethod;
import org.raml.api.RamlSupportedAnnotation;
import org.raml.api.Annotable;
import org.raml.jaxrs.types.RamlProperty;
import org.raml.jaxrs.types.RamlType;
import org.raml.utilities.IndentedAppendable;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jean-Philippe Belanger on 3/29/17. Just potential zeroes and ones
 */
public class AnnotationInstanceEmitter implements LocalEmitter {

  private final IndentedAppendable writer;
  private final List<RamlSupportedAnnotation> supportedAnnotations;

  public AnnotationInstanceEmitter(IndentedAppendable writer, List<RamlSupportedAnnotation> supportedAnnotation) {
    this.writer = writer;
    this.supportedAnnotations = supportedAnnotation;
  }

  @Override
  public void emit(RamlType ramlType) throws IOException {

    annotate(ramlType);
  }

  @Override
  public void emit(RamlProperty ramlProperty) throws IOException {

    annotate(ramlProperty);
  }

  @Override
  public void emit(RamlResourceMethod method) throws IOException {

    annotate(method);
  }

  private void annotate(Annotable annotable) throws IOException {
    for (RamlSupportedAnnotation suportedAnnotation : supportedAnnotations) {

      Optional<Annotation> annotationOptional = suportedAnnotation.getAnnotationInstance(annotable);
      if (annotationOptional.isPresent() == false) {
        continue;
      }

      Annotation annotation = annotationOptional.get();

      if (annotation.annotationType().getDeclaredMethods().length == 0) {

        writer.appendLine("(" + annotation.annotationType().getSimpleName() + "):");
      } else {

        writer.appendLine("(" + annotation.annotationType().getSimpleName() + "):");
        writer.indent();
        try {
          for (Method method : annotation.annotationType().getDeclaredMethods()) {

            Object value = method.invoke(annotation);
            if (value.getClass().isArray()) {
              List<Object> list = new ArrayList<>();
              for (int i = 0; i < Array.getLength(value); i++) {
                list.add(Array.get(value, i));
              }

              String listString = Joiner.on(", ").join(FluentIterable.from(list).transform(new Function<Object, String>() {

                @Override
                public String apply(Object input) {
                  return toValue(input);
                }
              }));
              writer.appendLine(method.getName() + ": [" + listString + "]");
            } else {

              writer.appendLine(method.getName() + ": " + toValue(value));
            }
          }

          writer.outdent();
        } catch (Exception e) {
          throw new IOException("unable to write property", e);
        }
      }

    }
  }

  private String toValue(Object value) {

    if (Class.class.isAssignableFrom(value.getClass())) {

      return ((Class) value).getSimpleName();
    } else {

      return value.toString();
    }
  }
}
