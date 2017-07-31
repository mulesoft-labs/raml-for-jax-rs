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

import javax.lang.model.element.Modifier;

/**
 * Created. There, you have it.
 */
public interface TypeCopyHandler {

  TypeSpec.Builder newType(TypeSpec type);

  boolean handleAnnotations(TypeSpec.Builder newType, AnnotationSpec annotation);

  boolean handleInitializerBlock(TypeSpec.Builder newType, CodeBlock initializerBlock);

  boolean handleStaticBlock(TypeSpec.Builder newType, CodeBlock staticBlock);

  boolean handleSuperclass(TypeSpec.Builder newType, TypeName superclass);

  boolean handleJavadoc(TypeSpec.Builder newType, CodeBlock javadoc);

  boolean handleEnumConstant(TypeSpec.Builder newType, String key, TypeSpec value);

  boolean handleTypeSpec(TypeSpec.Builder newType, TypeSpec typeSpec);

  boolean handleMethod(TypeSpec.Builder newType, MethodSpec methodSpec);

  boolean handleField(TypeSpec.Builder newType, FieldSpec fieldSpec);

  boolean handleSuperInterface(TypeSpec.Builder newType, TypeName typeVariable);

  boolean handleTypeVariable(TypeSpec.Builder newType, TypeVariableName typeVariable);

  boolean handleModifier(TypeSpec.Builder newType, Modifier modifier);
}
