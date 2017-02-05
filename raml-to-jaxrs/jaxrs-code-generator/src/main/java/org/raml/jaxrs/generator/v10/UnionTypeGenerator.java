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
package org.raml.jaxrs.generator.v10;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.Names;
import org.raml.jaxrs.generator.builders.CodeContainer;
import org.raml.jaxrs.generator.builders.JavaPoetTypeGenerator;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.UnionTypeDeclaration;

import javax.lang.model.element.Modifier;
import java.io.IOException;

/**
 * Created by Jean-Philippe Belanger on 1/1/17. Just potential zeroes and ones
 */
public class UnionTypeGenerator implements JavaPoetTypeGenerator {


  private final V10TypeRegistry registry;
  private final V10GType v10GType;
  private final ClassName javaName;
  private final CurrentBuild currentBuild;

  public UnionTypeGenerator(V10TypeRegistry registry, V10GType v10GType, ClassName javaName,
                            CurrentBuild currentBuild) {

    this.registry = registry;
    this.v10GType = v10GType;
    this.javaName = javaName;
    this.currentBuild = currentBuild;
  }

  @Override
  public void output(CodeContainer<TypeSpec.Builder> rootDirectory) throws IOException {

    UnionTypeDeclaration union = (UnionTypeDeclaration) v10GType.implementation();

    TypeSpec.Builder builder = TypeSpec.classBuilder(javaName).addModifiers(Modifier.PUBLIC);
    builder
        .addField(FieldSpec.builder(Object.class, "anyType", Modifier.PRIVATE).build())
        .addMethod(
                   MethodSpec.constructorBuilder()
                       .addModifiers(Modifier.PUBLIC).addStatement("this.$L = null", "anyType").build());

    for (TypeDeclaration typeDeclaration : union.of()) {

      V10GType type = registry.fetchType(typeDeclaration);

      TypeName typeName = type.defaultJavaTypeName(currentBuild.getModelPackage());
      String fieldName = Names.methodName(typeDeclaration.name());
      builder
          .addMethod(
                     MethodSpec.constructorBuilder()
                         .addParameter(ParameterSpec.builder(typeName, fieldName).build())
                         .addModifiers(Modifier.PUBLIC).addStatement("this.anyType = $L", fieldName).build())
          .addMethod(
                     MethodSpec
                         .methodBuilder(Names.methodName("get", typeDeclaration.name()))
                         .addModifiers(Modifier.PUBLIC)
                         .returns(typeName)
                         .addStatement(
                                       "if ( !(anyType instanceof  $T)) throw new $T(\"fetching wrong type out of the union: $T\")",
                                       typeName, IllegalStateException.class, typeName)
                         .addStatement("return ($T) anyType", typeName).build())
          .addMethod(
                     MethodSpec.methodBuilder(Names.methodName("is", typeDeclaration.name()))
                         .addStatement("return anyType instanceof $T", typeName)
                         .addModifiers(Modifier.PUBLIC).returns(TypeName.BOOLEAN).build());
    }

    currentBuild.withTypeListeners().onUnionType(currentBuild, builder, v10GType);
    rootDirectory.into(builder);
  }

  @Override
  public void output(CodeContainer<TypeSpec.Builder> rootDirectory, TYPE type) throws IOException {

    output(rootDirectory);
  }

  @Override
  public TypeName getGeneratedJavaType() {
    return javaName;
  }
}
