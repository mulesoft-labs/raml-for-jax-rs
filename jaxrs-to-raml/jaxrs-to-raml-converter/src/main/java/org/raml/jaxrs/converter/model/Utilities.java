package org.raml.jaxrs.converter.model;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

import org.raml.jaxrs.model.JaxRsResource;
import org.raml.jaxrs.model.Method;
import org.raml.jaxrs.model.JaxRsQueryParameter;
import org.raml.api.RamlMediaType;
import org.raml.api.RamlQueryParameter;
import org.raml.api.RamlResource;
import org.raml.api.RamlResourceMethod;

public class Utilities {

    public static FluentIterable<RamlMediaType> toRamlMediaTypes(Iterable<javax.ws.rs.core.MediaType> mediaTypes) {
        return FluentIterable.from(mediaTypes).transform(
                new Function<javax.ws.rs.core.MediaType, RamlMediaType>() {
                    @Override
                    public RamlMediaType apply(javax.ws.rs.core.MediaType mediaType) {
                        return JaxRsRamlMediaType.create(mediaType);
                    }
                }
        );
    }

    public static FluentIterable<RamlResource> toRamlResources(Iterable<JaxRsResource> jaxRsResources) {
        return FluentIterable.from(jaxRsResources).transform(
                new Function<JaxRsResource, RamlResource>() {
                    @Override
                    public RamlResource apply(JaxRsResource jaxRsResource) {
                        return JaxRsRamlResource.create(jaxRsResource);
                    }
                }
        );
    }

    public static FluentIterable<RamlResourceMethod> toRamlMethods(Iterable<Method> methods) {
        return FluentIterable.from(methods).transform(
                new Function<Method, RamlResourceMethod>() {
                    @Override
                    public RamlResourceMethod apply(Method method) {
                        return JaxRsRamlMethod.create(method);
                    }
                }
        );
    }

    public static FluentIterable<RamlQueryParameter> toRamlQueryParameters(Iterable<JaxRsQueryParameter> queryParameters) {
        return FluentIterable.from(queryParameters).transform(
                new Function<JaxRsQueryParameter, RamlQueryParameter>() {
                    @Override
                    public RamlQueryParameter apply(JaxRsQueryParameter queryParameter) {
                        return JaxRsRamlQueryParameter.create(queryParameter);
                    }
                }
        );
    }
}
