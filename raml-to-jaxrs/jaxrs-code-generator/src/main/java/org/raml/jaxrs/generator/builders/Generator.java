package org.raml.jaxrs.generator.builders;

import java.io.IOException;

/**
 * Created by Jean-Philippe Belanger on 11/19/16.
 * Just potential zeroes and ones
 */
public interface Generator<T> {
    void output(CodeContainer<T> rootDirectory) throws IOException;

}
