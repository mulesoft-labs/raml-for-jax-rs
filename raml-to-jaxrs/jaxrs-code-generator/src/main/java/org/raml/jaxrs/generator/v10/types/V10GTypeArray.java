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
package org.raml.jaxrs.generator.v10.types;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.GObjectType;
import org.raml.jaxrs.generator.builders.BuildPhase;
import org.raml.jaxrs.generator.ramltypes.GType;
import org.raml.jaxrs.generator.v10.CreationModel;
import org.raml.jaxrs.generator.v10.V10TypeRegistry;
import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import java.util.List;

/**
 * Created by Jean-Philippe Belanger on 1/5/17. Just potential zeroes and ones
 */
public class V10GTypeArray extends V10GTypeHelper {

  private final V10TypeRegistry registry;
  private final String name;
  private final ArrayTypeDeclaration typeDeclaration;

  public V10GTypeArray(V10TypeRegistry registry, String name, ArrayTypeDeclaration typeDeclaration, CreationModel model) {
    super(name, typeDeclaration, model);
    this.registry = registry;
    this.name = name;
    this.typeDeclaration = typeDeclaration;
  }

  @Override
  public TypeDeclaration implementation() {
    return typeDeclaration;
  }

  @Override
  public String type() {
    return typeDeclaration.type();
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public boolean isArray() {
    return true;
  }

  @Override
  public GType arrayContents() {
    return registry.fetchType(typeDeclaration.items().name(), typeDeclaration.items());
  }

  @Override
  public TypeName defaultJavaTypeName(String pack) {

    TypeName parameterType = arrayContents().defaultJavaTypeName(pack);
    if (parameterType.isPrimitive()) {

      parameterType = parameterType.box();
    }

    return ParameterizedTypeName.get(
                                     ClassName.get(List.class),
                                     parameterType);
  }

  @Override
  public ClassName javaImplementationName(String pack) {
    return null;
  }
}
