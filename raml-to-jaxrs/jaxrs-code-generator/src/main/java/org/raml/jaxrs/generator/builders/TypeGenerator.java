package org.raml.jaxrs.generator.builders;

import com.squareup.javapoet.TypeName;

import java.io.IOException;

/**
 * Created by Jean-Philippe Belanger on 11/20/16.
 * Just potential zeroes and ones
 */
public interface TypeGenerator<T> extends Generator<T> {

    void output(CodeContainer<T> rootDirectory, BuildPhase buildPhase) throws IOException;

    TypeName getGeneratedJavaType();
}
