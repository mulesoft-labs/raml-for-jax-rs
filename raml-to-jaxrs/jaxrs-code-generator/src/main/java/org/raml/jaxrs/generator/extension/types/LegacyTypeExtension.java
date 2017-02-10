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
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.v10.V10GType;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

/**
 * Created by Jean-Philippe Belanger on 11/30/16. Just potential zeroes and ones This interface is too big.
 */
public interface LegacyTypeExtension extends MethodExtension, FieldExtension, TypeExtension {

  void onTypeImplementation(CurrentBuild currentBuild, TypeSpec.Builder typeSpec, TypeDeclaration typeDeclaration);

  void onFieldImplementation(CurrentBuild currentBuild, FieldSpec.Builder typeSpec, TypeDeclaration typeDeclaration);

  void onGetterMethodImplementation(CurrentBuild currentBuild, MethodSpec.Builder typeSpec,
                                    TypeDeclaration typeDeclaration);

  void onSetterMethodImplementation(CurrentBuild currentBuild, MethodSpec.Builder typeSpec, ParameterSpec.Builder param,
                                    TypeDeclaration typeDeclaration);

  void onTypeDeclaration(CurrentBuild currentBuild, TypeSpec.Builder typeSpec, V10GType type);

  void onGetterMethodDeclaration(CurrentBuild currentBuild, MethodSpec.Builder typeSpec, TypeDeclaration typeDeclaration);

  void onSetterMethodDeclaration(CurrentBuild currentBuild, MethodSpec.Builder typeSpec, ParameterSpec.Builder param,
                                 TypeDeclaration typeDeclaration);

  void onEnumConstant(CurrentBuild currentBuild, TypeSpec.Builder builder, TypeDeclaration typeDeclaration, String name);

  void onEnumerationClass(CurrentBuild currentBuild, TypeSpec.Builder builder, TypeDeclaration typeDeclaration);

  void onEnumField(CurrentBuild currentBuild, FieldSpec.Builder field, TypeDeclaration typeDeclaration);

  void onUnionType(CurrentBuild currentBuild, TypeSpec.Builder builder, V10GType typeDeclaration);
}
