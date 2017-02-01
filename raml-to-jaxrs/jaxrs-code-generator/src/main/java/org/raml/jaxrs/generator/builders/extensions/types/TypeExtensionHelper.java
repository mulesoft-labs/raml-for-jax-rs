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
import org.raml.jaxrs.generator.extension.types.TypeExtension;
import org.raml.jaxrs.generator.v10.V10GType;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

/**
 * Created by Jean-Philippe Belanger on 12/4/16. Just potential zeroes and ones
 */
public class TypeExtensionHelper implements TypeExtension {

  @Override
  public void onTypeImplementation(CurrentBuild currentBuild, TypeSpec.Builder typeSpec,
                                   TypeDeclaration typeDeclaration) {

  }

  @Override
  public void onFieldImplementation(CurrentBuild currentBuild, FieldSpec.Builder typeSpec,
                                    TypeDeclaration typeDeclaration) {

  }

  @Override
  public void onGetterMethodImplementation(CurrentBuild currentBuild, MethodSpec.Builder typeSpec,
                                           TypeDeclaration typeDeclaration) {

  }

  @Override
  public void onSetterMethodImplementation(CurrentBuild currentBuild, MethodSpec.Builder typeSpec,
                                           ParameterSpec.Builder param, TypeDeclaration typeDeclaration) {

  }

  @Override
  public void onTypeDeclaration(CurrentBuild currentBuild, TypeSpec.Builder typeSpec, V10GType type) {

  }

  @Override
  public void onGetterMethodDeclaration(CurrentBuild currentBuild, MethodSpec.Builder typeSpec,
                                        TypeDeclaration typeDeclaration) {

  }

  @Override
  public void onSetterMethodDeclaration(CurrentBuild currentBuild, MethodSpec.Builder typeSpec,
                                        ParameterSpec.Builder param, TypeDeclaration typeDeclaration) {

  }

  @Override
  public void onEnumConstant(CurrentBuild currentBuild, TypeSpec.Builder builder,
                             TypeDeclaration typeDeclaration, String name) {

  }

  @Override
  public void onEnumerationClass(CurrentBuild currentBuild, TypeSpec.Builder builder,
                                 TypeDeclaration typeDeclaration) {

  }

  @Override
  public void onEnumField(CurrentBuild currentBuild, FieldSpec.Builder field,
                          TypeDeclaration typeDeclaration) {

  }

  @Override
  public void onUnionType(CurrentBuild currentBuild, TypeSpec.Builder builder,
                          V10GType typeDeclaration) {

  }
}
