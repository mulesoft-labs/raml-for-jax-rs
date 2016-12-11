package org.raml.jaxrs.generator.v08;

import org.raml.jaxrs.generator.GResponseType;
import org.raml.jaxrs.generator.GType;
import org.raml.v2.api.model.v08.bodies.BodyLike;

/**
 * Created by Jean-Philippe Belanger on 12/11/16.
 * Just potential zeroes and ones
 */
public class V08GResponseType implements GResponseType {
    private final BodyLike input;
    private final V08GType type;

    public V08GResponseType(BodyLike input) {
        this.input = input;
        this.type = new V08GType(input.schema().value());
    }

    @Override
    public String mediaType() {
        return input.name();
    }

    @Override
    public GType type() {
        return type;
    }
}
