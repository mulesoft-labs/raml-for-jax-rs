package org.raml.jaxrs.parser.analyzers.runtime;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

import org.glassfish.jersey.server.model.RuntimeResource;
import org.raml.jaxrs.model.JaxRsResource;
import org.raml.jaxrs.parser.analyzers.ResourceResolver;

import javax.annotation.Nullable;

public class OneToOneRuntimeResourceResolver implements ResourceResolver<RuntimeResource> {

    private OneToOneRuntimeResourceResolver() {}

    public static OneToOneRuntimeResourceResolver create() {
        return new OneToOneRuntimeResourceResolver();
    }

    @Override
    public Iterable<JaxRsResource> resolve(Iterable<RuntimeResource> resources) {
        return fromTheirsToOurs(resources);
    }

    private static Iterable<JaxRsResource> fromTheirsToOurs(Iterable<RuntimeResource> resources) {
        return FluentIterable.from(resources).transform(
                new Function<RuntimeResource, JaxRsResource>() {
                    @Nullable
                    @Override
                    public JaxRsResource apply(@Nullable RuntimeResource runtimeResource) {
                        return fromTheirsToOurs(runtimeResource);
                    }
                }
        );
    }

    private static JaxRsResource fromTheirsToOurs(RuntimeResource runtimeResource) {
        return JerseyRuntimeResource.from(runtimeResource);
    }
}
