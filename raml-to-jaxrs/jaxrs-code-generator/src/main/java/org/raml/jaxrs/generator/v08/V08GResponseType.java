package org.raml.jaxrs.generator.v08;

import org.raml.jaxrs.generator.GResponseType;
import org.raml.jaxrs.generator.GType;
import org.raml.v2.api.model.v08.bodies.BodyLike;
import org.raml.v2.api.model.v08.bodies.Response;
import org.raml.v2.api.model.v08.methods.Method;
import org.raml.v2.api.model.v08.resources.Resource;

import java.util.Set;

/**
 * Created by Jean-Philippe Belanger on 12/11/16.
 * Just potential zeroes and ones
 */
public class V08GResponseType implements GResponseType {
    private final BodyLike input;
    private final V08GType type;

    public V08GResponseType(Resource resource, Method method, Response response, BodyLike input, Set<String> globalSchemas) {
        this.input = input;

        if ( globalSchemas.contains(input.schema().value())) {

            this.type = new V08GType(input.schema().value());
        } else {

            this.type = new V08GType(resource, method, response, input );
        }
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
