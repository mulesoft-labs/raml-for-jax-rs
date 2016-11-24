package org.raml.jaxrs.parser.model;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;

import org.glassfish.jersey.server.model.RuntimeResource;
import org.raml.jaxrs.model.JaxRsApplication;
import org.raml.jaxrs.model.JaxRsResource;

import java.util.Set;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

public class JerseyJaxRsApplication implements JaxRsApplication {

    private final Set<JaxRsResource> resources;

    private JerseyJaxRsApplication(Set<JaxRsResource> resources) {
        this.resources = resources;
    }

    private static JerseyJaxRsApplication create(Iterable<JaxRsResource> resources) {
        checkNotNull(resources);

        return new JerseyJaxRsApplication(ImmutableSet.copyOf(resources));
    }

    public static JerseyJaxRsApplication fromRuntimeResources(Iterable<RuntimeResource> runtimeResources) {
        return create(FluentIterable.from(runtimeResources).transform(
                new Function<RuntimeResource, JaxRsResource>() {
                    @Nullable
                    @Override
                    public JaxRsResource apply(@Nullable RuntimeResource runtimeResource) {
                        return JerseyJaxRsResource.create(runtimeResource);
                    }
                }
        ));
    }

    @Override
    public Set<JaxRsResource> getResources() {
        return resources;
    }
}
