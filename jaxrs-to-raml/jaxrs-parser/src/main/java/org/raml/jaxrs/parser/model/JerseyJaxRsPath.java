package org.raml.jaxrs.parser.model;


import org.glassfish.jersey.server.model.RuntimeResource;

import static com.google.common.base.Preconditions.checkNotNull;

class JerseyJaxRsPath implements org.raml.jaxrs.model.Path {
    private final RuntimeResource runtimeResource;

    private JerseyJaxRsPath(RuntimeResource runtimeResource) {
        this.runtimeResource = runtimeResource;
    }

    public static JerseyJaxRsPath fromRuntimeResource(RuntimeResource runtimeResource) {
        checkNotNull(runtimeResource);

        return new JerseyJaxRsPath(runtimeResource);
    }

    @Override
    public String getStringRepresentation() {
        return runtimeResource.getPathPattern().getTemplate().getTemplate();
    }
}
