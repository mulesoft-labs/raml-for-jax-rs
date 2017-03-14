package org.raml.jaxrs.generator.v10.typegenerators;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.builders.BuildPhase;
import org.raml.jaxrs.generator.extension.types.TypeContext;
import org.raml.jaxrs.generator.extension.types.TypeExtension;
import org.raml.jaxrs.generator.v10.V10GType;
import org.raml.jaxrs.generator.v10.V10TypeRegistry;

import static org.hamcrest.core.IsInstanceOf.any;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Jean-Philippe Belanger on 3/14/17.
 * Just potential zeroes and ones
 */
public class SimpleTypeGeneratorTest {

    @Mock
    private V10GType originalType;

    @Mock
    private V10TypeRegistry registry;

    @Mock
    private CurrentBuild currentBuild;

    @Mock
    private TypeExtension typeExtension;

    @Mock
    private org.raml.jaxrs.generator.builders.CodeContainer<com.squareup.javapoet.TypeSpec.Builder> container;

    @Mock
    private TypeContext typeContext;

    private com.squareup.javapoet.TypeSpec.Builder builder;

    @Before
    public void setup() {

        MockitoAnnotations.initMocks(this);
        builder = TypeSpec.classBuilder("Foo");
    }


    @Test
    public void output() throws Exception {

        when(typeExtension.onType(ArgumentMatchers.any(TypeContext.class), isNull(com.squareup.javapoet.TypeSpec.Builder.class), eq(originalType), eq(BuildPhase.IMPLEMENTATION))).thenReturn(builder);
        SimpleTypeGenerator gen = new SimpleTypeGenerator(originalType, registry, currentBuild, typeExtension);
        gen.output(container, BuildPhase.IMPLEMENTATION);

        verify(container).into(isA(TypeSpec.Builder.class));
    }


    @Test
    public void type() throws Exception {

        when(currentBuild.getModelPackage()).thenReturn("pack");
        when(originalType.defaultJavaTypeName("pack")).thenReturn(ClassName.bestGuess("pack.Foo"));
        SimpleTypeGenerator gen = new SimpleTypeGenerator(originalType, registry, currentBuild, typeExtension);
        TypeName type = gen.getGeneratedJavaType();

        assertEquals("pack.Foo", type.toString());
    }


}
