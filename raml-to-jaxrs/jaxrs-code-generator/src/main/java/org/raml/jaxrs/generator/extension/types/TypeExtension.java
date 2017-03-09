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
package org.raml.jaxrs.generator.extension.types;

import com.squareup.javapoet.TypeSpec;
import org.raml.jaxrs.generator.builders.BuildPhase;
import org.raml.jaxrs.generator.extension.AbstractCompositeExtension;
import org.raml.jaxrs.generator.v10.V10GType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jean-Philippe Belanger on 1/26/17. Just potential zeroes and ones
 */
public interface TypeExtension {

  class TypeExtensionComposite extends AbstractCompositeExtension<TypeExtension, TypeSpec.Builder> implements TypeExtension {

    public TypeExtensionComposite(List<TypeExtension> extensions) {
      super(extensions);
    }

    @Override
    public TypeSpec.Builder onType(final TypeContext context, TypeSpec.Builder builder, final V10GType type,
                                   final BuildPhase btype) {

      return runList(builder, new ElementJob<TypeExtension, TypeSpec.Builder>() {

        @Override
        public TypeSpec.Builder doElement(TypeExtension e, TypeSpec.Builder builder) {
          return e.onType(context, builder, type, btype);
        }
      });
    }
  }

  TypeSpec.Builder onType(TypeContext context, TypeSpec.Builder builder, V10GType type, BuildPhase btype);
}
