package org.raml.jaxrs.parser.analyzers;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

import org.glassfish.jersey.server.model.Resource;
import org.raml.jaxrs.model.JaxRsResource;
import org.raml.jaxrs.model.Path;
import org.raml.jaxrs.model.impl.JaxRsResourceImpl;
import org.raml.jaxrs.model.impl.PathImpl;

import javax.annotation.Nullable;

class OneToOneResourceResolver implements ResourceResolver<Resource> {

    private OneToOneResourceResolver() {}

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
        return JaxRsResourceImpl.create(toJaxRsPath(resource.getPath()), resolve(resource.getChildResources()));
    }

    private Path toJaxRsPath(String path) {
        return PathImpl.fromString(path);
    }
}
