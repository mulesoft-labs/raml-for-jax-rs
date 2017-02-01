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
package org.raml.jaxrs.raml.core;

import org.raml.jaxrs.converter.RamlConfiguration;
import org.raml.jaxrs.converter.model.JaxRsRamlMediaType;
import org.raml.api.RamlMediaType;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;


public class DefaultRamlConfiguration implements RamlConfiguration {

  private final String application;

  private DefaultRamlConfiguration(String application) {
    this.application = application;
  }

  public static DefaultRamlConfiguration forApplication(String application) {
    checkNotNull(application);
    checkArgument(!application.trim().isEmpty(),
                  "application name should contain at least one meaningful character");

    return new DefaultRamlConfiguration(application.trim());
  }

  @Override
  public String getTitle() {
    return "Raml API generated from " + application;
  }

  @Override
  public String getBaseUri() {
    return "http://www.baseuri.com";
  }

  @Override
  public String getVersion() {
    return "1.0";
  }

  @Override
  public RamlMediaType getDefaultMediaType() {
    return JaxRsRamlMediaType.create(javax.ws.rs.core.MediaType.WILDCARD_TYPE);
  }
}
