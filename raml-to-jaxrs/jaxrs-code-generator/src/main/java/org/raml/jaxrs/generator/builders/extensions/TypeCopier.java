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
import java.util.Map;

/**
 * Created. There, you have it.
 */
public class TypeCopier {

  private final TypeCopyHandler handler;

  public TypeCopier(TypeCopyHandler handler) {
    this.handler = handler;
  }

  public TypeSpec.Builder copy(TypeSpec.Builder builder, String name) {

    TypeSpec type = builder.build();
    TypeSpec.Builder newType = handler.newType(type);

    for (AnnotationSpec annotation : type.annotations) {
      handler.handleAnnotations(newType, annotation);
    }

    for (Modifier modifier : type.modifiers) {
      handler.handleModifier(newType, modifier);
    }

    for (TypeVariableName typeVariable : type.typeVariables) {
      handler.handleTypeVariable(newType, typeVariable);
    }

    for (TypeName typeVariable : type.superinterfaces) {
      handler.handleSuperInterface(newType, typeVariable);
    }

    for (FieldSpec fieldSpec : type.fieldSpecs) {
      handler.handleField(newType, fieldSpec);
    }

    for (MethodSpec methodSpec : type.methodSpecs) {
      handler.handleMethod(newType, methodSpec);
    }

    for (TypeSpec typeSpec : type.typeSpecs) {
      handler.handleTypeSpec(newType, typeSpec);
    }

    for (Map.Entry<String, TypeSpec> enumConstant : type.enumConstants.entrySet()) {
      handler.handleEnumConstant(newType, enumConstant.getKey(), enumConstant.getValue());
    }

    if (type.javadoc != null) {
      handler.handleJavadoc(newType, type.javadoc);
    }

    if (type.superclass != null) {

      handler.handleSuperclass(newType, type.superclass);
    }

    if (type.staticBlock != null && !type.staticBlock.isEmpty()) {

      handler.handleStaticBlock(newType, type.staticBlock);
    }

    if (type.initializerBlock != null && !type.initializerBlock.isEmpty()) {

      handler.handleInitializerBlock(newType, type.initializerBlock);
    }

    return newType;
  }
}
