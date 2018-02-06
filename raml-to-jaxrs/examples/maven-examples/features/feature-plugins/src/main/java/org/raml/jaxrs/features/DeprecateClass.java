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
package org.raml.jaxrs.features;

/**
 * Created. There, you have it.
 */

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.TypeSpec;
import org.raml.ramltopojo.EventType;
import org.raml.ramltopojo.extensions.AllTypesPluginHelper;
import org.raml.ramltopojo.extensions.EnumerationPluginContext;
import org.raml.ramltopojo.extensions.ObjectPluginContext;
import org.raml.ramltopojo.extensions.UnionPluginContext;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.StringTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.UnionTypeDeclaration;

/**
 * Created by Jean-Philippe Belanger on 3/7/17. Just potential zeroes and ones
 */
public class DeprecateClass extends AllTypesPluginHelper {


  @Override
  public TypeSpec.Builder classCreated(ObjectPluginContext objectPluginContext, ObjectTypeDeclaration ramlType,
                                       TypeSpec.Builder incoming, EventType eventType) {
    return incoming.addAnnotation(AnnotationSpec.builder(Deprecated.class).build());
  }

  @Override
  public TypeSpec.Builder classCreated(UnionPluginContext unionPluginContext, UnionTypeDeclaration ramlType,
                                       TypeSpec.Builder incoming, EventType eventType) {
    return incoming.addAnnotation(AnnotationSpec.builder(Deprecated.class).build());
  }

  @Override
  public TypeSpec.Builder classCreated(EnumerationPluginContext enumerationPluginContext, StringTypeDeclaration ramlType,
                                       TypeSpec.Builder incoming, EventType eventType) {
    return incoming.addAnnotation(AnnotationSpec.builder(Deprecated.class).build());
  }
}
