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
package org.raml.jaxrs.generator;

import org.raml.jaxrs.generator.builders.BuildPhase;
import org.raml.jaxrs.generator.ramltypes.GType;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.UnionTypeDeclaration;

import static org.raml.jaxrs.generator.GObjectType.ENUMERATION_TYPE;
import static org.raml.jaxrs.generator.GObjectType.JSON_OBJECT_TYPE;
import static org.raml.jaxrs.generator.GObjectType.PLAIN_OBJECT_TYPE;
import static org.raml.jaxrs.generator.GObjectType.SCALAR;
import static org.raml.jaxrs.generator.GObjectType.UNION_TYPE;
import static org.raml.jaxrs.generator.GObjectType.XML_OBJECT_TYPE;

/**
 * Created by Jean-Philippe Belanger on 12/7/16. Just potential zeroes and ones
 */
public class GeneratorType {

  private final GObjectType objectType;
  private final GType declaredType;

  public static GeneratorType generatorFrom(GType typeDeclaration) {

    // Just a plain type we found.
    if (typeDeclaration.isJson()) {
      return new GeneratorType(JSON_OBJECT_TYPE, typeDeclaration);
    }

    if (typeDeclaration.implementation() instanceof UnionTypeDeclaration) {

      return new GeneratorType(UNION_TYPE, typeDeclaration);
    }

    if (typeDeclaration.isXml()) {

      return new GeneratorType(XML_OBJECT_TYPE, typeDeclaration);
    }

    if (typeDeclaration.implementation() instanceof ObjectTypeDeclaration) {

      return new GeneratorType(PLAIN_OBJECT_TYPE, typeDeclaration);
    }

    if (typeDeclaration.isEnum()) {

      return new GeneratorType(ENUMERATION_TYPE, typeDeclaration);
    }


    return new GeneratorType(SCALAR, typeDeclaration);
  }

  public GeneratorType(GObjectType objectType, GType declaredType) {

    this.objectType = objectType;
    this.declaredType = declaredType;
  }

  public GObjectType getObjectType() {
    return objectType;
  }

  public void construct(CurrentBuild currentBuild) {

    if (getObjectType() != GObjectType.SCALAR) {

      declaredType.construct(currentBuild, objectType);
    }

  }
}
