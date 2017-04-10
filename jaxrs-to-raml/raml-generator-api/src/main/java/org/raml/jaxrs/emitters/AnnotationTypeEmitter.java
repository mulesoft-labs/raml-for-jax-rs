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

import com.google.common.base.Optional;
import org.raml.api.RamlSupportedAnnotation;
import org.raml.api.ScalarType;
import org.raml.utilities.IndentedAppendable;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by Jean-Philippe Belanger on 3/29/17. Just potential zeroes and ones
 */
public class AnnotationTypeEmitter {

  private final IndentedAppendable writer;
  private final List<RamlSupportedAnnotation> suportedAnnotations;


  public AnnotationTypeEmitter(IndentedAppendable writer, List<RamlSupportedAnnotation> supportedAnnotation) {

    this.writer = writer;
    this.suportedAnnotations = supportedAnnotation;
  }

  public void emitAnnotations() throws IOException {

    writer.appendLine("annotationTypes:");
    writer.indent();
    for (RamlSupportedAnnotation ramlSupportedAnnotation : suportedAnnotations) {
      Class<? extends Annotation> javaAnnotation = ramlSupportedAnnotation.getAnnotation();

      if (javaAnnotation.getDeclaredMethods().length > 0) {
        writer.appendLine(javaAnnotation.getSimpleName() + ":");
        writer.indent();
        writer.appendLine("properties:");
        writer.indent();
        for (Method method : javaAnnotation.getDeclaredMethods()) {

          if (method.getReturnType().isArray()) {
            writer.appendLine(method.getName() + ": " + calculateRamlType(method.getReturnType().getComponentType()) + "[]");
          } else {
            writer.appendLine(method.getName() + ": " + calculateRamlType(method.getReturnType()));
          }
        }
        writer.outdent();
        writer.outdent();
      } else {

        writer.appendLine(javaAnnotation.getSimpleName() + ": nil");
      }
    }

    writer.outdent();
  }

  public String calculateRamlType(Class<?> type) throws IOException {

    if (Class.class.equals(type)) {

      return "string";
    }
    Optional<ScalarType> scalarType = ScalarType.fromType(type);
    if (scalarType.isPresent()) {

      return scalarType.get().getRamlSyntax();
    }

    throw new IOException("invalid type for annotation: " + type);
  }


}
