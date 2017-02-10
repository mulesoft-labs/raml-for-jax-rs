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
package org.raml.jaxrs.generator.builders.extensions.types;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.builders.BuildPhase;
import org.raml.jaxrs.generator.builders.extensions.ContextImpl;
import org.raml.jaxrs.generator.extension.types.FieldType;
import org.raml.jaxrs.generator.extension.types.MethodType;
import org.raml.jaxrs.generator.extension.types.TypeContext;
import org.raml.jaxrs.generator.v10.V10GProperty;
import org.raml.jaxrs.generator.v10.V10GType;

import java.util.List;

/**
 * Created by Jean-Philippe Belanger on 1/29/17. Just potential zeroes and ones
 */
abstract public class TypeContextImpl extends ContextImpl implements TypeContext {


  public TypeContextImpl(CurrentBuild build) {
    super(build);
  }


  @Override
  public TypeSpec.Builder onType(TypeContext context, TypeSpec.Builder builder, V10GType type, BuildPhase phase) {

    return getBuildContext().withTypeListeners().onType(context, builder, type, phase);
  }

  @Override
  public FieldSpec.Builder onField(TypeContext context, FieldSpec.Builder builder, V10GType containingType,
                                   V10GProperty property, BuildPhase buildPhase, FieldType fieldType) {
    return getBuildContext().withTypeListeners().onField(context, builder, containingType, property, buildPhase, fieldType);
  }

  @Override
  public MethodSpec.Builder onMethod(TypeContext context, MethodSpec.Builder builder,
                                     List<ParameterSpec.Builder> parameters, V10GType containingType, V10GProperty property,
                                     BuildPhase buildPhase,
                                     MethodType methodType) {
    return getBuildContext().withTypeListeners().onMethod(context, builder, parameters, containingType, property, buildPhase,
                                                          methodType);
  }
}
