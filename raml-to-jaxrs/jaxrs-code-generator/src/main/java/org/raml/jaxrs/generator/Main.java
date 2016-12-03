package org.raml.jaxrs.generator;

import java.io.File;
import java.io.IOException;

/**
 * Created by Jean-Philippe Belanger on 11/10/16.
 * Just potential zeroes and ones
 */
public class Main {

    public static void main(String[] args) throws IOException, GenerationException {
        RamlScanner scanner = new RamlScanner(args[1], args[2]);
        scanner.handle(new File(args[0]).toURI().toURL(), args.length <= 3 ? "." : args[3]);
    }
}
