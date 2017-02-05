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

import org.raml.jaxrs.generator.ramltypes.GType;

import java.util.Map;


/**
 * Created by Jean-Philippe Belanger on 12/10/16. Just potential zeroes and ones
 */
public class TypeFindingListener implements GFinderListener {

  private final Map<String, GeneratorType> foundTypes;

  public TypeFindingListener(Map<String, GeneratorType> foundTypes) {
    this.foundTypes = foundTypes;
  }

  @Override
  public void newTypeDeclaration(GType typeDeclaration) {

    GeneratorType generator = GeneratorType.generatorFrom(typeDeclaration);
    foundTypes.put(typeDeclaration.name(), generator);
  }


}
