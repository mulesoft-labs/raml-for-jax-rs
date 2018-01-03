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
package org.raml.jaxrs.generator.v08;

import com.google.common.collect.ImmutableMap;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.GObjectType;
import org.raml.jaxrs.generator.GenerationException;
import org.raml.jaxrs.generator.Names;
import org.raml.jaxrs.generator.ScalarTypes;
import org.raml.jaxrs.generator.SchemaTypeFactory;
import org.raml.jaxrs.generator.ramltypes.GType;
import org.raml.v2.api.model.v08.bodies.BodyLike;
import org.raml.v2.api.model.v08.bodies.Response;
import org.raml.v2.api.model.v08.methods.Method;
import org.raml.v2.api.model.v08.resources.Resource;

import java.io.File;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * Created by Jean-Philippe Belanger on 12/11/16. Just potential zeroes and ones
 */
public class V08GType implements GType {

  private static Map<String, Class<?>> stringScalarToType = ImmutableMap
      .<String, Class<?>>builder().put("integer", int.class).put("boolean", boolean.class)
      .put("date-time", Date.class).put("date", Date.class).put("number", BigDecimal.class)
      .put("string", String.class).put("file", File.class).build();


  private final String ramlName;
  private final String defaultJavaName;
  private final BodyLike typeDeclaration;
  private TypeName modelSpecifiedJavaType;

  public V08GType(Resource resource, Method method, BodyLike typeDeclaration) {

    this.ramlName = Names.ramlTypeName(resource, method, typeDeclaration);
    this.defaultJavaName = Names.javaTypeName(resource, method, typeDeclaration);
    this.typeDeclaration = typeDeclaration;
  }

  public V08GType(Resource resource, Method method, Response response, BodyLike typeDeclaration) {

    this.ramlName = Names.ramlTypeName(resource, method, response, typeDeclaration);
    this.defaultJavaName = Names.javaTypeName(resource, method, response, typeDeclaration);
    this.typeDeclaration = typeDeclaration;
  }

  public V08GType(String type) {
    this.ramlName = type;
    this.typeDeclaration = null;
    this.defaultJavaName = Names.typeName(type);
  }

  public V08GType(String type, BodyLike typeDeclaration) {
    this.ramlName = type;
    this.typeDeclaration = typeDeclaration;
    this.defaultJavaName = Names.typeName(type);
  }

  @Override
  public BodyLike implementation() {
    return typeDeclaration;
  }


  @Override
  public boolean isScalar() {
    return !isJson() && !isXml();
  }

  @Override
  public String type() {
    return ramlName;
  }

  @Override
  public String name() {
    return ramlName;
  }

  @Override
  public boolean isJson() {
    return typeDeclaration != null && typeDeclaration.name().equals("application/json");
  }

  @Override
  public boolean isXml() {
    return typeDeclaration != null && typeDeclaration.name().equals("application/xml");
  }

  @Override
  public String schema() {
    return typeDeclaration.schemaContent();
  }

  @Override
  public boolean isArray() {
    return false;
  }


  @Override
  public GType arrayContents() {
    return null;
  }

  @Override
  public TypeName defaultJavaTypeName(String pack) {

    if (modelSpecifiedJavaType != null) {
      return modelSpecifiedJavaType;
    }

    Class<?> type = scalarToJavaType(defaultJavaName);
    if (type == null) {
      return ClassName.get(pack, defaultJavaName);
    } else {

      return ScalarTypes.classToTypeName(type);
    }
  }

  public static Class<?> scalarToJavaType(String name) {

    return stringScalarToType.get(name.toLowerCase());
  }

  @Override
  public void setJavaType(TypeName generatedJavaType) {

    if (isXml()) {
      this.modelSpecifiedJavaType = generatedJavaType;
    }
  }

  @Override
  public boolean isEnum() {
    return false;
  }

  @Override
  public void construct(final CurrentBuild currentBuild, GObjectType objectType) {
    objectType.dispatch(new GObjectType.GObjectTypeDispatcher() {

      @Override
      public void onPlainObject() {
        throw new GenerationException("no plain objects in v08");
      }

      @Override
      public void onXmlObject() {

        SchemaTypeFactory.createXmlType(currentBuild, V08GType.this);
      }

      @Override
      public void onJsonObject() {

        SchemaTypeFactory.createJsonType(currentBuild, V08GType.this);
      }

      @Override
      public void onEnumeration() {

        throw new GenerationException("no enums objects in v08");
      }

      @Override
      public void onUnion() {

        throw new GenerationException("no union objects in v08");
      }

    });
  }


}
