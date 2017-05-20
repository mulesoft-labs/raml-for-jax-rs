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

import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.raml.v2.api.model.v10.datamodel.BooleanTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.IntegerTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.NumberTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeInstance;
import org.raml.v2.api.model.v10.declarations.AnnotationRef;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Created by Jean-Philippe Belanger on 12/17/16. Just potential zeroes and ones
 */
public class ScalarTypesTest {

  @Before
  public void mockito() {

    MockitoAnnotations.initMocks(this);

    when(javaTypeAnnotationRef.annotation()).thenReturn(javaTypeAnnotation);
    when(javaTypeAnnotation.name()).thenReturn("javaPrimitiveType");
    when(javaTypeAnnotationRef.structuredValue()).thenReturn(typeInstance);
    when(typeInstance.value()).thenReturn(false);
  }

  @Mock
  IntegerTypeDeclaration integer;

  @Mock
  NumberTypeDeclaration number;

  @Mock
  BooleanTypeDeclaration bool;


  @Mock
  AnnotationRef javaTypeAnnotationRef;
  @Mock
  ObjectTypeDeclaration javaTypeAnnotation;
  @Mock
  TypeInstance typeInstance;

  @Test
  public void scalarToJavaType() throws Exception {

    when(integer.required()).thenReturn(true);
    when(number.required()).thenReturn(true);
    when(bool.required()).thenReturn(true);

    assertEquals(int.class, ScalarTypes.scalarToJavaType(integer));
    assertEquals(BigDecimal.class, ScalarTypes.scalarToJavaType(number));
    assertEquals(boolean.class, ScalarTypes.scalarToJavaType(bool));
  }

  @Test
  public void requiredMeansPrimitive() throws Exception {

    when(integer.required()).thenReturn(true);
    assertEquals(int.class, ScalarTypes.scalarToJavaType(integer));

    when(integer.required()).thenReturn(false);
    assertEquals(Integer.class, ScalarTypes.scalarToJavaType(integer));
  }

  @Test
  public void intsCanSwitchWithAnnotation() throws Exception {

    when(integer.annotations()).thenReturn(Collections.singletonList(javaTypeAnnotationRef));

    assertEquals(Integer.class, ScalarTypes.scalarToJavaType(integer));
  }

  @Test
  public void size8() throws Exception {

    setupBasic("int8");
    assertEquals(byte.class, ScalarTypes.scalarToJavaType(integer));
    assertEquals(byte.class, ScalarTypes.scalarToJavaType(number));
  }

  @Test
  public void size16() throws Exception {

    setupBasic("int16");

    assertEquals(short.class, ScalarTypes.scalarToJavaType(integer));
    assertEquals(short.class, ScalarTypes.scalarToJavaType(number));
  }


  @Test
  public void size32() throws Exception {

    setupBasic("int32");
    assertEquals(int.class, ScalarTypes.scalarToJavaType(integer));
    assertEquals(int.class, ScalarTypes.scalarToJavaType(number));
  }

  @Test
  public void size64() throws Exception {

    setupBasic("int64");
    assertEquals(long.class, ScalarTypes.scalarToJavaType(integer));
    assertEquals(long.class, ScalarTypes.scalarToJavaType(number));
  }

  @Test
  public void sizeFloat() throws Exception {

    setupBasic("float");
    assertEquals(float.class, ScalarTypes.scalarToJavaType(number));
  }

  @Test
  public void sizeDouble() throws Exception {

    setupBasic("double");
    assertEquals(double.class, ScalarTypes.scalarToJavaType(number));
  }


  @Test
  public void size8Object() throws Exception {

    when(integer.format()).thenReturn("int8");
    when(number.format()).thenReturn("int8");
    when(integer.annotations()).thenReturn(Collections.singletonList(javaTypeAnnotationRef));
    when(number.annotations()).thenReturn(Collections.singletonList(javaTypeAnnotationRef));

    assertEquals(Byte.class, ScalarTypes.scalarToJavaType(integer));
    assertEquals(Byte.class, ScalarTypes.scalarToJavaType(number));
  }


  @Test
  public void size16Object() throws Exception {

    when(integer.format()).thenReturn("int16");
    when(number.format()).thenReturn("int16");
    when(integer.annotations()).thenReturn(Collections.singletonList(javaTypeAnnotationRef));
    when(number.annotations()).thenReturn(Collections.singletonList(javaTypeAnnotationRef));

    assertEquals(Short.class, ScalarTypes.scalarToJavaType(integer));
    assertEquals(Short.class, ScalarTypes.scalarToJavaType(number));
  }

  @Test
  public void size32Object() throws Exception {

    when(integer.format()).thenReturn("int32");
    when(number.format()).thenReturn("int32");
    when(integer.annotations()).thenReturn(Collections.singletonList(javaTypeAnnotationRef));
    when(number.annotations()).thenReturn(Collections.singletonList(javaTypeAnnotationRef));

    assertEquals(Integer.class, ScalarTypes.scalarToJavaType(integer));
    assertEquals(Integer.class, ScalarTypes.scalarToJavaType(number));
  }

  @Test
  public void size64Object() throws Exception {

    when(integer.format()).thenReturn("int64");
    when(number.format()).thenReturn("int64");
    when(integer.annotations()).thenReturn(Collections.singletonList(javaTypeAnnotationRef));
    when(number.annotations()).thenReturn(Collections.singletonList(javaTypeAnnotationRef));

    assertEquals(Long.class, ScalarTypes.scalarToJavaType(integer));
    assertEquals(Long.class, ScalarTypes.scalarToJavaType(number));
  }

  @Test
  public void sizeFloatObject() throws Exception {

    when(number.format()).thenReturn("float");
    when(number.annotations()).thenReturn(Collections.singletonList(javaTypeAnnotationRef));

    assertEquals(Float.class, ScalarTypes.scalarToJavaType(number));
  }

  @Test
  public void sizeDoubleObject() throws Exception {

    when(number.format()).thenReturn("double");
    when(number.annotations()).thenReturn(Collections.singletonList(javaTypeAnnotationRef));
    assertEquals(Double.class, ScalarTypes.scalarToJavaType(number));
  }

  @Test
  public void classToTypeNameTest() throws Exception {

    assertEquals(TypeName.INT, ScalarTypes.classToTypeName(int.class));
    assertEquals(TypeName.INT.box(), ScalarTypes.classToTypeName(Integer.class));
  }

  public void setupBasic(String format) {
    when(integer.format()).thenReturn(format);
    when(number.format()).thenReturn(format);
    when(number.required()).thenReturn(true);
    when(integer.required()).thenReturn(true);
  }

}
