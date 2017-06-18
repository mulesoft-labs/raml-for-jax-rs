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
import org.raml.jaxrs.generator.ramltypes.GType;
import org.raml.jaxrs.generator.v10.CreationModel;
import org.raml.jaxrs.generator.v10.NullXMLFacetInfo;
import org.raml.jaxrs.generator.v10.V10GProperty;
import org.raml.jaxrs.generator.v10.V10GType;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.XMLFacetInfo;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by Jean-Philippe Belanger on 1/5/17. Just potential zeroes and ones
 */
public abstract class V10GTypeHelper implements V10GType {


  private final String name;
  private final TypeDeclaration typeDeclaration;
  private final CreationModel creationModel;

  public V10GTypeHelper(String name, TypeDeclaration typeDeclaration) {
    this(name, typeDeclaration, CreationModel.NEVER_INLINE);
  }

  public V10GTypeHelper(String name, TypeDeclaration typeDeclaration, CreationModel creationModel) {
    this.name = name;
    this.typeDeclaration = typeDeclaration;
    this.creationModel = creationModel;
  }

  @Override
  public boolean isJson() {
    return false;
  }

  @Override
  public boolean isUnion() {
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
  public boolean isObject() {
    return false;
  }

  @Override
  public String schema() {
    return null;
  }

  @Override
  public List<V10GType> parentTypes() {
    return Collections.emptyList();
  }

  @Override
  public List<V10GProperty> properties() {
    return Collections.emptyList();
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
  public String discriminator() {
    return null;
  }

  @Override
  public String discriminatorValue() {
    return null;
  }

  @Override
  public boolean isEnum() {
    return false;
  }

  @Override
  public List<String> enumValues() {
    return Collections.emptyList();
  }

  @Override
  public boolean isInline() {
    return creationModel.isInline(typeDeclaration);
  }

  @Override
  public Collection<V10GType> childClasses(String typeName) {
    return Collections.emptyList();
  }

  @Override
  public void construct(CurrentBuild currentBuild, GObjectType objectType) {

  }

  @Override
  public void setJavaType(TypeName generatedJavaType) {

  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;

    if (!(o instanceof V10GType)) {

      return false;
    }

    V10GType v10GType = (V10GType) o;

    return name.equals(v10GType.name());
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }

  @Override
  public XMLFacetInfo xml() {
    if (implementation().xml() == null) {
      return new NullXMLFacetInfo();
    }
    return implementation().xml();
  }
}
