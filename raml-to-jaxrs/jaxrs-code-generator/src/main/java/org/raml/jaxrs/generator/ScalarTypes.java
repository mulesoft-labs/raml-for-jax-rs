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
package org.raml.jaxrs.generator;

import com.google.common.collect.ImmutableMap;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import org.raml.jaxrs.generator.ramltypes.GType;
import org.raml.jaxrs.generator.v10.Annotations;
import org.raml.jaxrs.generator.v10.V10GType;
import org.raml.v2.api.model.v10.datamodel.*;

import java.io.File;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * Created by Jean-Philippe Belanger on 11/4/16. Just potential zeroes and ones
 */
public class ScalarTypes {


  private static Map<Class, Class<?>> scalarToType = ImmutableMap.<Class, Class<?>>builder()
      .put(IntegerTypeDeclaration.class, int.class)
      .put(BooleanTypeDeclaration.class, boolean.class)
      .put(DateTimeOnlyTypeDeclaration.class, Date.class)
      .put(TimeOnlyTypeDeclaration.class, Date.class)
      .put(DateTimeTypeDeclaration.class, Date.class).put(DateTypeDeclaration.class, Date.class)
      .put(NumberTypeDeclaration.class, BigDecimal.class)
      .put(StringTypeDeclaration.class, String.class).put(FileTypeDeclaration.class, File.class)
      .put(AnyTypeDeclaration.class, Object.class)
      .build();

  private static Map<String, Class<?>> stringScalarToType = ImmutableMap
      .<String, Class<?>>builder().put("integer", int.class).put("boolean", boolean.class)
      .put("date-time", Date.class).put("date", Date.class).put("number", BigDecimal.class)
      .put("string", String.class).put("file", File.class).build();

  // cheating: I know I only have one table for floats and ints, but the parser
  // should prevent problems.
  private static Map<String, Class<?>> properType = ImmutableMap.<String, Class<?>>builder()
      .put("float", float.class).put("double", double.class).put("int8", byte.class)
      .put("int16", short.class).put("int32", int.class).put("int64", long.class)
      .put("int", int.class).build();

  private static Map<String, Class<?>> properTypeObject = ImmutableMap.<String, Class<?>>builder()
      .put("float", Float.class).put("double", Double.class).put("int8", Byte.class)
      .put("int16", Short.class).put("int32", Integer.class).put("int64", Long.class)
      .put("int", Integer.class).build();

  public static Class<?> scalarToJavaType(TypeDeclaration type) {

    if (type instanceof IntegerTypeDeclaration) {

      return properType(shouldUsePrimitiveType((NumberTypeDeclaration) type) ? int.class
          : Integer.class, (IntegerTypeDeclaration) type);
    }

    if (type instanceof NumberTypeDeclaration) {

      return properType(BigDecimal.class, (NumberTypeDeclaration) type);
    }

    if (type instanceof BooleanTypeDeclaration) {

      return shouldUsePrimitiveType((BooleanTypeDeclaration) type) ? boolean.class : Boolean.class;
    }

    return scalarToType.get(type.getClass().getInterfaces()[0]);
  }

  private static Class<?> properType(Class<?> defaultClass, NumberTypeDeclaration type) {

    if (type.format() == null) {

      return defaultClass;
    }

    if (shouldUsePrimitiveType(type)) {
      return properType.get(type.format());
    } else {

      return properTypeObject.get(type.format());
    }
  }

  private static boolean shouldUsePrimitiveType(NumberTypeDeclaration type) {

    Boolean shouldUse = Annotations.USE_PRIMITIVE_TYPE.get(null, type);
    if (shouldUse != null && shouldUse) {

      return true;
    } else {

      return type.required();
    }
  }

  private static boolean shouldUsePrimitiveType(BooleanTypeDeclaration type) {

    Boolean shouldUse = Annotations.USE_PRIMITIVE_TYPE.get(null, type);
    if (shouldUse != null && shouldUse) {

      return true;
    } else {

      return type.required();
    }
  }

  public static Class<?> scalarToJavaType(String name) {

    return stringScalarToType.get(name.toLowerCase());
  }


  public static boolean extendsScalarRamlType(TypeDeclaration typeDeclaration) {

    return scalarToJavaType(typeDeclaration.name()) != null;
  }

  public static TypeName classToTypeName(Class scalar) {
    if (scalar.isPrimitive()) {
      switch (scalar.getSimpleName()) {
        case "int":
          return TypeName.INT;

        case "boolean":
          return TypeName.BOOLEAN;

        case "double":
          return TypeName.DOUBLE;

        case "float":
          return TypeName.FLOAT;

        case "byte":
          return TypeName.BYTE;

        case "char":
          return TypeName.CHAR;

        case "short":
          return TypeName.SHORT;

        case "long":
          return TypeName.LONG;

        case "void":
          return TypeName.VOID; // ?

        default:
          throw new GenerationException("can't handle type: " + scalar);
      }
    } else {
      return ClassName.get(scalar);
    }
  }

}
