package org.raml.jaxrs.generator.builders.types;

import org.raml.jaxrs.generator.builders.JavaPoetTypeGenerator;

/**
 * Created by Jean-Philippe Belanger on 11/13/16.
 * Just potential zeroes and ones
 */
public interface RamlTypeGenerator extends JavaPoetTypeGenerator {

    RamlTypeGenerator addProperty(String type, String name, boolean internalType);

    RamlTypeGenerator addInternalType(RamlTypeGenerator internalGenerator);
}
