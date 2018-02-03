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
package org.raml.jaxrs.plugins;

import org.raml.builder.TypeBuilder;
import org.raml.builder.TypeDeclarationBuilder;
import org.raml.builder.TypePropertyBuilder;
import org.raml.pojotoraml.ClassParser;
import org.raml.pojotoraml.Property;
import org.raml.pojotoraml.RamlAdjuster;

import java.lang.reflect.Type;
import java.util.List;
import java.util.UUID;

/**
 * Created. There, you have it.
 */
public class ChangeType extends RamlAdjuster.Helper {

  final private List<String> arguments;

  public ChangeType(List<String> arguments) {
    this.arguments = arguments;
  }


  @Override
  public String adjustTypeName(Class<?> type, String name, ClassParser parser) {

    if (arguments.get(0).equals(type.getName())) {

      return arguments.get(1);
    }

    return name;
  }

  @Override
  public String adjustEnumValue(Class<?> type, String name) {
    return super.adjustEnumValue(type, name);
  }

  @Override
  public TypeBuilder adjustType(Type type, TypeBuilder builder) {

    return TypeBuilder.type("string");
  }

  @Override
  public TypePropertyBuilder adjustScalarProperty(TypeDeclarationBuilder typeDeclaration, Property property,
                                                  TypePropertyBuilder typePropertyBuilder) {
    return typePropertyBuilder;
  }

  @Override
  public TypePropertyBuilder adjustComposedProperty(TypeDeclarationBuilder typeDeclaration, Property property,
                                                    TypePropertyBuilder typePropertyBuilder) {
    return typePropertyBuilder;
  }
}
