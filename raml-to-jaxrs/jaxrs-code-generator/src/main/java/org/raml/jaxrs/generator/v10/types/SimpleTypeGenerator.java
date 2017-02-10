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
import com.squareup.javapoet.TypeSpec;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.builders.CodeContainer;
import org.raml.jaxrs.generator.builders.JavaPoetTypeGenerator;
import org.raml.jaxrs.generator.builders.BuildPhase;
import org.raml.jaxrs.generator.builders.extensions.types.TypeContextImpl;
import org.raml.jaxrs.generator.extension.types.TypeExtension;
import org.raml.jaxrs.generator.v10.V10GType;
import org.raml.jaxrs.generator.v10.V10TypeRegistry;

import java.io.IOException;

/**
 * Created by Jean-Philippe Belanger on 1/29/17. Just potential zeroes and ones
 */
class SimpleTypeGenerator implements JavaPoetTypeGenerator {

  private final V10GType originalType;
  private final V10TypeRegistry registry;
  private final CurrentBuild currentBuild;

  public SimpleTypeGenerator(V10GType originalType, V10TypeRegistry registry, CurrentBuild currentBuild) {
    this.originalType = originalType;
    this.registry = registry;
    this.currentBuild = currentBuild;
  }

  @Override
  public void output(CodeContainer<TypeSpec.Builder> rootDirectory, BuildPhase buildPhase) throws IOException {

    TypeExtension typeExtension = new SimpleInheritanceExtension(originalType, registry, currentBuild);
    rootDirectory.into(typeExtension.onType(new SimpleTypeContextImpl(currentBuild, this), null, originalType, buildPhase));
  }

  @Override
  public TypeName getGeneratedJavaType() {
    return originalType.defaultJavaTypeName(currentBuild.getModelPackage());
  }

  @Override
  public void output(CodeContainer<TypeSpec.Builder> rootDirectory) throws IOException {

    output(rootDirectory, null);
  }

  private static class SimpleTypeContextImpl extends TypeContextImpl {

    private final SimpleTypeGenerator objectType;

    public SimpleTypeContextImpl(CurrentBuild build, SimpleTypeGenerator objectType) {
      super(build);
      this.objectType = objectType;
    }


    @Override
    public void addImplementation() {
      getBuild().newImplementation(objectType);
    }

    @Override
    public void createInternalClass(JavaPoetTypeGenerator internalGenerator) {
      getBuild().internalClass(objectType, internalGenerator);
    }
  }
}
