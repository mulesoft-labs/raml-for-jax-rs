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
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.builders.BuildPhase;
import org.raml.jaxrs.generator.extension.types.PredefinedFieldType;
import org.raml.jaxrs.generator.extension.types.PredefinedMethodType;
import org.raml.jaxrs.generator.v10.V10GProperty;
import org.raml.jaxrs.generator.v10.V10GType;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Created by Jean-Philippe Belanger on 3/19/17. Just potential zeroes and ones
 */
public class TypeExtensionHelperTest {

  @Mock
  TypeContextImpl typeContext;

  @Mock
  private V10GType type;

  boolean called = false;

  @Mock
  private CurrentBuild currentBuild;

  @Mock
  private TypeDeclaration objectTypeDeclaration;

  @Mock
  private TypeDeclaration propertyTypeDeclaration;

  @Mock
  private V10GProperty property;

  @Before
  public void mocking() {

    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void onTypeImplementation() throws Exception {

    final TypeSpec.Builder fooClass = TypeSpec.classBuilder("Foo");

    when(typeContext.getBuildContext()).thenReturn(currentBuild);
    when(type.implementation()).thenReturn(objectTypeDeclaration);

    TypeExtensionHelper extensionHelper = new TypeExtensionHelper() {

      @Override
      public void onTypeImplementation(CurrentBuild currentBuild, TypeSpec.Builder typeSpec, TypeDeclaration typeDeclaration) {

        assertSame(TypeExtensionHelperTest.this.currentBuild, currentBuild);
        assertSame(fooClass, typeSpec);
        assertSame(TypeExtensionHelperTest.this.objectTypeDeclaration, typeDeclaration);
        called = true;
      }
    };

    extensionHelper.onType(typeContext, fooClass, type, BuildPhase.IMPLEMENTATION);

    assertTrue(called);
  }

  @Test
  public void onTypeDeclaration() throws Exception {

    final TypeSpec.Builder fooClass = TypeSpec.classBuilder("Foo");

    when(typeContext.getBuildContext()).thenReturn(currentBuild);
    when(type.implementation()).thenReturn(objectTypeDeclaration);

    TypeExtensionHelper extensionHelper = new TypeExtensionHelper() {

      @Override
      public void onTypeDeclaration(CurrentBuild currentBuild, TypeSpec.Builder typeSpec, V10GType type) {

        assertSame(TypeExtensionHelperTest.this.currentBuild, currentBuild);
        assertSame(fooClass, typeSpec);
        assertSame(TypeExtensionHelperTest.this.type, type);
        called = true;
      }
    };

    extensionHelper.onType(typeContext, fooClass, type, BuildPhase.INTERFACE);

    assertTrue(called);
  }


  @Test
  public void onFieldImplementation() throws Exception {

    final TypeSpec.Builder fooClass = TypeSpec.classBuilder("Foo");
    final FieldSpec.Builder field = FieldSpec.builder(ClassName.SHORT, "field");

    when(typeContext.getBuildContext()).thenReturn(currentBuild);
    when(property.implementation()).thenReturn(propertyTypeDeclaration);

    TypeExtensionHelper extensionHelper = new TypeExtensionHelper() {

      @Override
      public void onFieldImplementation(CurrentBuild currentBuild, FieldSpec.Builder fieldSpec,
                                        TypeDeclaration typeDeclaration) {

        assertSame(TypeExtensionHelperTest.this.currentBuild, currentBuild);
        assertSame(field, fieldSpec);
        assertSame(TypeExtensionHelperTest.this.propertyTypeDeclaration, typeDeclaration);
        called = true;
      }
    };

    extensionHelper.onField(typeContext, fooClass, field, type, property, BuildPhase.IMPLEMENTATION,
                            PredefinedFieldType.PROPERTY);

    assertTrue(called);
  }

  @Test
  public void onGetterSetterImplementation() throws Exception {

    final TypeSpec.Builder fooClass = TypeSpec.classBuilder("Foo");
    final MethodSpec.Builder method = MethodSpec.methodBuilder("meth");
    final ParameterSpec.Builder parameter = ParameterSpec.builder(ClassName.SHORT, "param");

    when(typeContext.getBuildContext()).thenReturn(currentBuild);
    when(property.implementation()).thenReturn(propertyTypeDeclaration);

    TypeExtensionHelper extensionHelper = new TypeExtensionHelper() {

      @Override
      public void onSetterMethodImplementation(CurrentBuild currentBuild, MethodSpec.Builder methodSpec,
                                               ParameterSpec.Builder param, TypeDeclaration typeDeclaration) {

        assertSame(TypeExtensionHelperTest.this.currentBuild, currentBuild);
        assertSame(method, methodSpec);
        assertSame(parameter, param);
        assertSame(TypeExtensionHelperTest.this.propertyTypeDeclaration, typeDeclaration);
        called = true;
      }

      @Override
      public void onGetterMethodImplementation(CurrentBuild currentBuild, MethodSpec.Builder methodSpec,
                                               TypeDeclaration typeDeclaration) {

        assertSame(TypeExtensionHelperTest.this.currentBuild, currentBuild);
        assertSame(method, methodSpec);
        assertSame(TypeExtensionHelperTest.this.propertyTypeDeclaration, typeDeclaration);
        called = true;
      }

      @Override
      public void onGetterMethodDeclaration(CurrentBuild currentBuild, MethodSpec.Builder methodSpec,
                                            TypeDeclaration typeDeclaration) {
        assertSame(TypeExtensionHelperTest.this.currentBuild, currentBuild);
        assertSame(method, methodSpec);
        assertSame(TypeExtensionHelperTest.this.propertyTypeDeclaration, typeDeclaration);
        called = true;
      }

      @Override
      public void onSetterMethodDeclaration(CurrentBuild currentBuild, MethodSpec.Builder methodSpec,
                                            ParameterSpec.Builder param, TypeDeclaration typeDeclaration) {
        assertSame(TypeExtensionHelperTest.this.currentBuild, currentBuild);
        assertSame(method, methodSpec);
        assertSame(parameter, param);
        assertSame(TypeExtensionHelperTest.this.propertyTypeDeclaration, typeDeclaration);
        called = true;
      }
    };

    extensionHelper.onMethod(typeContext, fooClass, method, Collections.<ParameterSpec.Builder>emptyList(), type, property,
                             BuildPhase.IMPLEMENTATION, PredefinedMethodType.GETTER);
    assertTrue(called);

    called = false;
    extensionHelper.onMethod(typeContext, fooClass, method, Collections.singletonList(parameter), type, property,
                             BuildPhase.IMPLEMENTATION, PredefinedMethodType.SETTER);
    assertTrue(called);

    called = false;
    extensionHelper.onMethod(typeContext, fooClass, method, Collections.<ParameterSpec.Builder>emptyList(), type, property,
                             BuildPhase.INTERFACE, PredefinedMethodType.GETTER);
    assertTrue(called);

    called = false;
    extensionHelper.onMethod(typeContext, fooClass, method, Collections.singletonList(parameter), type, property,
                             BuildPhase.INTERFACE, PredefinedMethodType.SETTER);
    assertTrue(called);
  }

}
