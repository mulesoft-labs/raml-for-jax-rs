package org.raml.jaxrs.generator.builders.resources;

/**
 * Created by Jean-Philippe Belanger on 11/5/16.
 * Just potential zeroes and ones
 */
public interface ResponseClassBuilder {
    String name();

    void withResponse(String value);
    void withResponse(String code, String name, String type);

    void output();

}
