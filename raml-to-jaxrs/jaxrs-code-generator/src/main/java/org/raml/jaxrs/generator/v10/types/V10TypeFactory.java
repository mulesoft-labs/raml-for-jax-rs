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

import com.squareup.javapoet.ClassName;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.builders.JavaPoetTypeGenerator;
import org.raml.jaxrs.generator.builders.TypeGenerator;
import org.raml.jaxrs.generator.ramltypes.GType;
import org.raml.jaxrs.generator.v10.typegenerators.EnumerationGenerator;
import org.raml.jaxrs.generator.v10.typegenerators.UnionTypeGenerator;
import org.raml.jaxrs.generator.v10.V10GType;
import org.raml.jaxrs.generator.v10.V10TypeRegistry;
import org.raml.jaxrs.generator.v10.typegenerators.SimpleTypeGenerator;

/**
 * Created by Jean-Philippe Belanger on 12/30/16. Just potential zeroes and ones
 */
public class V10TypeFactory {

  public static TypeGenerator createObjectType(final V10TypeRegistry registry, final CurrentBuild currentBuild,
                                               final V10GType originalType, boolean publicType) {

    TypeGenerator generator = new SimpleTypeGenerator(originalType, registry, currentBuild);

    if (publicType) {
      currentBuild.newGenerator(originalType.name(), generator);
    }
    return generator;
  }

  public static TypeGenerator createEnumerationType(CurrentBuild currentBuild, GType type) {
    JavaPoetTypeGenerator generator =
        new EnumerationGenerator(
                                 currentBuild,
                                 ((V10GType) type).implementation(),
                                 (ClassName) type.defaultJavaTypeName(currentBuild.getModelPackage()),
                                 type.enumValues());

    currentBuild.newGenerator(type.name(), generator);
    return generator;
  }


  public static void createUnion(CurrentBuild currentBuild, V10TypeRegistry v10TypeRegistry, V10GType v10GType) {

    ClassName unionJavaName = (ClassName) v10GType.defaultJavaTypeName(currentBuild.getModelPackage());
    currentBuild.newGenerator(v10GType.name(), new UnionTypeGenerator(v10TypeRegistry, v10GType, unionJavaName, currentBuild));
  }

}
