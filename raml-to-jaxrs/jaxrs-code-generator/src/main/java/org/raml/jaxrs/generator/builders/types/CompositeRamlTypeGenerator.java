package org.raml.jaxrs.generator.builders.types;

import java.io.IOException;

/**
 * Created by Jean-Philippe Belanger on 11/13/16.
 * Just potential zeroes and ones
 */
public class CompositeRamlTypeGenerator implements RamlTypeGenerator {


    private final RamlTypeGeneratorInterface intf;
    private final RamlTypeGeneratorImplementation impl;


    public CompositeRamlTypeGenerator(RamlTypeGeneratorInterface intf, RamlTypeGeneratorImplementation impl) {
        this.intf = intf;
        this.impl = impl;
    }

    @Override
    public RamlTypeGenerator addProperty(String type, String name) {

        impl.addProperty(type, name);
        intf.addProperty(type, name);

        return this;
    }

    @Override
    public void output(String rootDirectory) throws IOException {
        intf.output(rootDirectory);
        impl.output(rootDirectory);
    }

    @Override
    public boolean declares(String name) {
        return intf.declares(name);
    }
}
