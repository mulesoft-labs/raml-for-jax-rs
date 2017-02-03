/*
 * Copyright ${licenseYear} (c) MuleSoft, Inc.
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
package org.raml.jaxrs.generator.v10;

import org.raml.jaxrs.generator.ramltypes.GParameter;
import org.raml.jaxrs.generator.ramltypes.GType;
import org.raml.v2.api.model.v10.datamodel.JSONTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.XMLTypeDeclaration;

/**
 * Created by Jean-Philippe Belanger on 12/10/16. Just potential zeroes and ones
 */
class V10PGParameter implements GParameter {

  private final TypeDeclaration input;
  private final V10GType type;

  public V10PGParameter(V10TypeRegistry registry, TypeDeclaration input) {

    this.input = input;
    this.type = registry.fetchType(input.type(), input);
  }

  @Override
  public String name() {
    return input.name();
  }

  @Override
  public boolean isComposite() {
    return input instanceof ObjectTypeDeclaration || input instanceof XMLTypeDeclaration
        || input instanceof JSONTypeDeclaration;
  }

  @Override
  public GType type() {

    return type;
  }

  @Override
  public TypeDeclaration implementation() {

    return input;
  }
}
