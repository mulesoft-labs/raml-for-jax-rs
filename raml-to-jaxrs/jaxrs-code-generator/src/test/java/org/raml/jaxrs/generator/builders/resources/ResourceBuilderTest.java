package org.raml.jaxrs.generator.builders.resources;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import com.sun.codemodel.ClassType;
import org.junit.Test;
import org.raml.jaxrs.generator.builders.CodeContainer;
import org.raml.jaxrs.generator.utils.Raml;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Created by Jean-Philippe Belanger on 12/25/16.
 * Just potential zeroes and ones
 * More of a function test than a unit test.
 * Shortcut.
 */
public class ResourceBuilderTest {

    @Test
    public void build_simple() throws Exception {

        Raml.buildResource(this, "resource_entity_no_response.raml", new CodeContainer<TypeSpec>() {
            @Override
            public void into(TypeSpec g) throws IOException {

                assertEquals("Foo", g.name);
                assertEquals(1, g.methodSpecs.size());
                MethodSpec methodSpec = g.methodSpecs.get(0);
                assertEquals("postSearch", methodSpec.name);
                assertEquals(2, methodSpec.annotations.size());
                assertEquals(ClassName.get(POST.class), methodSpec.annotations.get(0).type);
                AnnotationSpec mediaTypeSpec = methodSpec.annotations.get(1);
                assertEquals(ClassName.get(Consumes.class), mediaTypeSpec.type);
                assertEquals(1, mediaTypeSpec.members.get("value").size());
                assertEquals("\"application/json\"", mediaTypeSpec.members.get("value").get(0).toString());
                assertEquals(1, methodSpec.parameters.size());
                assertEquals(ClassName.get(String.class), methodSpec.parameters.get(0).type);
            }
        }, "foo", "/fun");
    }

    @Test
    public void build_same_type_two_media() throws Exception {

        Raml.buildResource(this, "resource_entity_same_type_two_media.raml", new CodeContainer<TypeSpec>() {
            @Override
            public void into(TypeSpec g) throws IOException {

                assertEquals("Foo", g.name);
                assertEquals(1, g.methodSpecs.size());
                MethodSpec methodSpec = g.methodSpecs.get(0);
                assertEquals("postSearch", methodSpec.name);
                assertEquals(2, methodSpec.annotations.size());
                assertEquals(ClassName.get(POST.class), methodSpec.annotations.get(0).type);
                AnnotationSpec mediaTypeSpec = methodSpec.annotations.get(1);
                assertEquals(ClassName.get(Consumes.class), mediaTypeSpec.type);
                assertEquals(2, mediaTypeSpec.members.get("value").size());
                assertEquals("\"application/json\"", mediaTypeSpec.members.get("value").get(0).toString());
                assertEquals("\"application/xml\"", mediaTypeSpec.members.get("value").get(1).toString());
                assertEquals(1, methodSpec.parameters.size());
                assertEquals(ClassName.get(String.class), methodSpec.parameters.get(0).type);
            }
        }, "foo", "/fun");
    }

    @Test
    public void build_two_types_different_media() throws Exception {

        Raml.buildResource(this, "resource_entity_two_types_different_media.raml", new CodeContainer<TypeSpec>() {
            @Override
            public void into(TypeSpec g) throws IOException {

                assertEquals("Foo", g.name);
                assertEquals(2, g.methodSpecs.size());
                {
                    MethodSpec methodSpec = g.methodSpecs.get(0);
                    assertEquals("postSearch", methodSpec.name);
                    assertEquals(2, methodSpec.annotations.size());
                    assertEquals(ClassName.get(POST.class), methodSpec.annotations.get(0).type);
                    AnnotationSpec mediaTypeSpec = methodSpec.annotations.get(1);
                    assertEquals(ClassName.get(Consumes.class), mediaTypeSpec.type);
                    assertEquals(1, mediaTypeSpec.members.get("value").size());
                    assertEquals("\"application/json\"", mediaTypeSpec.members.get("value").get(0).toString());
                    assertEquals(1, methodSpec.parameters.size());
                    assertEquals(ClassName.get(String.class), methodSpec.parameters.get(0).type);
                }
                {
                    MethodSpec methodSpec = g.methodSpecs.get(1);
                    assertEquals("postSearch", methodSpec.name);
                    assertEquals(2, methodSpec.annotations.size());
                    assertEquals(ClassName.get(POST.class), methodSpec.annotations.get(0).type);
                    AnnotationSpec mediaTypeSpec = methodSpec.annotations.get(1);
                    assertEquals(ClassName.get(Consumes.class), mediaTypeSpec.type);
                    assertEquals(1, mediaTypeSpec.members.get("value").size());
                    assertEquals("\"application/xml\"", mediaTypeSpec.members.get("value").get(0).toString());
                    assertEquals(1, methodSpec.parameters.size());
                    assertEquals(ClassName.INT, methodSpec.parameters.get(0).type);
                }

            }
        }, "foo", "/fun");
    }

    @Test
    public void build_empty() throws Exception {

        Raml.buildResource(this, "resource_no_entity_no_response.raml", new CodeContainer<TypeSpec>() {
            @Override
            public void into(TypeSpec g) throws IOException {

                assertEquals("Foo", g.name);
                assertEquals(1, g.methodSpecs.size());
                MethodSpec methodSpec = g.methodSpecs.get(0);
                assertEquals("postSearch", methodSpec.name);
                assertEquals(1, methodSpec.annotations.size());
                assertEquals(ClassName.get(POST.class), methodSpec.annotations.get(0).type);
                assertEquals(0, methodSpec.parameters.size());
            }
        }, "foo", "/fun");
    }

    @Test
    public void build_with_path_param() throws Exception {

        Raml.buildResource(this, "resource_no_entity_path_param.raml", new CodeContainer<TypeSpec>() {
            @Override
            public void into(TypeSpec g) throws IOException {

                assertEquals("Foo", g.name);
                assertEquals(1, g.methodSpecs.size());
                MethodSpec methodSpec = g.methodSpecs.get(0);
                assertEquals("postSearchByOneAndTwo", methodSpec.name);
                assertEquals(1, methodSpec.annotations.size());
                assertEquals(ClassName.get(POST.class), methodSpec.annotations.get(0).type);
                assertEquals(2, methodSpec.parameters.size());

                ParameterSpec paramOneSpec = methodSpec.parameters.get(0);
                assertEquals("one", paramOneSpec.name);
                assertEquals(ClassName.get(String.class), paramOneSpec.type);
                assertEquals(1, paramOneSpec.annotations.size());
                assertEquals(ClassName.get(PathParam.class), paramOneSpec.annotations.get(0).type);
                assertEquals("\"one\"", paramOneSpec.annotations.get(0).members.get("value").get(0).toString());
                
                ParameterSpec paramTwoSpec = methodSpec.parameters.get(1);
                assertEquals("two", paramTwoSpec.name);
                assertEquals(ClassName.INT, paramTwoSpec.type);
                assertEquals(1, paramTwoSpec.annotations.size());
                assertEquals(ClassName.get(PathParam.class), paramTwoSpec.annotations.get(0).type);
                assertEquals("\"two\"", paramTwoSpec.annotations.get(0).members.get("value").get(0).toString());
            }
        }, "foo", "/fun");
    }

    @Test
    public void build_with_query_param() throws Exception {

        Raml.buildResource(this, "resource_no_entity_query_param.raml", new CodeContainer<TypeSpec>() {
            @Override
            public void into(TypeSpec g) throws IOException {

                assertEquals("Foo", g.name);
                assertEquals(1, g.methodSpecs.size());
                MethodSpec methodSpec = g.methodSpecs.get(0);
                assertEquals("postSearch", methodSpec.name);
                assertEquals(1, methodSpec.annotations.size());
                assertEquals(ClassName.get(POST.class), methodSpec.annotations.get(0).type);
                assertEquals(2, methodSpec.parameters.size());

                ParameterSpec paramOneSpec = methodSpec.parameters.get(0);
                assertEquals("one", paramOneSpec.name);
                assertEquals(ClassName.get(String.class), paramOneSpec.type);
                assertEquals(1, paramOneSpec.annotations.size());
                assertEquals(ClassName.get(QueryParam.class), paramOneSpec.annotations.get(0).type);
                assertEquals("\"one\"", paramOneSpec.annotations.get(0).members.get("value").get(0).toString());

                ParameterSpec paramTwoSpec = methodSpec.parameters.get(1);
                assertEquals("two", paramTwoSpec.name);
                assertEquals(ClassName.INT, paramTwoSpec.type);
                assertEquals(1, paramTwoSpec.annotations.size());
                assertEquals(ClassName.get(QueryParam.class), paramTwoSpec.annotations.get(0).type);
                assertEquals("\"two\"", paramTwoSpec.annotations.get(0).members.get("value").get(0).toString());
            }
        }, "foo", "/fun");
    }

}
