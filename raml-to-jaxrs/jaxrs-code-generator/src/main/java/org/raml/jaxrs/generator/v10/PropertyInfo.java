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

import com.squareup.javapoet.TypeName;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.ramltypes.GProperty;
import org.raml.jaxrs.generator.ramltypes.GType;

/**
 * Created by Jean-Philippe Belanger on 11/13/16. Just potential zeroes and ones
 */
public class PropertyInfo {

  private final V10TypeRegistry registry;
  private final GProperty property;

  public PropertyInfo(V10TypeRegistry registry, GProperty property) {
    this.registry = registry;
    this.property = property;
  }

  public String getName() {
    return property.name();
  }


  public GType getType() {
    return property.type();
  }


  public TypeName resolve(CurrentBuild currentBuild) {

    return property.type().defaultJavaTypeName(currentBuild.getModelPackage());
  }
}
