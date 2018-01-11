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
package org.raml.jaxrs.generator.v10;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.GObjectType;
import org.raml.jaxrs.generator.ramltypes.GType;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.XMLFacetInfo;

import java.util.List;

/**
 * Created by Jean-Philippe Belanger on 12/10/16. Just potential zeroes and ones
 */
public interface V10GType extends GType {

  TypeDeclaration implementation();

  @Override
  String type();

  @Override
  String name();

  @Override
  boolean isJson();

  @Override
  boolean isXml();

  boolean isScalar();

  @Override
  String schema();

  @Override
  boolean isArray();

  @Override
  GType arrayContents();

  @Override
  TypeName defaultJavaTypeName(String pack);


  @Override
  boolean isEnum();


  @Override
  void construct(final CurrentBuild currentBuild, GObjectType objectType);

}
