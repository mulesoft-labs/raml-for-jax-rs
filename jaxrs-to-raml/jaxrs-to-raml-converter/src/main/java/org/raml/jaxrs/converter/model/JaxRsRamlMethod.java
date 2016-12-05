package org.raml.jaxrs.converter.model;

import org.raml.api.RamlHeaderParameter;
import org.raml.jaxrs.model.Method;
import org.raml.api.RamlMediaType;
import org.raml.api.RamlQueryParameter;
import org.raml.api.RamlResourceMethod;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class JaxRsRamlMethod implements RamlResourceMethod {

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
    public List<RamlMediaType> getConsumedMediaTypes() {
        return Utilities.toRamlMediaTypes(this.resourceMethod.getConsumedMediaTypes()).toList();
    }

    @Override
    public List<RamlMediaType> getProducedMediaTypes() {
        return Utilities.toRamlMediaTypes(this.resourceMethod.getProducedMediaTypes()).toList();
    }

    @Override
    public List<RamlQueryParameter> getQueryParameters() {
        return Utilities.toRamlQueryParameters(this.resourceMethod.getQueryParameters()).toList();
    }

    @Override
    public List<RamlHeaderParameter> getHeaderParameters() {
        return Utilities.toRamlHeaderParameters(this.resourceMethod.getHeaderParameters()).toList();
    }
}
