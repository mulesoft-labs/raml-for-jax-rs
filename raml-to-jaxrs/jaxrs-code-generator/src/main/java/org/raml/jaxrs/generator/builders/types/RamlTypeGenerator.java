package org.raml.jaxrs.generator.builders.types;

import org.raml.jaxrs.generator.builders.Generator;
import org.raml.jaxrs.generator.builders.TypeGenerator;

/**
 * Created by Jean-Philippe Belanger on 11/13/16.
 * Just potential zeroes and ones
 */
public interface RamlTypeGenerator extends TypeGenerator {

    RamlTypeGenerator addProperty(String type, String name);

}
