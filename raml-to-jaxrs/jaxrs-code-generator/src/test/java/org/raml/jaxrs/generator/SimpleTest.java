package org.raml.jaxrs.generator;

import org.junit.Test;

import java.io.IOException;

/**
 * Created by Jean-Philippe Belanger on 10/29/16.
 * Just potential zeroes and ones
 */
@org.junit.Ignore
public class SimpleTest {

    @Test
    public void simple() throws IOException, GenerationException {

        RamlScanner scanner = new RamlScanner();
        scanner.handle("/fun.raml");
    }
}
