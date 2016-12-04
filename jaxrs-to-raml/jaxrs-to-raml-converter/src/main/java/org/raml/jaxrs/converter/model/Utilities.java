package org.raml.jaxrs.converter.model;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

import org.raml.jaxrs.model.JaxRsResource;
import org.raml.jaxrs.model.Method;
import org.raml.jaxrs.model.JaxRsQueryParameter;
import org.raml.model.MediaType;
import org.raml.model.RamlQueryParameter;
import org.raml.model.Resource;
import org.raml.model.ResourceMethod;

public class Utilities {

    public static FluentIterable<MediaType> toRamlMediaTypes(Iterable<javax.ws.rs.core.MediaType> mediaTypes) {
        return FluentIterable.from(mediaTypes).transform(
                new Function<javax.ws.rs.core.MediaType, MediaType>() {
                    @Override
                    public MediaType apply(javax.ws.rs.core.MediaType mediaType) {
                        return JaxRsRamlMediaType.create(mediaType);
                    }
                }
        );
    }

    public static FluentIterable<Resource> toRamlResources(Iterable<JaxRsResource> jaxRsResources) {
        return FluentIterable.from(jaxRsResources).transform(
                new Function<JaxRsResource, Resource>() {
                    @Override
                    public Resource apply(JaxRsResource jaxRsResource) {
                        return JaxRsRamlResource.create(jaxRsResource);
                    }
                }
        );
    }

    public static FluentIterable<ResourceMethod> toRamlMethods(Iterable<Method> methods) {
        return FluentIterable.from(methods).transform(
                new Function<Method, ResourceMethod>() {
                    @Override
                    public ResourceMethod apply(Method method) {
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
