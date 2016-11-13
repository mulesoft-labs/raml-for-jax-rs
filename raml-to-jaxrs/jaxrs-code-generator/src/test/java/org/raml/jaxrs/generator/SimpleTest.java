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
                "example.types"
        );
        scanner.handle("/types_user_defined.raml");
    }

    @Test

    public void simple() throws IOException, GenerationException {

        RamlScanner scanner = new RamlScanner(
                "/home/ebeljea/LocalProjects/raml-for-jax-rs-fork/raml-to-jaxrs/jaxrs-generated-example/src/main/java",
                "example.helloworld"
        );
        scanner.handle("/fun.raml");
    }

}
