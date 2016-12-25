package org.raml.jaxrs.generator.builders.resources;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.junit.Test;
import org.raml.jaxrs.generator.builders.CodeContainer;
import org.raml.jaxrs.generator.utils.Raml;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
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
}
