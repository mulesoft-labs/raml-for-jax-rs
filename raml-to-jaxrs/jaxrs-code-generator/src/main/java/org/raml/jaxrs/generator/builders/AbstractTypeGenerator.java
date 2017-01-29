package org.raml.jaxrs.generator.builders;

import java.io.IOException;

/**
 * Created by Jean-Philippe Belanger on 12/3/16.
 * Just potential zeroes and ones
 */
public abstract class AbstractTypeGenerator<T> implements TypeGenerator<T> {

    @Override
    public void output(CodeContainer<T> rootDirectory) throws IOException {
        output(rootDirectory, BuildPhase.INTERFACE);
    }
}
