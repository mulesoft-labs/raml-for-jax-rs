package org.raml.jaxrs.generator;

import org.junit.Ignore;
import org.junit.Test;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.v10.resources.Resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

/**
 * Created by Jean-Philippe Belanger on 10/29/16.
 * Just potential zeroes and ones
 */
@Ignore
public class SimpleTest {

    @Test
    public void simpleType() throws IOException, GenerationException {

        RamlScanner scanner = new RamlScanner(
                "/home/ebeljea/LocalProjects/raml-for-jax-rs-fork/raml-to-jaxrs/jaxrs-generated-example/src/main/java",
                "example.types.raml"
        );
        scanner.handle("/types_user_defined.raml");
    }

    @Test
    public void jsonSchemaToPojo() throws IOException, GenerationException {

        RamlScanner scanner = new RamlScanner(
                "/home/ebeljea/LocalProjects/raml-for-jax-rs-fork/raml-to-jaxrs/jaxrs-generated-example/src/main/java",
                "example.types.schema.json"
        );
        scanner.handle("/types_json_schema.raml");
    }

    @Test
    public void xmlSchemaToPojo() throws IOException, GenerationException {

        RamlScanner scanner = new RamlScanner(
                "/home/ebeljea/LocalProjects/raml-for-jax-rs-fork/raml-to-jaxrs/jaxrs-generated-example/src/main/java",
                "example.types.schema.xml"
        );
        scanner.handle("/types_xml_schema.raml");
    }

    @Test
    public void xmlAnonymous() throws IOException, GenerationException {

        RamlScanner scanner = new RamlScanner(
                "/home/ebeljea/LocalProjects/raml-for-jax-rs-fork/raml-to-jaxrs/jaxrs-generated-example/src/main/java",
                "example.types.schema.xml"
        );
        scanner.handle("/anonymous_xml_schema.raml");
    }

    @Test
    public void simple() throws IOException, GenerationException {

        RamlScanner scanner = new RamlScanner(
                "/home/ebeljea/LocalProjects/raml-for-jax-rs-fork/raml-to-jaxrs/jaxrs-generated-example/src/main/java",
                "example.helloworld"
        );
        scanner.handle("/base.raml");
    }

    @Test
    public void typeDependencies() throws IOException, GenerationException {

        RamlScanner scanner = new RamlScanner(
                "/home/ebeljea/LocalProjects/raml-for-jax-rs-fork/raml-to-jaxrs/jaxrs-generated-example/src/main/java",
                "example.helloworld"
        );
        scanner.handle("/type_dependencies.raml");
    }

    @Test
    public void internalTypesToTypes() throws IOException, GenerationException {

        RamlScanner scanner = new RamlScanner(
                "/home/ebeljea/LocalProjects/raml-for-jax-rs-fork/raml-to-jaxrs/jaxrs-generated-example/src/main/java",
                "example.helloworld"
        );
        scanner.handle("/internal_types_for_types.raml");
    }

    @Test
    public void internalTypesToResources() throws IOException, GenerationException {

        RamlScanner scanner = new RamlScanner(
                "/home/ebeljea/LocalProjects/raml-for-jax-rs-fork/raml-to-jaxrs/jaxrs-generated-example/src/main/java",
                "example.helloworld"
        );
        scanner.handle("/internal_types_for_resources.raml");
    }

    @Test
    public void worldMusic() throws IOException, GenerationException {

        RamlScanner scanner = new RamlScanner(
                "/home/ebeljea/LocalProjects/raml-for-jax-rs-fork/raml-to-jaxrs/jaxrs-generated-example/src/main/java",
                "example.worldmusic"
        );
        scanner.handle(new File("/home/ebeljea/LocalProjects/raml-for-jax-rs-fork/raml-to-jaxrs/jaxrs-generated-example/src/main/resources/world-music-api/api.raml"));
    }

    @Test
    public void marketing() throws IOException, GenerationException {

        RamlScanner scanner = new RamlScanner(
                "/home/ebeljea/LocalProjects/raml-for-jax-rs-fork/raml-to-jaxrs/jaxrs-generated-example/src/main/java",
                "example.marketing"
        );
        scanner.handle(new File("/home/ebeljea/LocalProjects/raml-for-jax-rs-fork/raml-to-jaxrs/jaxrs-generated-example/src/main/resources/marketing-cloud/api.raml"));
    }

    @Test
    public void fick() throws Exception {
        RamlModelResult result = new RamlModelBuilder().buildApi(new File("/home/ebeljea/LocalProjects/raml-for-jax-rs-fork/raml-to-jaxrs/jaxrs-code-generator/src/test/resources/base.raml"));
        Resource r1 = result.getApiV10().resources().get(0);
        Resource r2 = result.getApiV10().resources().get(0).resources().get(1);

        System.err.println("foo " + r1.resourcePath() + " " + r2.resourcePath());
        assertEquals(r1, r2);

    }

}
