package org.raml.jaxrs.generator;

import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

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

}
