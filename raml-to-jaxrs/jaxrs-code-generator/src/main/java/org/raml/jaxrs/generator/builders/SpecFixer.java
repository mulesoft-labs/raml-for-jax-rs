package org.raml.jaxrs.generator.builders;

/**
 * Created by Jean-Philippe Belanger on 11/13/16.
 * Just potential zeroes and ones
 */
public interface SpecFixer<T> {

    void adjust(T spec);
}
