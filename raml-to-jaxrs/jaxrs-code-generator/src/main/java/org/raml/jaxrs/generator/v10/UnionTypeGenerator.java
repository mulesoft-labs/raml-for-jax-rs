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
import com.squareup.javapoet.TypeSpec;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.builders.BuildPhase;
import org.raml.jaxrs.generator.builders.CodeContainer;
import org.raml.jaxrs.generator.builders.JavaPoetTypeGenerator;
import org.raml.jaxrs.generator.builders.extensions.types.TypeContextImpl;
import org.raml.jaxrs.generator.extension.types.UnionExtension;
import org.raml.jaxrs.generator.v10.types.V10GTypeUnion;

import java.io.IOException;

/**
 * Created by Jean-Philippe Belanger on 1/1/17. Just potential zeroes and ones
 */
public class UnionTypeGenerator implements JavaPoetTypeGenerator {


  private final V10TypeRegistry registry;
  private final V10GType v10GType;
  private final ClassName javaName;
  private final CurrentBuild currentBuild;

  public UnionTypeGenerator(V10TypeRegistry registry, V10GType v10GType, ClassName javaName, CurrentBuild currentBuild) {

    this.registry = registry;
    this.v10GType = v10GType;
    this.javaName = javaName;
    this.currentBuild = currentBuild;
  }

  @Override
  public void output(CodeContainer<TypeSpec.Builder> rootDirectory) throws IOException {

    UnionExtension ux = new SimpleUnionExtension(javaName, registry);

    rootDirectory.into(ux.onUnionType(new UnionTypeContextImpl(currentBuild, this), null, (V10GTypeUnion) v10GType,
                                      BuildPhase.INTERFACE));
  }

  @Override
  public void output(CodeContainer<TypeSpec.Builder> rootDirectory, BuildPhase buildPhase) throws IOException {

    output(rootDirectory);
  }

  @Override
  public TypeName getGeneratedJavaType() {
    return javaName;
  }

  private static class UnionTypeContextImpl extends TypeContextImpl {

    private final UnionTypeGenerator objectType;

    public UnionTypeContextImpl(CurrentBuild build, UnionTypeGenerator objectType) {
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
