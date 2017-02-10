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

import org.raml.api.RamlMediaType;

import static com.google.common.base.Preconditions.checkNotNull;

public class JaxRsRamlMediaType implements RamlMediaType {

  private final javax.ws.rs.core.MediaType mediaType;

  private JaxRsRamlMediaType(javax.ws.rs.core.MediaType mediaType) {
    this.mediaType = mediaType;
  }

  public static JaxRsRamlMediaType create(javax.ws.rs.core.MediaType mediaType) {
    checkNotNull(mediaType);

    return new JaxRsRamlMediaType(mediaType);
  }

  @Override
  public String toStringRepresentation() {
    return mediaType.toString();
  }

  @Override
  public String toString() {
    return toStringRepresentation();
  }
}
