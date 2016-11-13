package org.raml.jaxrs.generator.builders.types;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Jean-Philippe Belanger on 11/13/16.
 * Just potential zeroes and ones
 */
public class CompositeTypeBuilder implements TypeBuilder {


    private final TypeBuilderInterface intf;
    private final TypeBuilderImplementation impl;


    public CompositeTypeBuilder(TypeBuilderInterface intf, TypeBuilderImplementation impl) {
        this.intf = intf;
        this.impl = impl;
    }

    @Override
    public TypeBuilder addProperty(String type, String name) {

        impl.addProperty(type, name);
        intf.addProperty(type, name);

        return this;
    }

    @Override
    public void ouput(String rootDirectory) throws IOException {
        intf.ouput(rootDirectory);
        impl.ouput(rootDirectory);
    }

    @Override
    public boolean declares(String name) {
        return intf.declares(name);
    }
}
