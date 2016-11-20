package org.raml.jaxrs.generator.builders.types;

import org.raml.jaxrs.generator.builders.Generator;

/**
 * Created by Jean-Philippe Belanger on 11/13/16.
 * Just potential zeroes and ones
 */
public interface RamlTypeGenerator extends Generator {

    RamlTypeGenerator addProperty(String type, String name);

    // is a property declared here or in my parents ?
    boolean declares(String name);
}
