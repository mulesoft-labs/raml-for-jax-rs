package org.raml.jaxrs.parser.model;

import org.glassfish.jersey.server.model.ResourceMethod;
import org.raml.jaxrs.model.HttpVerb;
import org.raml.jaxrs.model.JaxRsHeaderParameter;
import org.raml.jaxrs.model.Method;
import org.raml.jaxrs.model.JaxRsQueryParameter;

import java.util.List;

import javax.ws.rs.core.MediaType;

import static com.google.common.base.Preconditions.checkNotNull;

class JerseyJaxRsMethod implements Method {

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
        return HttpVerb.fromStringUnchecked(resourceMethod.getHttpMethod());
    }

    @Override
    public List<MediaType> getConsumedMediaTypes() {
        return resourceMethod.getConsumedTypes();
    }

    @Override
    public List<MediaType> getProducedMediaTypes() {
        return resourceMethod.getProducedTypes();
    }

    @Override
    public List<JaxRsQueryParameter> getQueryParameters() {
        return Utilities.toJaxRsQueryParameters(Utilities.getQueryParameters(resourceMethod)).toList();
    }

    @Override
    public List<JaxRsHeaderParameter> getHeaderParameters() {
        return Utilities.toJaxRsHeaderParameters(Utilities.getHeaderParameters(resourceMethod)).toList();
    }
}
