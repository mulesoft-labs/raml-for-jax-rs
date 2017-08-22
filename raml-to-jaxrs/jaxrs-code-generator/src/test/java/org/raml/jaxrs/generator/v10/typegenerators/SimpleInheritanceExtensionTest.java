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

import com.google.common.base.Optional;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.GenerationException;
import org.raml.jaxrs.generator.builders.BuildPhase;
import org.raml.jaxrs.generator.extension.types.FieldExtension;
import org.raml.jaxrs.generator.extension.types.MethodExtension;
import org.raml.jaxrs.generator.extension.types.PredefinedFieldType;
import org.raml.jaxrs.generator.extension.types.PredefinedMethodType;
import org.raml.jaxrs.generator.extension.types.TypeContext;
import org.raml.jaxrs.generator.extension.types.TypeExtension;
import org.raml.jaxrs.generator.matchers.FieldSpecMatchers;
import org.raml.jaxrs.generator.matchers.MethodSpecMatchers;
import org.raml.jaxrs.generator.matchers.ParameterSpecMatchers;
import org.raml.jaxrs.generator.matchers.TypeSpecMatchers;
import org.raml.jaxrs.generator.ramltypes.GType;
import org.raml.jaxrs.generator.v10.Annotations;
import org.raml.jaxrs.generator.v10.V10GProperty;
import org.raml.jaxrs.generator.v10.V10GType;
import org.raml.jaxrs.generator.v10.V10TypeRegistry;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Jean-Philippe Belanger on 3/15/17. Just potential zeroes and ones
 */
public class SimpleInheritanceExtensionTest {

  @Mock
  private V10GType type;

  @Mock
  private V10TypeRegistry registry;

  @Mock
  private CurrentBuild currentBuild;

  @Mock
  private TypeContext typeContext;

  @Mock
  private TypeExtension typeExtension;

  @Mock
  private V10GType parentType;

  @Mock
  private V10GProperty property;

  @Mock
  private GType parameterType;

  @Mock
  private MethodExtension methodExtension;

  @Mock
  private FieldExtension fieldExtension;

  @Before
  public void setup() {

    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void mostlyEmptyDeclaration() throws Exception {

    setupMocking(BuildPhase.INTERFACE);

    SimpleInheritanceExtension ext = new SimpleInheritanceExtension(type, registry, currentBuild);
    TypeSpec.Builder builder = ext.onType(typeContext, null, type, BuildPhase.INTERFACE);

    assertNotNull(builder);
    assertThat(builder.build(), TypeSpecMatchers.name(equalTo("Foo")));
    assertEquals(0, builder.build().methodSpecs.size());
    assertEquals(0, builder.build().superinterfaces.size());

    verify(typeContext).addImplementation();
  }

  @Test
  public void mostlyEmptyImplementation() throws Exception {

    setupMocking(BuildPhase.IMPLEMENTATION);

    SimpleInheritanceExtension ext = new SimpleInheritanceExtension(type, registry, currentBuild);
    TypeSpec.Builder builder = ext.onType(typeContext, null, type, BuildPhase.IMPLEMENTATION);

    assertNotNull(builder);
    assertThat(builder.build(), TypeSpecMatchers.name(equalTo("Foo")));
    assertEquals(0, builder.build().methodSpecs.size());
    assertEquals(1, builder.build().superinterfaces.size());

    verify(typeContext, never()).addImplementation();
  }

  @Test
  public void withParentType() throws Exception {

    setupMocking(BuildPhase.INTERFACE);

    when(type.parentTypes()).thenReturn(Collections.singletonList(parentType));
    when(parentType.defaultJavaTypeName("pack")).thenReturn(ClassName.bestGuess("pack.Daddy"));

    SimpleInheritanceExtension ext = new SimpleInheritanceExtension(type, registry, currentBuild);
    TypeSpec.Builder builder = ext.onType(typeContext, null, type, BuildPhase.INTERFACE);

    TypeName tn = ClassName.bestGuess("pack.Daddy");
    assertNotNull(builder);
    assertThat(builder.build(), TypeSpecMatchers.name(equalTo("Foo")));
    assertThat(builder.build(), TypeSpecMatchers.superInterfaces(
        containsInAnyOrder(tn)));
  }

  @Test
  public void withParentTypeImplementation() throws Exception {

    setupMocking(BuildPhase.IMPLEMENTATION);

    when(type.parentTypes()).thenReturn(Collections.singletonList(parentType));
    when(parentType.defaultJavaTypeName("pack")).thenReturn(ClassName.bestGuess("pack.Daddy"));

    SimpleInheritanceExtension ext = new SimpleInheritanceExtension(type, registry, currentBuild);
    TypeSpec.Builder builder = ext.onType(typeContext, null, type, BuildPhase.IMPLEMENTATION);

    TypeName tn = ClassName.bestGuess("pack.Daddy");
    assertNotNull(builder);
    assertThat(builder.build(), TypeSpecMatchers.name(equalTo("Foo")));
    assertThat(builder.build(), TypeSpecMatchers.superInterfaces(
        containsInAnyOrder(tn)));
  }

  @Test
  public void withParentTypeImplementationAndDiscriminatorNoValue() throws Exception {

    setupMocking(BuildPhase.IMPLEMENTATION);

    when(type.parentTypes()).thenReturn(Collections.singletonList(parentType));

    when(type.properties()).thenReturn(Collections.singletonList(property));
    when(property.name()).thenReturn("Mimi");
    when(property.type()).thenReturn(parameterType);
    when(parameterType.defaultJavaTypeName("pack")).thenReturn(ClassName.get(String.class));
    when(currentBuild.getMethodExtension(Annotations.ON_TYPE_METHOD_CREATION, type)).thenReturn(methodExtension);
    when(currentBuild.getFieldExtension(Annotations.ON_TYPE_FIELD_CREATION, type)).thenReturn(fieldExtension);
    mockMethodCreation(BuildPhase.IMPLEMENTATION);

    when(type.discriminator()).thenReturn("Mimi");
    when(type.discriminatorValue()).thenReturn(Optional.<String>absent());

    when(parentType.defaultJavaTypeName("pack")).thenReturn(ClassName.bestGuess("pack.Daddy"));

    SimpleInheritanceExtension ext = new SimpleInheritanceExtension(type, registry, currentBuild);
    TypeSpec.Builder builder = ext.onType(typeContext, null, type, BuildPhase.IMPLEMENTATION);

    assertNotNull(builder);
    assertThat(builder.build(),
               allOf(
                     TypeSpecMatchers.methods(
                         contains(
                         MethodSpecMatchers.methodName(equalTo("getMimi"))
                         )
                         ),
                     TypeSpecMatchers.fields(
                         containsInAnyOrder(
                         allOf(
                               FieldSpecMatchers.fieldName(equalTo("mimi")),
                               FieldSpecMatchers.fieldType(equalTo(ClassName.get(String.class))),
                               FieldSpecMatchers.initializer(equalTo("\"Foo\""))
                         )
                         )
                         )));
  }

    @Test(expected = GenerationException.class)
    public void withParentTypeImplementationAndDiscriminatorNoValueBadType() throws Exception {

        setupMocking(BuildPhase.IMPLEMENTATION);

        when(type.parentTypes()).thenReturn(Collections.singletonList(parentType));

        when(type.properties()).thenReturn(Collections.singletonList(property));
        when(property.name()).thenReturn("Mimi");
        when(property.type()).thenReturn(parameterType);
        when(parameterType.defaultJavaTypeName("pack")).thenReturn(ClassName.get(Integer.class));
        when(currentBuild.getMethodExtension(Annotations.ON_TYPE_METHOD_CREATION, type)).thenReturn(methodExtension);
        when(currentBuild.getFieldExtension(Annotations.ON_TYPE_FIELD_CREATION, type)).thenReturn(fieldExtension);
        mockMethodCreation(BuildPhase.IMPLEMENTATION);

        when(type.discriminator()).thenReturn("Mimi");
        when(type.discriminatorValue()).thenReturn(Optional.<String>absent());

        when(parentType.defaultJavaTypeName("pack")).thenReturn(ClassName.bestGuess("pack.Daddy"));

        SimpleInheritanceExtension ext = new SimpleInheritanceExtension(type, registry, currentBuild);
        TypeSpec.Builder builder = ext.onType(typeContext, null, type, BuildPhase.IMPLEMENTATION);
    }

    @Test
  public void withProperty() throws Exception {

    setupMocking(BuildPhase.INTERFACE);
    when(type.properties()).thenReturn(Collections.singletonList(property));
    when(property.name()).thenReturn("Mimi");
    when(property.type()).thenReturn(parameterType);
    when(parameterType.defaultJavaTypeName("pack")).thenReturn(ClassName.get(String.class));


    when(currentBuild.getMethodExtension(Annotations.ON_TYPE_METHOD_CREATION, type)).thenReturn(methodExtension);
    mockMethodCreation(BuildPhase.INTERFACE);

    SimpleInheritanceExtension ext = new SimpleInheritanceExtension(type, registry, currentBuild);
    TypeSpec.Builder builder = ext.onType(typeContext, null, type, BuildPhase.INTERFACE);

    assertNotNull(builder);
    TypeName type = ClassName.get(String.class);
    assertThat(builder.build(),
               TypeSpecMatchers.methods(
                   containsInAnyOrder(
                                      MethodSpecMatchers.methodName(equalTo("getMimi")),
                                      allOf(
                                            MethodSpecMatchers.methodName(equalTo("setMimi")),
                                            MethodSpecMatchers.parameters(contains(ParameterSpecMatchers.type(equalTo(type))))
                                      )
                   )
                   ));

    assertEquals(2, builder.build().methodSpecs.size());
  }

  @Test
  public void withPropertyImplementation() throws Exception {

    setupMocking(BuildPhase.IMPLEMENTATION);
    when(type.properties()).thenReturn(Collections.singletonList(property));
    when(property.name()).thenReturn("Mimi");
    when(property.type()).thenReturn(parameterType);
    when(parameterType.defaultJavaTypeName("pack")).thenReturn(ClassName.get(String.class));


    when(currentBuild.getMethodExtension(Annotations.ON_TYPE_METHOD_CREATION, type)).thenReturn(methodExtension);
    when(currentBuild.getFieldExtension(Annotations.ON_TYPE_FIELD_CREATION, type)).thenReturn(fieldExtension);

    mockMethodCreation(BuildPhase.IMPLEMENTATION);

    SimpleInheritanceExtension ext = new SimpleInheritanceExtension(type, registry, currentBuild);
    TypeSpec.Builder builder = ext.onType(typeContext, null, type, BuildPhase.IMPLEMENTATION);

    assertNotNull(builder);
    TypeName type = ClassName.get(String.class);

    assertThat(builder.build(),
               allOf(
                     TypeSpecMatchers.methods(
                         containsInAnyOrder(
                                            MethodSpecMatchers.methodName(equalTo("getMimi")),
                                            allOf(
                                                  MethodSpecMatchers.methodName(equalTo("setMimi")),
                                                  MethodSpecMatchers
                                                      .parameters(contains(ParameterSpecMatchers.type(equalTo(type))))
                                            )
                         )
                         ),
                     TypeSpecMatchers.fields(
                         containsInAnyOrder(
                         allOf(
                               FieldSpecMatchers.fieldName(equalTo("mimi")),
                               FieldSpecMatchers.fieldType(equalTo(ClassName.get(String.class)))
                         )
                         )
                         )));

    assertEquals(2, builder.build().methodSpecs.size());
  }

  private void mockMethodCreation(BuildPhase phase) {


    when(typeContext.onMethod(
                              eq(typeContext), any(TypeSpec.Builder.class), any(MethodSpec.Builder.class),
                              eq(Collections.<ParameterSpec.Builder>emptyList()), eq(type), eq(property),
                              eq(phase), eq(PredefinedMethodType.GETTER))).then(new Answer<MethodSpec.Builder>() {

      @Override
      public MethodSpec.Builder answer(InvocationOnMock invocation) throws Throwable {
        return invocation.getArgument(2);
      }
    });

    when(
         methodExtension.onMethod(
                                  eq(typeContext), any(TypeSpec.Builder.class),
                                  any(MethodSpec.Builder.class),
                                  eq(Collections.<ParameterSpec.Builder>emptyList()), eq(type), eq(property),
                                  eq(phase), eq(PredefinedMethodType.GETTER))).then(new Answer<MethodSpec.Builder>() {

      @Override
      public MethodSpec.Builder answer(InvocationOnMock invocation) throws Throwable {
        return invocation.getArgument(2);
      }
    });



    when(typeContext.onMethod(
                              eq(typeContext), any(TypeSpec.Builder.class), any(MethodSpec.Builder.class),
                              any(List.class), eq(type), eq(property),
                              eq(phase), eq(PredefinedMethodType.SETTER))).then(new Answer<MethodSpec.Builder>() {

      @Override
      public MethodSpec.Builder answer(InvocationOnMock invocation) throws Throwable {
        return invocation.getArgument(2);
      }
    });

    when(methodExtension.onMethod(
                                  eq(typeContext), any(TypeSpec.Builder.class), any(MethodSpec.Builder.class),
                                  any(List.class), eq(type), eq(property),
                                  eq(phase), eq(PredefinedMethodType.SETTER))).then(new Answer<MethodSpec.Builder>() {

      @Override
      public MethodSpec.Builder answer(InvocationOnMock invocation) throws Throwable {
        return invocation.getArgument(2);
      }
    });



    when(typeContext.onField(
                             eq(typeContext), any(TypeSpec.Builder.class),
                             any(FieldSpec.Builder.class), eq(type), eq(property),
                             eq(phase), eq(PredefinedFieldType.PROPERTY))).then(new Answer<FieldSpec.Builder>() {

      @Override
      public FieldSpec.Builder answer(InvocationOnMock invocation) throws Throwable {
        return invocation.getArgument(2);
      }
    });

    when(
         fieldExtension.onField(
                                eq(typeContext), any(TypeSpec.Builder.class), any(FieldSpec.Builder.class), eq(type),
                                eq(property),
                                eq(phase), eq(PredefinedFieldType.PROPERTY))).then(new Answer<FieldSpec.Builder>() {

      @Override
      public FieldSpec.Builder answer(InvocationOnMock invocation) throws Throwable {
        return invocation.getArgument(2);
      }
    });
  }

  private void setupMocking(BuildPhase phase) {
    when(typeContext.getModelPackage()).thenReturn("pack");
    if (phase == BuildPhase.INTERFACE) {
      when(type.defaultJavaTypeName("pack")).thenReturn(ClassName.bestGuess("pack.Foo"));
    } else {

      when(type.defaultJavaTypeName("pack")).thenReturn(ClassName.bestGuess("pack.Daddy"));
      when(type.javaImplementationName("pack")).thenReturn(ClassName.bestGuess("pack.Foo"));
    }

    when(currentBuild.getTypeExtension(Annotations.ON_TYPE_CLASS_CREATION, type)).thenReturn(typeExtension);
    when(currentBuild.getTypeExtension(Annotations.ON_TYPE_CLASS_FINISH, type)).thenReturn(typeExtension);


    when(typeExtension.onType(eq(typeContext), isNull(TypeSpec.Builder.class), eq(type), eq(phase)))
        .thenAnswer(
                    new Answer<TypeSpec.Builder>() {

                      @Override
                      public TypeSpec.Builder answer(InvocationOnMock invocation) throws Throwable {
                        return (TypeSpec.Builder) invocation.getArguments()[1];
                      }
                    });
    when(typeExtension.onType(eq(typeContext), any(TypeSpec.Builder.class), eq(type), eq(phase)))
        .thenAnswer(
                    new Answer<TypeSpec.Builder>() {

                      @Override
                      public TypeSpec.Builder answer(InvocationOnMock invocation) throws Throwable {
                        return (TypeSpec.Builder) invocation.getArguments()[1];
                      }
                    });

    when(typeContext.onType(eq(typeContext), any(TypeSpec.Builder.class), eq(type), eq(phase)))
        .thenAnswer(
                    new Answer<TypeSpec.Builder>() {

                      @Override
                      public TypeSpec.Builder answer(InvocationOnMock invocation) throws Throwable {
                        return (TypeSpec.Builder) invocation.getArguments()[1];
                      }
                    });
  }

}
