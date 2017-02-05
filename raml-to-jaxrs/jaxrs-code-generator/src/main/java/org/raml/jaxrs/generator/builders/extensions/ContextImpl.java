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
package org.raml.jaxrs.generator.builders.extensions;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.GenerationException;
import org.raml.jaxrs.generator.extension.Context;

import javax.lang.model.element.Modifier;
import java.util.Map;

/**
 * Created by Jean-Philippe Belanger on 1/15/17. Just potential zeroes and ones
 */
public class ContextImpl implements Context {

  private final CurrentBuild build;

  protected ContextImpl(CurrentBuild build) {

    this.build = build;
  }

  protected CurrentBuild getBuild() {
    return build;
  }

  @Override
  public String getResourcePackage() {
    return build.getResourcePackage();
  }

  @Override
  public String getModelPackage() {
    return build.getModelPackage();
  }

  @Override
  public String getSupportPackage() {
    return build.getSupportPackage();
  }

  @Override
  public MethodSpec.Builder rename(MethodSpec.Builder builder, String name) {

    MethodSpec method = builder.build();

    if (method.isConstructor()) {
      throw new GenerationException("Can't rename a constructor: " + name);
    }

    MethodSpec.Builder newBuilder = MethodSpec.methodBuilder(name);

    for (AnnotationSpec annotation : method.annotations) {
      newBuilder.addAnnotation(annotation);
    }

    for (Modifier modifier : method.modifiers) {
      newBuilder.addModifiers(modifier);
    }

    for (TypeVariableName typeVariable : method.typeVariables) {
      newBuilder.addTypeVariable(typeVariable);
    }

    for (ParameterSpec parameter : method.parameters) {
      newBuilder.addParameter(parameter);
    }

    for (TypeName exception : method.exceptions) {
      newBuilder.addException(exception);
    }

    if (method.code != null) {
      newBuilder.addCode(method.code);
    }

    if (method.defaultValue != null) {
      newBuilder.defaultValue(method.defaultValue);
    }

    if (method.returnType != null) {
      newBuilder.returns(method.returnType);
    }

    if (method.javadoc != null) {
      newBuilder.addJavadoc("$L", method.javadoc);
    }

    newBuilder.varargs(method.varargs);

    return newBuilder;
  }

  @Override
  public TypeSpec.Builder rename(TypeSpec.Builder builder, String name) {

    TypeSpec type = builder.build();
    TypeSpec.Builder newType;
    switch (type.kind) {

      case ANNOTATION:
        newType = TypeSpec.annotationBuilder(name);
        break;

      case ENUM:
        newType = TypeSpec.enumBuilder(name);
        break;

      case INTERFACE:
        newType = TypeSpec.interfaceBuilder(name);
        break;

      case CLASS:
        newType = TypeSpec.classBuilder(name);
        break;

      default:
        throw new GenerationException("this can't happen...");
    }

    for (AnnotationSpec annotation : type.annotations) {
      newType.addAnnotation(annotation);
    }

    for (Modifier modifier : type.modifiers) {
      newType.addModifiers(modifier);
    }

    for (TypeVariableName typeVariable : type.typeVariables) {
      newType.addTypeVariable(typeVariable);
    }

    for (TypeName typeVariable : type.superinterfaces) {
      newType.addSuperinterface(typeVariable);
    }

    for (FieldSpec fieldSpec : type.fieldSpecs) {
      newType.addField(fieldSpec);
    }

    for (MethodSpec methodSpec : type.methodSpecs) {
      newType.addMethod(methodSpec);
    }

    for (TypeSpec typeSpec : type.typeSpecs) {
      newType.addType(typeSpec);
    }

    for (Map.Entry<String, TypeSpec> enumConstant : type.enumConstants.entrySet()) {
      newType.addEnumConstant(enumConstant.getKey(), enumConstant.getValue());
    }

    if (type.javadoc != null) {
      newType.addJavadoc("$L", type.javadoc);
    }

    if (type.superclass != null) {

      newType.superclass(type.superclass);
    }

    if (type.staticBlock != null && !type.staticBlock.isEmpty()) {

      newType.addStaticBlock(type.staticBlock);
    }

    if (type.initializerBlock != null && !type.initializerBlock.isEmpty()) {

      newType.addInitializerBlock(type.initializerBlock);
    }

    return newType;
  }

  public CurrentBuild getBuildContext() {

    return getBuild();
  }
}
