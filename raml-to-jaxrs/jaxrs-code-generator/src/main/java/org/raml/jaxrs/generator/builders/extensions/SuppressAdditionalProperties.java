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
package org.raml.jaxrs.generator.builders.extensions;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import org.raml.jaxrs.generator.builders.BuildPhase;
import org.raml.jaxrs.generator.extension.resources.ResourceContext;
import org.raml.jaxrs.generator.extension.types.MethodExtension;
import org.raml.jaxrs.generator.extension.types.MethodType;
import org.raml.jaxrs.generator.extension.types.TypeContext;
import org.raml.jaxrs.generator.extension.types.TypeExtension;
import org.raml.jaxrs.generator.v10.V10GMethod;
import org.raml.jaxrs.generator.v10.V10GProperty;
import org.raml.jaxrs.generator.v10.V10GResponse;
import org.raml.jaxrs.generator.v10.V10GType;

import java.util.List;

/**
 * Created. There, you have it.
 */
public class SuppressAdditionalProperties implements TypeExtension {

  @Override
  public TypeSpec.Builder onType(TypeContext context, TypeSpec.Builder builder, V10GType type, BuildPhase btype) {

    TypeCopier copier = new TypeCopier(new DefaultTypeCopyHandler() {

      @Override
      public boolean handleMethod(TypeSpec.Builder newType, MethodSpec methodSpec) {
        if (methodSpec.name.equals("setAdditionalProperties") || methodSpec.name.equals("getAdditionalProperties")) {
          return true;
        } else {

          return super.handleMethod(newType, methodSpec);
        }
      }
    });

    return copier.copy(builder, "");
  }
}
