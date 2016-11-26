package org.raml.jaxrs.generator.builders.types;

import com.squareup.javapoet.TypeSpec;
import org.raml.jaxrs.generator.builders.CodeContainer;

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
    public RamlTypeGenerator addInternalType(RamlTypeGenerator internalGenerator) {

        CompositeRamlTypeGenerator gen = (CompositeRamlTypeGenerator) internalGenerator;
        intf.addInternalType(gen.intf);
        impl.addInternalType(gen.impl);

        return this;
    }

    @Override
    public RamlTypeGenerator addProperty(String type, String name, boolean internalType) {

        impl.addProperty(type, name, internalType);
        intf.addProperty(type, name, internalType);

        return this;
    }

    @Override
    public void output(CodeContainer<TypeSpec.Builder> rootDirectory) throws IOException {

        intf.output(rootDirectory);
        impl.output(rootDirectory);
    }

    @Override
    public boolean declaresProperty(String name) {
        return intf.declaresProperty(name);
    }

    @Override
    public String getGeneratedJavaType() {

        return intf.getGeneratedJavaType();
    }
}
