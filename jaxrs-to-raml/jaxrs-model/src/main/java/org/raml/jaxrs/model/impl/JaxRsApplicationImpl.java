package org.raml.jaxrs.model.impl;

import com.google.common.collect.ImmutableSet;

import org.raml.jaxrs.model.JaxRsApplication;
import org.raml.jaxrs.model.Resource;

import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public class JaxRsApplicationImpl implements JaxRsApplication {

    private final Set<Resource> resources;

    private JaxRsApplicationImpl(Set<Resource> resources) {
        this.resources = resources;
    }

    public static JaxRsApplicationImpl create(Iterable<Resource> resources) {
        checkNotNull(resources);

        return new JaxRsApplicationImpl(ImmutableSet.copyOf(resources));
    }

    @Override
    public Set<Resource> getResources() {
        return null;
    }
}
