package org.raml.jaxrs.generator;

import org.junit.Test;

import java.io.IOException;

/**
 * Created by Jean-Philippe Belanger on 10/29/16.
 * Just potential zeroes and ones
 */
@org.junit.Ignore
public class SimpleTest {

    //@Test
    public void simple() throws IOException, GenerationException {

        RamlScanner scanner = new RamlScanner(
                "/home/ebeljea/LocalProjects/raml-for-jax-rs-fork/raml-to-jaxrs/jaxrs-generated-example/src/main/java",
                "jp.fun"
        );
        scanner.handle("/fun.raml");
    }
}
