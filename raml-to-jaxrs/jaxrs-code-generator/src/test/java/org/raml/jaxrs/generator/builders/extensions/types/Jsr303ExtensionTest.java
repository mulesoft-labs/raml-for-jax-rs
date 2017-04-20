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
package org.raml.jaxrs.generator.builders.extensions.types;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.NumberTypeDeclaration;

import javax.lang.model.element.Modifier;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.math.BigInteger;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Created by Jean-Philippe Belanger on 12/12/16. Just potential zeroes and ones
 */
public class Jsr303ExtensionTest {


  @Before
  public void annotations() {

    MockitoAnnotations.initMocks(this);
  }

  @Mock
  NumberTypeDeclaration number;

  @Mock
  ArrayTypeDeclaration array;

  @Test
  public void forInteger() throws Exception {

    setupNumberFacets();
    Jsr303Extension ext = new Jsr303Extension();
    FieldSpec.Builder builder =
        FieldSpec.builder(ClassName.get(Integer.class), "champ", Modifier.PUBLIC);

    ext.onFieldImplementation(null, builder, number);

    assertForIntegerNumber(builder);
  }


  @Test
  public void forBigInt() throws Exception {

    setupNumberFacets();
    Jsr303Extension ext = new Jsr303Extension();
    FieldSpec.Builder builder =
        FieldSpec.builder(ClassName.get(BigInteger.class), "champ", Modifier.PUBLIC);

    ext.onFieldImplementation(null, builder, number);

    assertForIntegerNumber(builder);
  }


  @Test
  public void forDouble() throws Exception {

    setupNumberFacets();
    Jsr303Extension ext = new Jsr303Extension();
    FieldSpec.Builder builder =
        FieldSpec.builder(ClassName.get(Double.class), "champ", Modifier.PUBLIC);

    ext.onFieldImplementation(null, builder, number);

    assertEquals(1, builder.build().annotations.size());
    assertEquals(NotNull.class.getName(), builder.build().annotations.get(0).type.toString());
  }

  @Test
  public void forArrays() throws Exception {

    when(array.minItems()).thenReturn(3);
    when(array.maxItems()).thenReturn(5);

    FieldSpec.Builder builder =
        FieldSpec.builder(ParameterizedTypeName.get(List.class, String.class), "champ",
                          Modifier.PUBLIC);
    Jsr303Extension ext = new Jsr303Extension();
    ext.onFieldImplementation(null, builder, array);
    assertEquals(1, builder.build().annotations.size());
    assertEquals(Size.class.getName(), builder.build().annotations.get(0).type.toString());
    assertEquals("3", builder.build().annotations.get(0).members.get("min").get(0).toString());
    assertEquals("5", builder.build().annotations.get(0).members.get("max").get(0).toString());
  }

  @Test
  public void forArraysMaxOnly() throws Exception {

    when(array.minItems()).thenReturn(null);
    when(array.maxItems()).thenReturn(5);

    FieldSpec.Builder builder =
        FieldSpec.builder(ParameterizedTypeName.get(List.class, String.class), "champ",
                          Modifier.PUBLIC);
    Jsr303Extension ext = new Jsr303Extension();
    ext.onFieldImplementation(null, builder, array);
    assertEquals(1, builder.build().annotations.size());
    assertEquals(Size.class.getName(), builder.build().annotations.get(0).type.toString());
    assertEquals(1, builder.build().annotations.get(0).members.size());
    assertEquals("5", builder.build().annotations.get(0).members.get("max").get(0).toString());
  }

  @Test
  public void forArraysNotNull() throws Exception {

    when(array.minItems()).thenReturn(null);
    when(array.maxItems()).thenReturn(null);
    when(array.required()).thenReturn(true);

    FieldSpec.Builder builder =
        FieldSpec.builder(ParameterizedTypeName.get(List.class, String.class), "champ",
                          Modifier.PUBLIC);
    Jsr303Extension ext = new Jsr303Extension();
    ext.onFieldImplementation(null, builder, array);
    assertEquals(1, builder.build().annotations.size());
    assertEquals(NotNull.class.getName(), builder.build().annotations.get(0).type.toString());
  }


  public void setupNumberFacets() {
    when(number.minimum()).thenReturn(13.0);
    when(number.maximum()).thenReturn(17.0);
    when(number.required()).thenReturn(true);
  }

  public void assertForIntegerNumber(FieldSpec.Builder builder) {

    assertEquals(3, builder.build().annotations.size());
    assertEquals(NotNull.class.getName(), builder.build().annotations.get(0).type.toString());
    assertEquals(Min.class.getName(), builder.build().annotations.get(1).type.toString());
    assertEquals("13", builder.build().annotations.get(1).members.get("value").get(0).toString());
    assertEquals(Max.class.getName(), builder.build().annotations.get(2).type.toString());
    assertEquals("17", builder.build().annotations.get(2).members.get("value").get(0).toString());
  }

}
