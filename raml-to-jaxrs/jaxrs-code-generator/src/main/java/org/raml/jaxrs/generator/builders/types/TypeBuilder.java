package org.raml.jaxrs.generator.builders.types;

import java.io.IOException;

/**
 * Created by Jean-Philippe Belanger on 11/13/16.
 * Just potential zeroes and ones
 */
public interface TypeBuilder {

    TypeBuilder addProperty(String type, String name);
    void ouput(String rootDirectory) throws IOException;

    // is a property declared here or in my parents ?
    boolean declares(String name);
}
