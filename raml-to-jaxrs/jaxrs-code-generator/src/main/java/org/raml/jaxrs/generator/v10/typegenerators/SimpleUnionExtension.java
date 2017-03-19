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
package org.raml.jaxrs.generator.v10.typegenerators;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.raml.jaxrs.generator.Names;
import org.raml.jaxrs.generator.builders.BuildPhase;
import org.raml.jaxrs.generator.extension.types.TypeContext;
import org.raml.jaxrs.generator.extension.types.UnionExtension;
import org.raml.jaxrs.generator.v10.Annotations;
import org.raml.jaxrs.generator.v10.V10GType;
import org.raml.jaxrs.generator.v10.V10TypeRegistry;
import org.raml.jaxrs.generator.v10.types.V10GTypeUnion;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.UnionTypeDeclaration;

import javax.lang.model.element.Modifier;

import static org.raml.jaxrs.generator.extension.types.PredefinedFieldType.UNION;

/**
 * Created by Jean-Philippe Belanger on 2/9/17. Just potential zeroes and ones
 */
class SimpleUnionExtension implements UnionExtension {

  private ClassName javaName;
  private V10TypeRegistry registry;

  public SimpleUnionExtension(ClassName javaName, V10TypeRegistry registry) {
    this.javaName = javaName;
    this.registry = registry;
  }

  @Override
  public TypeSpec.Builder onUnionType(TypeContext context, TypeSpec.Builder noBuilder, V10GTypeUnion currentType,
                                      BuildPhase btype) {
    UnionTypeDeclaration union = (UnionTypeDeclaration) currentType.implementation();

    TypeSpec.Builder typeSpec = TypeSpec.classBuilder(javaName).addModifiers(Modifier.PUBLIC);
    typeSpec = Annotations.ON_TYPE_CLASS_FINISH.get(currentType).onType(context, typeSpec, currentType, btype);
    context.onType(context, typeSpec, currentType, btype);

    FieldSpec.Builder anyType = FieldSpec.builder(Object.class, "anyType", Modifier.PRIVATE);
    anyType = context.onField(context, typeSpec, anyType, currentType, null, BuildPhase.INTERFACE, UNION);
    anyType =
        Annotations.ON_TYPE_FIELD_CREATION.get(currentType)
            .onField(context, typeSpec, anyType, currentType, null,
                     BuildPhase.INTERFACE, UNION);
    typeSpec.addField(anyType.build());
    typeSpec.addMethod(
        MethodSpec.constructorBuilder()
            .addModifiers(Modifier.PRIVATE).addStatement("this.anyType = null")
            .build());

    for (TypeDeclaration typeDeclaration : union.of()) {

      V10GType type = registry.fetchType(typeDeclaration);

      TypeName typeName = type.defaultJavaTypeName(context.getModelPackage());
      String fieldName = Names.methodName(typeDeclaration.name());
      typeSpec
          .addMethod(
                     MethodSpec.constructorBuilder()
                         .addParameter(ParameterSpec.builder(typeName, fieldName).build())
                         .addModifiers(Modifier.PUBLIC).addStatement("this.anyType = $L", fieldName)
                         .build())
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
                         .addModifiers(Modifier.PUBLIC)
                         .returns(TypeName.BOOLEAN).build()
          );
    }
    return typeSpec;
  }
}
