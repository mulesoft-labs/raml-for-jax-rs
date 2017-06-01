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

import com.google.common.base.Optional;
import org.glassfish.jersey.server.model.Parameter;
import org.raml.jaxrs.model.JaxRsEntity;
import org.raml.utilities.types.Cast;
import org.raml.jaxrs.parser.source.SourceParser;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Created by Jean-Philippe Belanger on 3/25/17. Just potential zeroes and ones
 */
public class JerseyJaxRsEntity implements JaxRsEntity {

  private final Type input;
  private final SourceParser sourceParser;

  public JerseyJaxRsEntity(Type input, SourceParser sourceParser) {

    this.input = input;
    this.sourceParser = sourceParser;
  }

  @Override
  public Type getType() {

    return input;
  }

  @Override
  public Optional<String> getDescription() {
    return sourceParser.getDocumentationFor(input);
  }

  @Override
  public JaxRsEntity createJaxRsEntity(Type type) {
    return new JerseyJaxRsEntity(type, sourceParser);
  }

  static JerseyJaxRsEntity create(Parameter input, SourceParser sourceParser) {

    return new JerseyJaxRsEntity(input.getType(), sourceParser);
  }

  static Optional<JaxRsEntity> create(Type input, SourceParser sourceParser) {

    if (input == null) {
      return Optional.absent();
    } else {

      return Optional.<JaxRsEntity>of(new JerseyJaxRsEntity(input, sourceParser));
    }
  }

  @Override
  public <T extends Annotation> Optional<T> getAnnotation(Class<T> annotationType) {

    Class castClass = Cast.toClass(input);

    return (Optional<T>) Optional.fromNullable(castClass.getAnnotation(annotationType));
  }
}
