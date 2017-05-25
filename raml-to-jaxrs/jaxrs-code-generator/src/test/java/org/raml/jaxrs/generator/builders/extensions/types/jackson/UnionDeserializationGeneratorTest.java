package org.raml.jaxrs.generator.builders.extensions.types.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.builders.CodeContainer;
import org.raml.jaxrs.generator.matchers.MethodSpecMatchers;
import org.raml.jaxrs.generator.matchers.ParameterSpecMatchers;
import org.raml.jaxrs.generator.matchers.TypeSpecMatchers;
import org.raml.jaxrs.generator.v10.V10GType;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.UnionTypeDeclaration;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

/**
 * Created by Jean-Philippe Belanger on 5/24/17.
 * Just potential zeroes and ones
 */
public class UnionDeserializationGeneratorTest {

    @Mock
    private CurrentBuild build;

    @Mock
    private V10GType declaration;

    @Mock
    private UnionTypeDeclaration typeDeclaration;

    @Mock
    private TypeDeclaration unionOfType;

    @Before
    public void mockito() {

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void makeIt() throws Exception {

        when(declaration.name()).thenReturn("Foo");
        when(declaration.implementation()).thenReturn(typeDeclaration);
        when(build.getModelPackage()).thenReturn("model");

        when(typeDeclaration.of()).thenReturn(Arrays.asList(unionOfType));
        when(typeDeclaration.name()).thenReturn("Foo");
        when(unionOfType.name()).thenReturn("UnionOf");

        UnionDeserializationGenerator generator = new UnionDeserializationGenerator(build, declaration,
                ClassName.get("foo", "CooGenerator")) {
            @Override
            protected UnionTypeDeclaration getUnionTypeDeclaration() {
                return typeDeclaration;
            }
        };

        generator.output(new CodeContainer<TypeSpec.Builder>() {
            @Override
            public void into(TypeSpec.Builder g) throws IOException {

                TypeName looksLikeType = ParameterizedTypeName.get(Map.class, String.class, Object.class);
                TypeName jsonParser = ClassName.get(JsonParser.class);
                TypeName context = ClassName.get(DeserializationContext.class);

                assertThat(g.build(), TypeSpecMatchers.name(is(equalTo("CooGenerator"))));
                assertThat(g.build(), TypeSpecMatchers.methods(containsInAnyOrder(
                        allOf(
                                MethodSpecMatchers.methodName(is(equalTo("looksLikeUnionOf"))),
                                MethodSpecMatchers.parameters(
                                        contains(
                                                ParameterSpecMatchers.type(is(equalTo(looksLikeType)))
                                        )
                                )
                        ),
                        allOf(
                                MethodSpecMatchers.methodName(is(equalTo("deserialize"))),
                                MethodSpecMatchers.parameters(
                                        contains(
                                                ParameterSpecMatchers.type(is(equalTo(jsonParser))),
                                                ParameterSpecMatchers.type(is(equalTo(context)))
                                        )
                                ),
                                MethodSpecMatchers.codeContent(containsString("looksLikeUnionOf")) // approx
                        ),
                        allOf(
                                MethodSpecMatchers.methodName(is(equalTo("<init>")))
                        )


                )));

            }
        });
    }

}
