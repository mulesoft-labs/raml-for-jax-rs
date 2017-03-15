package org.raml.jaxrs.generator.v10.typegenerators;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.builders.BuildPhase;
import org.raml.jaxrs.generator.extension.types.MethodExtension;
import org.raml.jaxrs.generator.extension.types.MethodType;
import org.raml.jaxrs.generator.extension.types.PredefinedMethodType;
import org.raml.jaxrs.generator.extension.types.TypeContext;
import org.raml.jaxrs.generator.extension.types.TypeExtension;
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

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.raml.jaxrs.generator.Names.methodName;

/**
 * Created by Jean-Philippe Belanger on 3/15/17.
 * Just potential zeroes and ones
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

    @Before
    public void setup() {

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void mostlyEmptyDeclaration() throws Exception {

        setupMocking();

        SimpleInheritanceExtension ext = new SimpleInheritanceExtension(type, registry, currentBuild);
        TypeSpec.Builder builder = ext.onType(typeContext, null, type, BuildPhase.INTERFACE);

        assertNotNull(builder);
        assertThat(builder.build(), TypeSpecMatchers.name(equalTo("Foo")));
        assertEquals(0, builder.build().methodSpecs.size());
        assertEquals(0, builder.build().superinterfaces.size());

        verify(typeContext).addImplementation();
    }

    @Test
    public void withParentType() throws Exception {

        setupMocking();

        when(type.parentTypes()).thenReturn(Collections.singletonList(parentType));
        when(parentType.defaultJavaTypeName("pack")).thenReturn(ClassName.bestGuess("pack.Daddy"));

        SimpleInheritanceExtension ext = new SimpleInheritanceExtension(type, registry, currentBuild);
        TypeSpec.Builder builder = ext.onType(typeContext, null, type, BuildPhase.INTERFACE);

        TypeName tn = ClassName.bestGuess("pack.Daddy");
        assertNotNull(builder);
        assertThat(builder.build(), TypeSpecMatchers.name(equalTo("Foo")));
        assertThat(builder.build(), TypeSpecMatchers.superclasses(
                containsInAnyOrder(tn)));
    }

    @Test
    public void withProperty() throws Exception {

        setupMocking();
        when(type.properties()).thenReturn(Collections.singletonList(property));
        when(property.name()).thenReturn("Mimi");
        when(property.type()).thenReturn(parameterType);
        when(parameterType.defaultJavaTypeName("pack")).thenReturn(ClassName.get(String.class));


        when(currentBuild.getMethodExtension(Annotations.ON_TYPE_METHOD_CREATION, type)).thenReturn(methodExtension);
        mockMethodCreation();

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
                )
        );

        assertEquals(2, builder.build().methodSpecs.size());
    }

    private void mockMethodCreation() {
        when(typeContext.onMethod(
                eq(typeContext), any(MethodSpec.Builder.class), eq(Collections.<ParameterSpec.Builder>emptyList()),
                eq(type), eq(property), eq(BuildPhase.INTERFACE),
                eq(PredefinedMethodType.GETTER))).then(new Answer<MethodSpec.Builder>() {
            @Override
            public MethodSpec.Builder answer(InvocationOnMock invocation) throws Throwable {
                return invocation.getArgument(1);
            }
        });

        when(methodExtension.onMethod(
                eq(typeContext), any(MethodSpec.Builder.class), eq(Collections.<ParameterSpec.Builder>emptyList()),
                eq(type), eq(property), eq(BuildPhase.INTERFACE),
                eq(PredefinedMethodType.GETTER))).then(new Answer<MethodSpec.Builder>() {
            @Override
            public MethodSpec.Builder answer(InvocationOnMock invocation) throws Throwable {
                return invocation.getArgument(1);
            }
        });

        when(typeContext.onMethod(
                eq(typeContext), any(MethodSpec.Builder.class), any(List.class),
                eq(type), eq(property), eq(BuildPhase.INTERFACE),
                eq(PredefinedMethodType.SETTER))).then(new Answer<MethodSpec.Builder>() {
            @Override
            public MethodSpec.Builder answer(InvocationOnMock invocation) throws Throwable {
                return invocation.getArgument(1);
            }
        });

        when(methodExtension.onMethod(
                eq(typeContext), any(MethodSpec.Builder.class), any(List.class),
                eq(type), eq(property), eq(BuildPhase.INTERFACE),
                eq(PredefinedMethodType.SETTER))).then(new Answer<MethodSpec.Builder>() {
            @Override
            public MethodSpec.Builder answer(InvocationOnMock invocation) throws Throwable {
                return invocation.getArgument(1);
            }
        });

        when(methodExtension.onMethod(
                eq(typeContext), any(MethodSpec.Builder.class), eq(Collections.<ParameterSpec.Builder>emptyList()),
                eq(type), eq(property), eq(BuildPhase.INTERFACE),
                eq(PredefinedMethodType.GETTER))).then(new Answer<MethodSpec.Builder>() {
            @Override
            public MethodSpec.Builder answer(InvocationOnMock invocation) throws Throwable {
                return invocation.getArgument(1);
            }
        });
    }

    private void setupMocking() {
        when(typeContext.getModelPackage()).thenReturn("pack");
        when(type.defaultJavaTypeName("pack")).thenReturn(ClassName.bestGuess("pack.Foo"));

        when(currentBuild.getTypeExtension(Annotations.ON_TYPE_CLASS_CREATION, type)).thenReturn(typeExtension);
        when(currentBuild.getTypeExtension(Annotations.ON_TYPE_CLASS_FINISH, type)).thenReturn(typeExtension);
        when(typeExtension.onType(eq(typeContext), isNull(TypeSpec.Builder.class), eq(type), eq(BuildPhase.INTERFACE))).thenAnswer(
                new Answer<TypeSpec.Builder>() {
                    @Override
                    public TypeSpec.Builder answer(InvocationOnMock invocation) throws Throwable {
                        return (TypeSpec.Builder) invocation.getArguments()[1];
                    }
                });
        when(typeExtension.onType(eq(typeContext), any(TypeSpec.Builder.class), eq(type), eq(BuildPhase.INTERFACE))).thenAnswer(
                new Answer<TypeSpec.Builder>() {
                    @Override
                    public TypeSpec.Builder answer(InvocationOnMock invocation) throws Throwable {
                        return (TypeSpec.Builder) invocation.getArguments()[1];
                    }
                });

        when(typeContext.onType(eq(typeContext), any(TypeSpec.Builder.class), eq(type), eq(BuildPhase.INTERFACE))).thenAnswer(
                new Answer<TypeSpec.Builder>() {
                    @Override
                    public TypeSpec.Builder answer(InvocationOnMock invocation) throws Throwable {
                        return (TypeSpec.Builder) invocation.getArguments()[1];
                    }
                });
    }

}
