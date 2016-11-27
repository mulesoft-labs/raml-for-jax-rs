package org.raml.jaxrs.generator.builders;

/**
 * Created by Jean-Philippe Belanger on 11/26/16.
 * Just potential zeroes and ones
 */
public interface OutputBuilder<T> {

    void build(T parent);
}
