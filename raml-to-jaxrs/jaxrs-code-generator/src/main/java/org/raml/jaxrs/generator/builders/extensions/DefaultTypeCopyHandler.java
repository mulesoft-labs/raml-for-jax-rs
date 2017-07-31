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

import com.squareup.javapoet.*;
import org.raml.jaxrs.generator.GenerationException;

import javax.lang.model.element.Modifier;

/**
 * Created. There, you have it.
 */
public class DefaultTypeCopyHandler implements TypeCopyHandler {

  public TypeSpec.Builder newType(TypeSpec type) {

    TypeSpec.Builder newType;
    switch (type.kind) {

      case ANNOTATION:
        newType = TypeSpec.annotationBuilder(type.name);
        break;

      case ENUM:
        newType = TypeSpec.enumBuilder(type.name);
        break;

      case INTERFACE:
        newType = TypeSpec.interfaceBuilder(type.name);
        break;

      case CLASS:
        newType = TypeSpec.classBuilder(type.name);
        break;

      default:
        throw new GenerationException("this can't happen...");
    }

    return newType;
  }

  @Override
  public boolean handleAnnotations(TypeSpec.Builder newType, AnnotationSpec annotation) {
    newType.addAnnotation(annotation);
    return true;
  }

  @Override
  public boolean handleInitializerBlock(TypeSpec.Builder newType, CodeBlock initializerBlock) {
    newType.addInitializerBlock(initializerBlock);
    return true;
  }

  @Override
  public boolean handleStaticBlock(TypeSpec.Builder newType, CodeBlock staticBlock) {

    newType.addStaticBlock(staticBlock);
    return true;
  }

  @Override
  public boolean handleSuperclass(TypeSpec.Builder newType, TypeName superclass) {
    newType.superclass(superclass);
    return true;
  }

  @Override
  public boolean handleJavadoc(TypeSpec.Builder newType, CodeBlock javadoc) {
    newType.addJavadoc("$L", javadoc);
    return true;
  }

  @Override
  public boolean handleEnumConstant(TypeSpec.Builder newType, String key, TypeSpec value) {
    newType.addEnumConstant(key, value);
    return true;
  }

  @Override
  public boolean handleTypeSpec(TypeSpec.Builder newType, TypeSpec typeSpec) {
    newType.addType(typeSpec);
    return true;
  }

  @Override
  public boolean handleMethod(TypeSpec.Builder newType, MethodSpec methodSpec) {
    newType.addMethod(methodSpec);
    return true;
  }

  @Override
  public boolean handleField(TypeSpec.Builder newType, FieldSpec fieldSpec) {
    newType.addField(fieldSpec);
    return true;
  }

  @Override
  public boolean handleSuperInterface(TypeSpec.Builder newType, TypeName typeVariable) {
    newType.addSuperinterface(typeVariable);
    return true;
  }

  @Override
  public boolean handleTypeVariable(TypeSpec.Builder newType, TypeVariableName typeVariable) {
    newType.addTypeVariable(typeVariable);
    return true;
  }

  @Override
  public boolean handleModifier(TypeSpec.Builder newType, Modifier modifier) {
    newType.addModifiers(modifier);
    return true;
  }
}
