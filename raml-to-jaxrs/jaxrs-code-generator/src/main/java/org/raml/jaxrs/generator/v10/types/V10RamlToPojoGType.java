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

import com.squareup.javapoet.TypeName;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.GObjectType;
import org.raml.jaxrs.generator.SchemaTypeFactory;
import org.raml.jaxrs.generator.ramltypes.GType;
import org.raml.jaxrs.generator.v10.V10GType;
import org.raml.v2.api.model.v10.datamodel.*;

/**
 * Created. There, you have it.
 */
public class V10RamlToPojoGType implements V10GType {

  private final TypeDeclaration typeDeclaration;
  private final String name;
  private TypeName typeName;

  public V10RamlToPojoGType(TypeDeclaration typeDeclaration) {
    this.name = typeDeclaration.name();
    this.typeDeclaration = typeDeclaration;
  }

  public V10RamlToPojoGType(String name, TypeDeclaration typeDeclaration) {
    // this is wrong. TODO fix.
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
  public boolean isJson() {
    return false;
  }

  @Override
  public boolean isXml() {
    return false;
  }

  @Override
  public boolean isScalar() {
    return false;
  }

  @Override
  public String schema() {
    return null;
  }

  @Override
  public boolean isArray() {
    return typeDeclaration instanceof ArrayTypeDeclaration;
  }

  @Override
  public GType arrayContents() {
    return new V10RamlToPojoGType(((ArrayTypeDeclaration) typeDeclaration).items());
  }

  @Override
  public TypeName defaultJavaTypeName(String pack) {
    return typeName;
  }

  @Override
  public boolean isEnum() {
    return (typeDeclaration instanceof StringTypeDeclaration) && ((StringTypeDeclaration) typeDeclaration).enumValues() != null;
  }

  @Override
  public void construct(final CurrentBuild currentBuild, GObjectType objectType) {
    objectType.dispatch(new GObjectType.GObjectTypeDispatcher() {

      @Override
      public void onPlainObject() {

        SchemaTypeFactory.createRamlToPojo(currentBuild, V10RamlToPojoGType.this);
      }

      @Override
      public void onEnumeration() {
        SchemaTypeFactory.createRamlToPojo(currentBuild, V10RamlToPojoGType.this);
      }

      @Override
      public void onUnion() {
        SchemaTypeFactory.createRamlToPojo(currentBuild, V10RamlToPojoGType.this);
      }
    });

  }


  @Override
  public void setJavaType(TypeName generatedJavaType) {

    this.typeName = generatedJavaType;
  }
}
