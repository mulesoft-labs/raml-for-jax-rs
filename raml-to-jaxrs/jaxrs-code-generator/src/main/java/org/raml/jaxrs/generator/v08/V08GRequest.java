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
package org.raml.jaxrs.generator.v08;

import org.raml.jaxrs.generator.ramltypes.GRequest;
import org.raml.jaxrs.generator.ramltypes.GType;
import org.raml.v2.api.model.v08.bodies.BodyLike;

import java.util.Set;

/**
 * Created by Jean-Philippe Belanger on 12/11/16. Just potential zeroes and ones
 */
public class V08GRequest implements GRequest {

  private final BodyLike input;
  private final V08GType type;

  public V08GRequest(V08GResource v08GResource, V08Method v08Method, BodyLike input,
                     Set<String> globalSchemas, V08TypeRegistry registry) {
    this.input = input;
    if (input.schema() == null) {

      type = null;
      return;
    }

    if (globalSchemas.contains(input.schema().value())) {

      V08GType t = registry.fetchType(input.schema().value());
      if (t == null) {
        this.type = new V08GType(input.schema().value());
        registry.addType(type);
      } else {
        this.type = t;
      }
    } else {
      // lets be stupid.

      V08GType t = new V08GType(v08GResource.implementation(), v08Method.implementation(), input);
      V08GType check = registry.fetchType(t.name());
      if (check != null) {

        this.type = check;
      } else {
        this.type = t;
        registry.addType(t);
      }
    }
  }

  @Override
  public BodyLike implementation() {
    return input;
  }

  @Override
  public String mediaType() {
    return input.name();
  }

  @Override
  public GType type() {
    return type;
  }
}
