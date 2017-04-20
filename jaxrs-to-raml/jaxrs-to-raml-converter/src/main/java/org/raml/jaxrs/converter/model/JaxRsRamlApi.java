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

import org.raml.api.RamlSupportedAnnotation;
import org.raml.jaxrs.converter.RamlConfiguration;
import org.raml.jaxrs.model.JaxRsApplication;
import org.raml.api.RamlMediaType;
import org.raml.api.RamlApi;
import org.raml.api.RamlResource;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class JaxRsRamlApi implements RamlApi {

  private final RamlConfiguration configuration;
  private final JaxRsApplication application;

  private JaxRsRamlApi(RamlConfiguration configuration, JaxRsApplication application) {
    this.configuration = configuration;
    this.application = application;
  }

  public static JaxRsRamlApi create(RamlConfiguration configuration, JaxRsApplication application) {
    checkNotNull(configuration);
    checkNotNull(application);

    return new JaxRsRamlApi(configuration, application);
  }

  @Override
  public String getTitle() {
    return configuration.getTitle();
  }

  @Override
  public String getVersion() {
    return configuration.getVersion();
  }

  @Override
  public String getBaseUri() {
    return configuration.getBaseUri();
  }

  @Override
  public List<RamlResource> getResources() {
    return Utilities.toRamlResources(this.application.getResources()).toList();
  }

  @Override
  public RamlMediaType getDefaultMediaType() {
    return configuration.getDefaultMediaType();
  }

  @Override
  public List<RamlSupportedAnnotation> getSupportedAnnotation() {

    return Utilities.toRamlSupportedAnnotation(application.getSupportedAnnotations()).toList();
  }
}
