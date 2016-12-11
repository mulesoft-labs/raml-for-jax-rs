package org.raml.jaxrs.generator.v08;

import org.raml.jaxrs.generator.GRequest;
import org.raml.jaxrs.generator.GType;
import org.raml.v2.api.model.v08.bodies.BodyLike;
import org.raml.v2.api.model.v08.methods.Method;
import org.raml.v2.api.model.v08.resources.Resource;

import java.util.Set;

/**
 * Created by Jean-Philippe Belanger on 12/11/16.
 * Just potential zeroes and ones
 */
public class V08GRequest implements GRequest {
    private final BodyLike input;
    private final V08GType type;

    public V08GRequest(V08GResource v08GResource, V08Method v08Method, BodyLike input, Set<String> globalSchemas) {
        this.input = input;
        if ( globalSchemas.contains(input.schema().value())) {

            this.type = new V08GType(input.schema().value());
        } else {

            this.type = new V08GType(v08GResource.implementation(), v08Method.implementation(), input );
        }
    }

    @Override
    public BodyLike implementation() {
        return input;
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
