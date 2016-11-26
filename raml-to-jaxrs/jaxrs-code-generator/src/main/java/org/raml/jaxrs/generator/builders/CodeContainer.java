package org.raml.jaxrs.generator.builders;

import java.io.IOException;

/**
 * Created by Jean-Philippe Belanger on 11/25/16.
 * Just potential zeroes and ones
 */
public interface CodeContainer<T> {

    void into(T g) throws IOException;
}
