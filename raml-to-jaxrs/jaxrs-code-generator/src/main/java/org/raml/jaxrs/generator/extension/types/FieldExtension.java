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

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeSpec;
import org.raml.jaxrs.generator.builders.BuildPhase;
import org.raml.jaxrs.generator.extension.AbstractCompositeExtension;
import org.raml.jaxrs.generator.v10.V10GProperty;
import org.raml.jaxrs.generator.v10.V10GType;

import java.util.List;

/**
 * Created by Jean-Philippe Belanger on 2/9/17. Just potential zeroes and ones
 */
public interface FieldExtension {

  class Composite extends AbstractCompositeExtension<FieldExtension, FieldSpec.Builder> implements FieldExtension {

    public Composite(List<FieldExtension> extensions) {
      super(extensions);
    }

    @Override
    public FieldSpec.Builder onField(final TypeContext context, final TypeSpec.Builder typeSpec, FieldSpec.Builder fieldSpec,
                                     final V10GType containingType,
                                     final V10GProperty property, final BuildPhase buildPhase, final FieldType fieldType) {

      return runList(fieldSpec, new ElementJob<FieldExtension, FieldSpec.Builder>() {

        @Override
        public FieldSpec.Builder doElement(FieldExtension e, FieldSpec.Builder builder) {

          return e.onField(context, typeSpec, builder, containingType, property, buildPhase, fieldType);
        }
      });
    }
  }

  FieldSpec.Builder onField(TypeContext context, TypeSpec.Builder typeSpec, FieldSpec.Builder fieldSpec,
                            V10GType containingType, V10GProperty property,
                            BuildPhase buildPhase, FieldType fieldType);
}
