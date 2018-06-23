/*
 * Copyright 2013-2018 (c) MuleSoft, Inc.
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
import org.raml.v2.api.model.v10.datamodel.*;

import java.io.File;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * Created by Jean-Philippe Belanger on 11/4/16. Just potential zeroes and ones
 */
public class ScalarTypes {

  private static Map<String, Class<?>> stringScalarToType = ImmutableMap
      .<String, Class<?>>builder().put("integer", int.class).put("boolean", boolean.class)
      .put("date-time", Date.class).put("date", Date.class).put("number", BigDecimal.class)
      .put("string", String.class).put("file", File.class).build();

  private static Class<?> scalarToJavaType(String name) {

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
