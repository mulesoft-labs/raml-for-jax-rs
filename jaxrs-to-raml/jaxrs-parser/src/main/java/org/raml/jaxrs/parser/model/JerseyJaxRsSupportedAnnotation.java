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
package org.raml.jaxrs.parser.model;

import org.raml.jaxrs.model.JaxRsSupportedAnnotation;

import java.lang.annotation.Annotation;

/**
 * Created by Jean-Philippe Belanger on 3/29/17. Just potential zeroes and ones
 */
public class JerseyJaxRsSupportedAnnotation implements JaxRsSupportedAnnotation {

  private final Class<? extends Annotation> annotation;

  JerseyJaxRsSupportedAnnotation(Class<? extends Annotation> annotation) {

    this.annotation = annotation;
  }

  public static JaxRsSupportedAnnotation createSupportedAnnotation(Class<? extends Annotation> annotation) {

    return new JerseyJaxRsSupportedAnnotation(annotation);
  }

  public Class<? extends Annotation> getJavaAnnotation() {
    return annotation;
  }
}
