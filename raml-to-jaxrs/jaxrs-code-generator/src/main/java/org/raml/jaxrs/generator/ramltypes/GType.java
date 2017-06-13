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
package org.raml.jaxrs.generator.ramltypes;

import com.squareup.javapoet.TypeName;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.GAbstraction;
import org.raml.jaxrs.generator.GObjectType;

import java.util.List;

/**
 * Created by Jean-Philippe Belanger on 12/10/16. Just potential zeroes and ones
 */
public interface GType extends GAbstraction {

  String type();

  String name();

  TypeName defaultJavaTypeName(String pack);

  boolean isJson();

  boolean isXml();

  boolean isObject();

  boolean isArray();

  boolean isEnum();

  boolean isUnion();

  boolean isScalar();

  List<String> enumValues();

  String schema();

  GType arrayContents();

  void construct(CurrentBuild currentBuild, GObjectType objectType);

  void setJavaType(TypeName generatedJavaType);
}
