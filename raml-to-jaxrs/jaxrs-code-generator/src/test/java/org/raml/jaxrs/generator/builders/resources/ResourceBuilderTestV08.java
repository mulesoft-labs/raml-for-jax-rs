package org.raml.jaxrs.generator.builders.resources;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.junit.Test;
import org.raml.jaxrs.generator.builders.CodeContainer;
import org.raml.jaxrs.generator.utils.RamlV08;
import org.raml.jaxrs.generator.utils.RamlV10;

import javax.lang.model.element.Modifier;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Jean-Philippe Belanger on 12/25/16.
 * Just potential zeroes and ones
 * More of a function test than a unit test.
 * Shortcut.
 */
public class ResourceBuilderTestV08 {

    @Test
    public void check_query_params() throws Exception {

        RamlV08.buildResourceV08(this, "v08_query_parameters.raml", new CodeContainer<TypeSpec>() {
            @Override
            public void into(TypeSpec g) throws IOException {

                assertEquals("Foo", g.name);
                assertEquals(1, g.methodSpecs.size());
                MethodSpec methodSpec = g.methodSpecs.get(0);
                assertEquals("putAnimalsByUserId", methodSpec.name);
                assertEquals(2, methodSpec.annotations.size());
                assertEquals(ClassName.get(PUT.class), methodSpec.annotations.get(0).type);
                AnnotationSpec mediaTypeSpec = methodSpec.annotations.get(1);
                assertEquals(ClassName.get(Consumes.class), mediaTypeSpec.type);
                assertEquals(1, mediaTypeSpec.members.get("value").size());
                assertEquals("\"application/xml\"", mediaTypeSpec.members.get("value").get(0).toString());
                assertEquals(3, methodSpec.parameters.size());
                assertEquals(TypeName.INT, methodSpec.parameters.get(0).type);
                assertEquals("java.lang.String", methodSpec.parameters.get(1).type.toString());

            }
        }, "foo", "/fun");
    }
}
