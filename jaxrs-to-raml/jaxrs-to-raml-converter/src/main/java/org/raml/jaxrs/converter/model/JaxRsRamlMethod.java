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

import com.google.common.base.Function;
import com.google.common.base.Optional;

import org.raml.api.RamlEntity;
import org.raml.api.RamlFormParameter;
import org.raml.api.RamlHeaderParameter;
import org.raml.api.RamlMultiFormDataParameter;
import org.raml.jaxrs.model.JaxRsEntity;
import org.raml.jaxrs.model.JaxRsMethod;
import org.raml.api.RamlMediaType;
import org.raml.api.RamlQueryParameter;
import org.raml.api.RamlResourceMethod;

import java.lang.annotation.Annotation;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class JaxRsRamlMethod implements RamlResourceMethod {

  private final JaxRsMethod resourceMethod;

  private JaxRsRamlMethod(JaxRsMethod resourceMethod) {
    this.resourceMethod = resourceMethod;
  }

  public static JaxRsRamlMethod create(JaxRsMethod resourceMethod) {
    checkNotNull(resourceMethod);

    return new JaxRsRamlMethod(resourceMethod);
  }

  @Override
  public String getHttpMethod() {
    return this.resourceMethod.getHttpVerb().getString().toLowerCase();
  }

  @Override
  public List<RamlMediaType> getConsumedMediaTypes() {
    return Utilities.toRamlMediaTypes(this.resourceMethod.getConsumedMediaTypes()).toList();
  }

  @Override
  public List<RamlMediaType> getProducedMediaTypes() {
    return Utilities.toRamlMediaTypes(this.resourceMethod.getProducedMediaTypes()).toList();
  }

  @Override
  public List<RamlQueryParameter> getQueryParameters() {
    return Utilities.toRamlQueryParameters(this.resourceMethod.getQueryParameters()).toList();
  }

  @Override
  public List<RamlHeaderParameter> getHeaderParameters() {
    return Utilities.toRamlHeaderParameters(this.resourceMethod.getHeaderParameters()).toList();
  }

  @Override
  public List<RamlFormParameter> getFormParameters() {
    return Utilities.toRamlFormParameters(this.resourceMethod.getFormParameters()).toList();
  }

  @Override
  public Optional<RamlEntity> getConsumedType() {

    return this.resourceMethod.getConsumedEntity().transform(new Function<JaxRsEntity, RamlEntity>() {

      @Override
      public RamlEntity apply(JaxRsEntity input) {
        return JaxRsRamlEntity.create(input);
      }
    });
  }

  @Override
  public List<RamlMultiFormDataParameter> getMultiFormDataParameter() {
    return Utilities.toRamlMultiPartFormDataParameters(this.resourceMethod.getMultiPartFormDataParameters()).toList();
  }

  @Override
  public Optional<RamlEntity> getProducedType() {

    return this.resourceMethod.getProducedEntity().transform(new Function<JaxRsEntity, RamlEntity>() {

      @Override
      public RamlEntity apply(JaxRsEntity input) {
        return JaxRsRamlEntity.create(input);
      }
    });
  }

  @Override
  public Optional<String> getDescription() {
    return this.resourceMethod.getDescription();
  }

  @Override
  public <T extends Annotation> Optional<T> getAnnotation(Class<T> annotationType) {
    return this.resourceMethod.getJavaAnnotation(annotationType);
  }

}
