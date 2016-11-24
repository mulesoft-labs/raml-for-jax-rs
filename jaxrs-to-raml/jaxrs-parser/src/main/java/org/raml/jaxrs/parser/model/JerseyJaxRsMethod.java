package org.raml.jaxrs.parser.model;

import org.glassfish.jersey.server.model.ResourceMethod;
import org.raml.jaxrs.model.HttpVerb;
import org.raml.jaxrs.model.Method;

import java.util.List;

import javax.ws.rs.core.MediaType;

import static com.google.common.base.Preconditions.checkNotNull;

public class JerseyJaxRsMethod implements Method {

    private final ResourceMethod resourceMethod;

    private JerseyJaxRsMethod(ResourceMethod resourceMethod) {
        this.resourceMethod = resourceMethod;
    }

    public static JerseyJaxRsMethod create(ResourceMethod resourceMethod) {
        checkNotNull(resourceMethod);

        return new JerseyJaxRsMethod(resourceMethod);
    }

    @Override
    public HttpVerb getHttpVerb() {
        return HttpVerb.fromString(resourceMethod.getHttpMethod());
    }

    @Override
    public List<MediaType> getConsumedMediaTypes() {
        return resourceMethod.getConsumedTypes();
    }

    @Override
    public Iterable<MediaType> getProducedMediaTypes() {
        return resourceMethod.getProducedTypes();
    }
}
