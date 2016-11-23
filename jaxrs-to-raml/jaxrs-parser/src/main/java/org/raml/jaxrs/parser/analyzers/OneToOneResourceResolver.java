package org.raml.jaxrs.parser.analyzers;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.server.model.ResourceMethod;
import org.raml.jaxrs.model.JaxRsResource;
import org.raml.jaxrs.model.Method;
import org.raml.jaxrs.model.Path;
import org.raml.jaxrs.model.HttpVerb;
import org.raml.jaxrs.model.impl.JaxRsResourceImpl;
import org.raml.jaxrs.model.impl.MethodImpl;
import org.raml.jaxrs.model.impl.PathImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import javax.annotation.Nullable;

class OneToOneResourceResolver implements ResourceResolver<Resource> {

    private static final Logger logger = LoggerFactory.getLogger(OneToOneResourceResolver.class);

    private OneToOneResourceResolver() {
    }

    public static OneToOneResourceResolver create() {
        return new OneToOneResourceResolver();
    }


    @Override
    public Iterable<JaxRsResource> resolve(Iterable<Resource> resources) {

        return FluentIterable.from(resources).transform(new Function<Resource, JaxRsResource>() {
            @Nullable
            @Override
            public JaxRsResource apply(@Nullable Resource resource) {
                return toJaxRsResource(resource);
            }
        }).toList(); //Doing it eagerly to avoid some weird stuff later on.
    }

    private JaxRsResource toJaxRsResource(Resource resource) {
        return JaxRsResourceImpl.create(toJaxRsPath(resource.getPath()), resolve(resource.getChildResources()), toJaxRsMethods(resource.getResourceMethods()));
    }

    private Iterable<Method> toJaxRsMethods(List<ResourceMethod> resourceMethods) {
        return FluentIterable.from(resourceMethods).transform(
                new Function<ResourceMethod, Method>() {
                    @Nullable
                    @Override
                    public Method apply(@Nullable ResourceMethod resourceMethod) {
                        return toJaxRsMethod(resourceMethod);
                    }
                }
        );
    }

    private Method toJaxRsMethod(ResourceMethod resourceMethod) {
        logger.debug("converting resource method: {}", resourceMethod);

        return MethodImpl.create(HttpVerb.fromString(resourceMethod.getHttpMethod()), resourceMethod.getConsumedTypes());
    }

    private Path toJaxRsPath(String path) {
        return PathImpl.fromString(path);
    }
}
