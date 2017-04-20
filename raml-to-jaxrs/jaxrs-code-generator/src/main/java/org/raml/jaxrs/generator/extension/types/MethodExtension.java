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

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import org.raml.jaxrs.generator.builders.BuildPhase;
import org.raml.jaxrs.generator.extension.AbstractCompositeExtension;
import org.raml.jaxrs.generator.v10.V10GProperty;
import org.raml.jaxrs.generator.v10.V10GType;

import java.util.List;

/**
 * Created by Jean-Philippe Belanger on 2/9/17. Just potential zeroes and ones
 */
public interface MethodExtension {

  class Composite extends AbstractCompositeExtension<MethodExtension, MethodSpec.Builder> implements MethodExtension {

    public Composite(List<MethodExtension> extensions) {
      super(extensions);
    }

    @Override
    public MethodSpec.Builder onMethod(final TypeContext context, final TypeSpec.Builder typeSpec, MethodSpec.Builder methodSpec,
                                       final List<ParameterSpec.Builder> parameters, final V10GType containingType,
                                       final V10GProperty property, final BuildPhase buildPhase,
                                       final MethodType methodType) {

      return runList(methodSpec, new ElementJob<MethodExtension, MethodSpec.Builder>() {

        @Override
        public MethodSpec.Builder doElement(MethodExtension e, MethodSpec.Builder builder) {

          return e.onMethod(context, typeSpec, builder, parameters, containingType, property, buildPhase, methodType);
        }
      });
    }
  }

  MethodSpec.Builder onMethod(TypeContext context, TypeSpec.Builder typeSpec, MethodSpec.Builder methodSpec,
                              List<ParameterSpec.Builder> parameters,
                              V10GType containingType, V10GProperty property,
                              BuildPhase buildPhase, MethodType methodType);
}
