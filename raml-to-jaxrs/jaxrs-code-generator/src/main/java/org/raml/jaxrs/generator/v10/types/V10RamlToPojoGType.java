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

import amf.client.model.domain.AnyShape;
import com.squareup.javapoet.TypeName;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.GObjectType;
import org.raml.jaxrs.generator.SchemaTypeFactory;
import org.raml.jaxrs.generator.v10.V10GType;

import java.util.function.Consumer;

/**
 * Created. There, you have it.
 */
public class V10RamlToPojoGType implements V10GType {

  private final AnyShape shape;
  private final String name;
  private TypeName typeName;

  public V10RamlToPojoGType(AnyShape shape) {
    this.name = shape.name().value();
    this.shape = shape;
  }

  public V10RamlToPojoGType(String name, AnyShape shape) {
    // this is wrong. TODO fix.
    this.name = name;
    this.shape = shape;
  }

  @Override
  public String id() {
    return shape.id();
  }

  @Override
  public AnyShape implementation() {
    return shape;
  }

  @Override
  public String type() {
    return shape.name().value();
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
  public String schema() {
    return null;
  }

  @Override
  public TypeName defaultJavaTypeName(String pack) {
    return typeName;
  }

  @Override
  public void construct(CurrentBuild currentBuild, Consumer<GObjectType.GObjectTypeDispatcher> objectType) {
    objectType.accept(new GObjectType.GObjectTypeDispatcher() {

      @Override
      public void onSchema() {
        SchemaTypeFactory.createRamlToPojo(currentBuild, V10RamlToPojoGType.this);
      }
    });
  }

  @Override
  public void setJavaType(TypeName generatedJavaType) {

    this.typeName = generatedJavaType;
  }

  @Override
  public int hashCode() {
    return shape.id().hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof V10RamlToPojoGType))
      return false;
    V10RamlToPojoGType that = (V10RamlToPojoGType) o;
    return shape.id().equals(that.shape.id());
  }
}
