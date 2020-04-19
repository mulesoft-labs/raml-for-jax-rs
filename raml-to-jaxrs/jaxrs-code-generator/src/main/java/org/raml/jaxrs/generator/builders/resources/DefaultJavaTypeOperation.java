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
import org.raml.jaxrs.generator.TypeBasedOperation;
import org.raml.ramltopojo.ExtraInformation;

import java.util.Optional;

/**
 * Created. There, you have it.
 */
public class DefaultJavaTypeOperation extends TypeBasedOperation.Default<Optional<TypeName>> {

  private final CurrentBuild build;
  private final String packageName;

  private DefaultJavaTypeOperation(CurrentBuild build, String packageName) {
    super((x) -> Optional.ofNullable(build.fetchTypeName(x)));
    this.build = build;
    this.packageName = packageName;
  }

  public static DefaultJavaTypeOperation defaultJavaType(CurrentBuild build, String packageName) {
    return new DefaultJavaTypeOperation(build, packageName);
  }

  @Override
  public Optional<TypeName> on(SchemaShape schemaShape) {
    if (ExtraInformation.isInline(schemaShape)) {
      return Optional.of(ClassName.get("", "SchemaShape"));
    } else {
      return Optional.of(ClassName.get(packageName, "SchemaShape"));
    }
  }
}
