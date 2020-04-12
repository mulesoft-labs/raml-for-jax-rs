/*
 * Copyright 2013-2018 (c) MuleSoft, Inc.
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

import amf.client.model.domain.SchemaShape;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.GObjectType;
import org.raml.jaxrs.generator.SchemaTypeFactory;
import org.raml.jaxrs.generator.v10.CreationModel;

/**
 * Created by Jean-Philippe Belanger on 1/3/17. Just potential zeroes and ones
 */
public class V10GTypeJson extends V10GTypeHelper {

  private final SchemaShape schemaShape;
  private final String name;
  private final String defaultJavatypeName;
  private TypeName modelSpecifiedJavaType;

  V10GTypeJson(SchemaShape schemaShape, String realName, String defaultJavatypeName, CreationModel model) {
    super(realName, schemaShape, model);
    this.schemaShape = schemaShape;
    this.name = realName;
    this.defaultJavatypeName = defaultJavatypeName;
  }

  @Override
  public String type() {
    return schemaShape.name().value();
  }

  @Override
  public String name() {

    return name;
  }

  @Override
  public boolean isJson() {

    return true;
  }

  @Override
  public String schema() {

    return schemaShape.toJsonSchema();
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
  public String toString() {
    return "V10GTypeJson{" + "input=" + schemaShape.name() + ":" + schemaShape.name()
        + ", name='" + name() + '\'' + '}';
  }


  @Override
  public void construct(final CurrentBuild currentBuild, GObjectType objectType) {
    objectType.dispatch(new GObjectType.GObjectTypeDispatcher() {

      @Override
      public void onSchema() {

        SchemaTypeFactory.createJsonType(currentBuild, V10GTypeJson.this);
      }
    });
  }

  @Override
  public void setJavaType(TypeName generatedJavaType) {

    this.modelSpecifiedJavaType = generatedJavaType;
  }


}
