package org.raml.jaxrs.generator.v08;

import org.raml.jaxrs.generator.GParameter;
import org.raml.jaxrs.generator.GType;
import org.raml.v2.api.model.v08.parameters.Parameter;

/**
 * Created by Jean-Philippe Belanger on 12/11/16.
 * Just potential zeroes and ones
 */
public class V08GParameter implements GParameter {
    private final Parameter input;
    private final V08GType type;

    public V08GParameter(org.raml.v2.api.model.v08.parameters.Parameter input) {

        this.input = input;
        this.type = new V08GType(input.type());
    }

    @Override
    public Parameter implementation() {
        return input;
    }

    @Override
    public String name() {
        return input.name();
    }

    @Override
    public boolean isComposite() {
        return false;
    }

    @Override
    public GType type() {
        return type;
    }
}
