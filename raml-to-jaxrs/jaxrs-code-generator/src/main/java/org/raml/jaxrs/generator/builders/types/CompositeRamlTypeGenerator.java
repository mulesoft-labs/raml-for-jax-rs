package org.raml.jaxrs.generator.builders.types;

import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.raml.jaxrs.generator.builders.AbstractTypeGenerator;
import org.raml.jaxrs.generator.builders.CodeContainer;
import org.raml.jaxrs.generator.builders.Generator;

import java.io.IOException;

/**
 * Created by Jean-Philippe Belanger on 11/13/16.
 * Just potential zeroes and ones
 */
public class CompositeRamlTypeGenerator extends AbstractTypeGenerator<TypeSpec.Builder> implements RamlTypeGenerator {


    private final RamlTypeGeneratorInterface intf;
    private final RamlTypeGeneratorImplementation impl;


    public CompositeRamlTypeGenerator(RamlTypeGeneratorInterface intf, RamlTypeGeneratorImplementation impl) {
        this.intf = intf;
        this.impl = impl;
    }

    @Override
    public void output(CodeContainer<TypeSpec.Builder> rootDirectory, TYPE type) throws IOException {
        if ( type == TYPE.IMPLEMENTATION) {
            impl.output(rootDirectory);
        } else {
            intf.output(rootDirectory);
        }
    }

    @Override
    public void output(CodeContainer<TypeSpec.Builder> rootDirectory) throws IOException {

        intf.output(rootDirectory, TYPE.INTERFACE);
        impl.output(rootDirectory, TYPE.IMPLEMENTATION);
    }

    @Override
    public TypeName getGeneratedJavaType() {

        return intf.getGeneratedJavaType();
    }
}
