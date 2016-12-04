package org.raml.jaxrs.converter.model;

import org.raml.jaxrs.model.Method;
import org.raml.model.MediaType;
import org.raml.model.RamlQueryParameter;
import org.raml.model.ResourceMethod;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class JaxRsRamlMethod implements ResourceMethod {

    private final Method resourceMethod;

    private JaxRsRamlMethod(Method resourceMethod) {
        this.resourceMethod = resourceMethod;
    }

    public static JaxRsRamlMethod create(Method resourceMethod) {
        checkNotNull(resourceMethod);

        return new JaxRsRamlMethod(resourceMethod);
    }

    @Override
    public String getHttpMethod() {
        return this.resourceMethod.getHttpVerb().getString().toLowerCase();
    }

    @Override
    public List<MediaType> getConsumedMediaTypes() {
        return Utilities.toRamlMediaTypes(this.resourceMethod.getConsumedMediaTypes()).toList();
    }

    @Override
    public List<MediaType> getProducedMediaTypes() {
        return Utilities.toRamlMediaTypes(this.resourceMethod.getProducedMediaTypes()).toList();
    }

    @Override
    public List<RamlQueryParameter> getQueryParameters() {
        return Utilities.toRamlQueryParameters(this.resourceMethod.getQueryParameters()).toList();
    }
}
