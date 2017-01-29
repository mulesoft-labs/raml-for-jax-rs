package org.raml.jaxrs.generator.builders;

import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;

/**
 * Created by Jean-Philippe Belanger on 12/11/16.
 * Just potential zeroes and ones
 */
public abstract class JavaPoetTypeGeneratorBase implements JavaPoetTypeGenerator {

    private TypeName typeName;

    public JavaPoetTypeGeneratorBase(TypeName typeName) {
        this.typeName = typeName;
    }

    @Override
    public void output(CodeContainer<TypeSpec.Builder> rootDirectory, BuildPhase buildPhase) throws IOException {
        output(rootDirectory);
    }


    @Override
    public TypeName getGeneratedJavaType() {
        return typeName;
    }
}
