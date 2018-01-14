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
package org.raml.jaxrs.parser;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import org.raml.jaxrs.model.JaxRsSupportedAnnotation;
import org.raml.jaxrs.parser.model.JerseyJaxRsSupportedAnnotation;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * Created by Jean-Philippe Belanger on 4/1/17. Just potential zeroes and ones
 */
public class Utilities {

  public static Set<JaxRsSupportedAnnotation> getSupportedAnnotations(Set<Class<? extends Annotation>> translatedAnnotations) {
    return FluentIterable
        .from(translatedAnnotations).transform(new Function<Class<? extends Annotation>, JaxRsSupportedAnnotation>() {

          @Nullable
          @Override
          public JaxRsSupportedAnnotation apply(@Nullable Class<? extends Annotation> input) {
            return JerseyJaxRsSupportedAnnotation.createSupportedAnnotation(input);
          }
        }).toSet();
  }

}
