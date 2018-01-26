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

import com.squareup.javapoet.*;
import joptsimple.internal.Strings;

import javax.lang.model.element.Modifier;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jean-Philippe Belanger on 11/13/16. Just potential zeroes and ones
 */
public class ResponseSupport {

  public static void buildSupportClasses(File rootDir, String defaultPackage) throws IOException {

    /*
     * static class Headers200 {
     * 
     * Map<String, String> map;
     * 
     * public Headers200 withLocation(String value) {
     * 
     * map.put("Location", value); return this; }
     * 
     * private void toResponse(Response.ResponseBuilder builder) { for (String s : map.keySet()) { if (map.get(s) != null ) {
     * builder.header(s, map.get(s)); } } } }
     */

    TypeSpec.Builder builder =
        TypeSpec
            .classBuilder(ClassName.get(defaultPackage, "ResponseDelegate"))
            .addModifiers(Modifier.PUBLIC)
            .superclass(Response.class)
            .addField(
                      FieldSpec.builder(Response.class, "delegate", Modifier.PRIVATE, Modifier.FINAL)
                          .build())
            .addField(
                      FieldSpec.builder(Object.class, "entity", Modifier.PRIVATE, Modifier.FINAL)
                          .build());;

    builder.addMethod(MethodSpec.constructorBuilder()
        .addParameter(ParameterSpec.builder(Response.class, "delegate").build())
        .addParameter(ParameterSpec.builder(Object.class, "entity").build())
        .addModifiers(Modifier.PROTECTED).addCode("this.delegate = delegate;\n").addCode("this.entity = entity;\n").build());

    builder.addMethod(MethodSpec.constructorBuilder()
        .addParameter(ParameterSpec.builder(Response.class, "delegate").build())
        .addModifiers(Modifier.PROTECTED)
        .addCode("this(delegate, null);\n").build());

    for (Method m : Response.class.getDeclaredMethods()) {

      if (java.lang.reflect.Modifier.isStatic(m.getModifiers())) {
        continue;
      }

      if (m.getName().equals("getEntity")) {

        MethodSpec.Builder methodBuilder =
            MethodSpec.methodBuilder("getEntity").addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(Object.class)
                .addCode("return this.entity;");

        builder.addMethod(methodBuilder.build());
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

    }

    TypeSpec.Builder headerBuilderBase =
        TypeSpec
            .classBuilder("HeaderBuilderBase")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .addField(
                      FieldSpec.builder(ParameterizedTypeName.get(Map.class, String.class, String.class), "headerMap",
                                        Modifier.PROTECTED, Modifier.FINAL).build())
            .addMethod(MethodSpec.constructorBuilder().addCode(CodeBlock.of("this.headerMap = new $T<>();\n", HashMap.class))
                .addModifiers(Modifier.PROTECTED)
                .build())
            .addMethod(
                       MethodSpec
                           .methodBuilder("toResponseBuilder")
                           .addModifiers(Modifier.PUBLIC)
                           .returns(ClassName.get(Response.ResponseBuilder.class))
                           .addParameter(ParameterSpec.builder(Response.ResponseBuilder.class, "builder", Modifier.FINAL).build())
                           .addCode(CodeBlock.builder()
                               .beginControlFlow("for (String s : headerMap.keySet()) ")
                               .beginControlFlow("if (headerMap.get(s) != null ) ")
                               .addStatement("builder.header(s, headerMap.get(s));")
                               .endControlFlow()
                               .endControlFlow()
                               .addStatement("return builder")
                               .build())
                           .build());



    builder.addType(headerBuilderBase.build());
    JavaFile.Builder file = JavaFile.builder(defaultPackage, builder.build()).skipJavaLangImports(true);
    file.build().writeTo(rootDir);

  }

  private static String buildParamList(Method m) {

    ArrayList<String> list = new ArrayList<String>();
    for (int i = 0; i < m.getParameterTypes().length; i++) {
      list.add("p" + i);
    }

    return Strings.join(list, ",");
  }


}
