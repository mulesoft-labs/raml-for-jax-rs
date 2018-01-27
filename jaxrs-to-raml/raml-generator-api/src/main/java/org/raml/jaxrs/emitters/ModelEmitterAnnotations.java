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
import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import org.raml.api.Annotable;
import org.raml.api.RamlSupportedAnnotation;
import org.raml.builder.AnnotableBuilder;
import org.raml.builder.AnnotationBuilder;
import org.raml.builder.PropertyValueBuilder;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created. There, you have it.
 */
public class ModelEmitterAnnotations {

  public static void annotate(Collection<RamlSupportedAnnotation> supportedAnnotations, Annotable annotable,
                              AnnotableBuilder annotableModel) throws IOException {
    for (RamlSupportedAnnotation suportedAnnotation : supportedAnnotations) {

      Optional<Annotation> annotationOptional = suportedAnnotation.getAnnotationInstance(annotable);
      if (!annotationOptional.isPresent()) {
        continue;
      }

      Annotation annotation = annotationOptional.get();

      AnnotationBuilder builder = AnnotationBuilder.annotation(annotation.annotationType().getSimpleName());

      if (annotation.annotationType().getDeclaredMethods().length > 0) {

        try {
          for (Method method : annotation.annotationType().getDeclaredMethods()) {

            Object value = method.invoke(annotation);
            if (value.getClass().isArray()) {
              List<Object> list = new ArrayList<>();
              for (int i = 0; i < Array.getLength(value); i++) {
                list.add(Array.get(value, i));
              }

              String[] listString = FluentIterable.from(list).transform(new Function<Object, String>() {

                @Override
                public String apply(Object input) {
                  return toValue(input);
                }
              }).toArray(String.class);

              builder.withProperties(PropertyValueBuilder.propertyOfArray(method.getName(), listString));
            } else {

              builder.withProperties(PropertyValueBuilder.property(method.getName(), toValue(value)));
            }
          }
        } catch (Exception e) {
          throw new IOException("unable to write property", e);
        }
      }

      annotableModel.withAnnotations(builder);

    }
  }

  private static String toValue(Object value) {

    if (Class.class.isAssignableFrom(value.getClass())) {

      return ((Class) value).getSimpleName();
    } else {

      return value.toString();
    }
  }
}
