/*
 * Copyright ${licenseYear} (c) MuleSoft, Inc.
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
import org.raml.jaxrs.generator.v10.Annotations;
import org.raml.jaxrs.generator.v10.V10GProperty;
import org.raml.jaxrs.generator.v10.V10GType;
import org.raml.jaxrs.generator.v10.V10TypeRegistry;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Jean-Philippe Belanger on 1/3/17. Just potential zeroes and ones
 */
public class V10GTypeObject extends V10GTypeHelper {

  private final V10TypeRegistry registry;
  private final TypeDeclaration typeDeclaration;
  private final String name;
  private final String defaultJavatypeName;
  private final boolean inline;
  private final List<V10GProperty> properties;
  private final List<V10GType> parentTypes;


  V10GTypeObject(V10TypeRegistry registry, TypeDeclaration typeDeclaration, String realName,
                 String defaultJavatypeName, boolean inline, List<V10GProperty> properties,
                 List<V10GType> parentTypes) {
    super(realName);
    this.registry = registry;
    this.typeDeclaration = typeDeclaration;
    this.name = realName;
    this.defaultJavatypeName = defaultJavatypeName;
    this.inline = inline;
    this.properties = properties;
    this.parentTypes = parentTypes;

    if (isObject() && !name.equals("object")) {

      registry.addChildToParent(parentTypes(), this);
    }
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
  public boolean isObject() {
    return typeDeclaration instanceof ObjectTypeDeclaration;
  }

  public List<V10GType> parentTypes() {
    return parentTypes;
  }

  public List<V10GProperty> properties() {

    return properties;
  }

  @Override
  public TypeName defaultJavaTypeName(String pack) {

    if (isInline()) {
      return ClassName.get("", defaultJavatypeName);
    } else {
      return ClassName.get(pack, defaultJavatypeName);
    }
  }

  public ClassName javaImplementationName(String pack) {

    if (isInline()) {

      return ClassName.get("",
                           Annotations.IMPLEMENTATION_CLASS_NAME.get(defaultJavatypeName + "Impl", typeDeclaration));
    } else {
      return ClassName.get(pack,
                           Annotations.IMPLEMENTATION_CLASS_NAME.get(defaultJavatypeName + "Impl", typeDeclaration));
    }
  }

  public boolean isInline() {
    return inline;
  }


  public Collection<V10GType> childClasses(String typeName) {

    return new HashSet<>(registry.getChildClasses().get(typeName));
  }


  @Override
  public String toString() {
    return "V10GType{" + "input=" + typeDeclaration.name() + ":" + typeDeclaration.type()
        + ", name='" + name() + '\'' + '}';
  }


  @Override
  public void construct(final CurrentBuild currentBuild, GObjectType objectType) {
    objectType.dispatch(new GObjectType.GObjectTypeDispatcher() {

      @Override
      public void onPlainObject() {

        V10TypeFactory.createObjectType(registry, currentBuild, V10GTypeObject.this, true);
      }
    });
  }


}
