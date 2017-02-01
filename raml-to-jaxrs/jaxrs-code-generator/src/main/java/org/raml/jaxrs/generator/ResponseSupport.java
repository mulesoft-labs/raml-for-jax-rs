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

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import joptsimple.internal.Strings;

import javax.lang.model.element.Modifier;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Jean-Philippe Belanger on 11/13/16. Just potential zeroes and ones
 */
public class ResponseSupport {

  public static void buildSupportClasses(File rootDir, String defaultPackage) throws IOException {

    TypeSpec.Builder builder =
        TypeSpec
            .classBuilder(ClassName.get(defaultPackage, "ResponseDelegate"))
            .addModifiers(Modifier.PUBLIC)
            .superclass(Response.class)
            .addField(
                      FieldSpec.builder(Response.class, "delegate", Modifier.PRIVATE, Modifier.FINAL)
                          .build());

    builder.addMethod(MethodSpec.constructorBuilder()
        .addParameter(ParameterSpec.builder(Response.class, "delegate").build())
        .addModifiers(Modifier.PROTECTED).addCode("this.delegate = delegate;\n").build());

    for (Method m : Response.class.getDeclaredMethods()) {

      if (java.lang.reflect.Modifier.isStatic(m.getModifiers())) {
        continue;
      }

      MethodSpec.Builder methodBuilder =
          MethodSpec.methodBuilder(m.getName()).addModifiers(Modifier.PUBLIC)
              .addAnnotation(Override.class);

      if (m.getGenericReturnType().toString().equals("T")) {
        methodBuilder.returns(TypeVariableName.get("<T> T"));
      } else {
        methodBuilder.returns(m.getGenericReturnType());
      }

      int p = 0;
      for (Type aClass : m.getGenericParameterTypes()) {
        methodBuilder.addParameter(ParameterSpec.builder(aClass, "p" + (p++)).build());
      }

      if (m.getReturnType() == void.class) {
        methodBuilder.addCode("this.delegate." + m.getName() + "(" + buildParamList(m) + ");\n");
      } else {
        methodBuilder.addCode("return this.delegate." + m.getName() + "(" + buildParamList(m)
            + ");\n");
      }

      builder.addMethod(methodBuilder.build());

      JavaFile.Builder file = JavaFile.builder(defaultPackage, builder.build());
      file.build().writeTo(rootDir);
    }
  }

  private static String buildParamList(Method m) {

    ArrayList<String> list = new ArrayList<String>();
    for (int i = 0; i < m.getParameterTypes().length; i++) {
      list.add("p" + i);
    }

    return Strings.join(list, ",");
  }


}
