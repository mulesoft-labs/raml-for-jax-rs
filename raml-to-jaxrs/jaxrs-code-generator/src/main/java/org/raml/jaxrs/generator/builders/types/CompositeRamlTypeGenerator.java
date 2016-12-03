package org.raml.jaxrs.generator.builders.types;

import com.squareup.javapoet.TypeName;
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
    public void output(CodeContainer<TypeSpec.Builder> rootDirectory) throws IOException {

        intf.output(rootDirectory);
        impl.output(rootDirectory);
    }

    @Override
    public boolean declaresProperty(String name) {
        return intf.declaresProperty(name);
    }

    @Override
    public TypeName getGeneratedJavaType() {

        return intf.getGeneratedJavaType();
    }
}
