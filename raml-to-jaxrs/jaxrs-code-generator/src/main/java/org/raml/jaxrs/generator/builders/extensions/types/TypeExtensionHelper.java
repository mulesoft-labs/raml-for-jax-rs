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
import org.raml.jaxrs.generator.extension.types.FieldExtension;
import org.raml.jaxrs.generator.extension.types.FieldType;
import org.raml.jaxrs.generator.extension.types.LegacyTypeExtension;
import org.raml.jaxrs.generator.extension.types.MethodExtension;
import org.raml.jaxrs.generator.extension.types.MethodType;
import org.raml.jaxrs.generator.extension.types.PredefinedFieldType;
import org.raml.jaxrs.generator.extension.types.PredefinedMethodType;
import org.raml.jaxrs.generator.extension.types.TypeContext;
import org.raml.jaxrs.generator.extension.types.TypeExtension;
import org.raml.jaxrs.generator.v10.V10GProperty;
import org.raml.jaxrs.generator.v10.V10GType;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import java.util.List;

/**
 * Created by Jean-Philippe Belanger on 12/4/16. Just potential zeroes and ones
 */
public class TypeExtensionHelper implements LegacyTypeExtension, TypeExtension, MethodExtension, FieldExtension {

  @Override
  public void onTypeImplementation(CurrentBuild currentBuild, TypeSpec.Builder typeSpec, TypeDeclaration typeDeclaration) {

  }

  @Override
  public void onFieldImplementation(CurrentBuild currentBuild, FieldSpec.Builder typeSpec, TypeDeclaration typeDeclaration) {

  }

  @Override
  public void onGetterMethodImplementation(CurrentBuild currentBuild, MethodSpec.Builder typeSpec,
                                           TypeDeclaration typeDeclaration) {

  }

  @Override
  public void onSetterMethodImplementation(CurrentBuild currentBuild, MethodSpec.Builder typeSpec,
                                           ParameterSpec.Builder param,
                                           TypeDeclaration typeDeclaration) {

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
                                        ParameterSpec.Builder param,
                                        TypeDeclaration typeDeclaration) {

  }

  @Override
  public void onEnumConstant(CurrentBuild currentBuild, TypeSpec.Builder builder, TypeDeclaration typeDeclaration,
                             String name) {

  }

  @Override
  public void onEnumerationClass(CurrentBuild currentBuild, TypeSpec.Builder builder, TypeDeclaration typeDeclaration) {

  }

  @Override
  public void onEnumField(CurrentBuild currentBuild, FieldSpec.Builder field, TypeDeclaration typeDeclaration) {

  }

  @Override
  public void onUnionType(CurrentBuild currentBuild, TypeSpec.Builder builder, V10GType typeDeclaration) {

  }

  @Override
  public TypeSpec.Builder onType(TypeContext context, TypeSpec.Builder builder, V10GType type, BuildPhase buildPhase) {

    TypeContextImpl c = (TypeContextImpl) context;
    if (type.isUnion()) {

      onUnionType(c.getBuildContext(), builder, type);
    } else {

      if (buildPhase == BuildPhase.INTERFACE) {
        this.onTypeDeclaration(c.getBuildContext(), builder, type);
      } else {
        this.onTypeImplementation(c.getBuildContext(), builder, type.implementation());
      }
    }


    return builder;
  }

  @Override
  public FieldSpec.Builder onField(TypeContext context, FieldSpec.Builder builder, V10GType containingType,
                                   V10GProperty property, BuildPhase buildPhase, FieldType methodType) {

    if (methodType == PredefinedFieldType.PROPERTY && buildPhase == BuildPhase.IMPLEMENTATION) {

      TypeContextImpl c = (TypeContextImpl) context;
      this.onFieldImplementation(c.getBuildContext(), builder, property.implementation());
    }

    return builder;
  }


  @Override
  public MethodSpec.Builder onMethod(TypeContext context, MethodSpec.Builder builder,
                                     List<ParameterSpec.Builder> parameters, V10GType containingType, V10GProperty property,
                                     BuildPhase buildPhase,
                                     MethodType methodType) {

    TypeContextImpl c = (TypeContextImpl) context;
    if (methodType == PredefinedMethodType.GETTER) {

      if (buildPhase == BuildPhase.INTERFACE) {
        this.onGetterMethodDeclaration(c.getBuildContext(), builder, property.implementation());
      } else {
        this.onGetterMethodImplementation(c.getBuildContext(), builder, property.implementation());
      }
    }

    if (methodType == PredefinedMethodType.SETTER) {

      if (buildPhase == BuildPhase.INTERFACE) {
        this.onSetterMethodDeclaration(c.getBuildContext(), builder, parameters.get(0), property.implementation());
      } else {
        this.onSetterMethodImplementation(c.getBuildContext(), builder, parameters.get(0), property.implementation());
      }
    }

    return builder;
  }
}
