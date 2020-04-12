/*
 * Copyright 2013-2018 (c) MuleSoft, Inc.
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

import amf.client.model.domain.*;
import org.raml.jaxrs.generator.ramltypes.GType;

import java.util.Optional;
import java.util.function.Consumer;

import static org.raml.jaxrs.generator.GObjectType.*;

/**
 * Created by Jean-Philippe Belanger on 12/7/16. Just potential zeroes and ones
 */
public class GeneratorType {

  private final Consumer<GObjectTypeDispatcher> objectType;
  private final GType declaredType;

  public static GeneratorType generatorFrom(GType type) {

    return TypeBasedOperation.run(type.implementation(), new TypeBasedOperation.OptionalDefault<GeneratorType>() {

      @Override
      public Optional<GeneratorType> on(AnyShape anyShape) {
        return Optional.of(new GeneratorType(GObjectTypeDispatcher::onRamlToPojo, type));
      }

      @Override
      public Optional<GeneratorType> on(NodeShape anyShape) {
        return Optional.of(new GeneratorType(GObjectTypeDispatcher::onRamlToPojo, type));
      }

      @Override
      public Optional<GeneratorType> on(ArrayShape anyShape) {
        return Optional.of(new GeneratorType(GObjectTypeDispatcher::onRamlToPojo, type));
      }

      @Override
      public Optional<GeneratorType> on(UnionShape anyShape) {
        return Optional.of(new GeneratorType(GObjectTypeDispatcher::onRamlToPojo, type));
      }

      @Override
      public Optional<GeneratorType> on(FileShape anyShape) {
        return Optional.of(new GeneratorType(GObjectTypeDispatcher::onRamlToPojo, type));
      }

      @Override
      public Optional<GeneratorType> on(ScalarShape anyShape) {
        return Optional.of(new GeneratorType(GObjectTypeDispatcher::onRamlToPojo, type));
      }

      @Override
      public Optional<GeneratorType> on(SchemaShape schemaShape) {
        return Optional.of(new GeneratorType(GObjectTypeDispatcher::onSchema, type));
      }

      @Override
      public Optional<GeneratorType> on(NilShape nilShape) {
        return Optional.of(new GeneratorType(GObjectTypeDispatcher::onRamlToPojo, type));
      }
    }).orElseThrow(() -> new GenerationException("can't create generator for " + type.implementation()));

  }

  public GeneratorType(Consumer<GObjectTypeDispatcher> objectType, GType declaredType) {

    this.objectType = objectType;
    this.declaredType = declaredType;
  }

  public void construct(CurrentBuild currentBuild) {

    //if (getObjectType() != GObjectType.SCALAR) {

      declaredType.construct(currentBuild, objectType);
    //}

  }
}
