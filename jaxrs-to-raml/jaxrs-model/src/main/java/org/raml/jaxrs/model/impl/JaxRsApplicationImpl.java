package org.raml.jaxrs.model.impl;

import com.google.common.collect.ImmutableSet;

import org.raml.jaxrs.model.JaxRsApplication;
import org.raml.jaxrs.model.JaxRsResource;

import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public class JaxRsApplicationImpl implements JaxRsApplication {

    private final Set<JaxRsResource> resources;

    private JaxRsApplicationImpl(Set<JaxRsResource> resources) {
        this.resources = resources;
    }

    public static JaxRsApplicationImpl create(Iterable<JaxRsResource> resources) {
        checkNotNull(resources);

        return new JaxRsApplicationImpl(ImmutableSet.copyOf(resources));
    }

    @Override
    public Set<JaxRsResource> getResources() {
        return resources;
    }
}
