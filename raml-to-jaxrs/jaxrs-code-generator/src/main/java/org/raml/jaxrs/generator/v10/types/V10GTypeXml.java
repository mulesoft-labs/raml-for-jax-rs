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
import com.squareup.javapoet.TypeName;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.GObjectType;
import org.raml.jaxrs.generator.SchemaTypeFactory;
import org.raml.jaxrs.generator.v10.CreationModel;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.XMLTypeDeclaration;

/**
 * Created by Jean-Philippe Belanger on 1/3/17. Just potential zeroes and ones
 */
public class V10GTypeXml extends V10GTypeHelper {

  private final XMLTypeDeclaration typeDeclaration;
  private final String name;
  private final String defaultJavatypeName;

  private TypeName modelSpecifiedJavaType;

  V10GTypeXml(XMLTypeDeclaration typeDeclaration, String realName, String defaultJavatypeName, CreationModel model) {
    super(realName, typeDeclaration, model);
    this.typeDeclaration = typeDeclaration;
    this.name = realName;
    this.defaultJavatypeName = defaultJavatypeName;
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
  public boolean isXml() {

    return true;
  }

  @Override
  public String schema() {

    return typeDeclaration.schemaContent();
  }


  @Override
  public TypeName defaultJavaTypeName(String pack) {

    if (modelSpecifiedJavaType != null) {

      return modelSpecifiedJavaType;
    }

    if (isInline()) {
      return ClassName.get("", defaultJavatypeName);
    } else {
      return ClassName.get(pack, defaultJavatypeName);
    }
  }

  @Override
  public void setJavaType(TypeName generatedJavaType) {

    this.modelSpecifiedJavaType = generatedJavaType;
  }

  @Override
  public String toString() {
    return "V10GTypeXml{" + "input=" + typeDeclaration.name() + ":" + typeDeclaration.type()
        + ", name='" + name() + '\'' + '}';
  }


  @Override
  public void construct(final CurrentBuild currentBuild, GObjectType objectType) {
    objectType.dispatch(new GObjectType.GObjectTypeDispatcher() {

      @Override
      public void onXmlObject() {

        SchemaTypeFactory.createXmlType(currentBuild, V10GTypeXml.this);
      }
    });
  }


}
