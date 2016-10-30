package org.raml.jaxrs.generator;

/**
 * Created by Jean-Philippe Belanger on 10/30/16.
 * Just potential zeroes and ones
 */
public class Paths {

    public static String relativize(String path) {

        if (path.startsWith("/")) {
            return path.substring(1);
        } else {
            return path;
        }
    }
}
