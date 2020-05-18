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
package org.raml.jaxrs.generator.builders.resources;

import amf.client.model.domain.SchemaShape;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.ramltopojo.TypeBasedOperation;

import java.util.Optional;

/**
 * Created. There, you have it.
 */
// todo this is deprecated.
public class DefaultJavaTypeOperation extends TypeBasedOperation.Default<Optional<TypeName>> {

  private final CurrentBuild build;

  private DefaultJavaTypeOperation(CurrentBuild build) {
    super(build::fetchTypeName);
    this.build = build;
  }

  public static DefaultJavaTypeOperation defaultJavaType(CurrentBuild build) {
    return new DefaultJavaTypeOperation(build);
  }

  @Override
  public Optional<TypeName> on(SchemaShape schemaShape) {

    return build.fetchTypeName(schemaShape);
  }
}
