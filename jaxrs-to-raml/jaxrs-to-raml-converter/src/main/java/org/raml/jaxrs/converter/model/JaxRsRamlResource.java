package org.raml.jaxrs.converter.model;

import org.raml.jaxrs.model.JaxRsResource;
import org.raml.api.RamlResource;
import org.raml.api.RamlResourceMethod;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.raml.jaxrs.converter.model.Utilities.toRamlMethods;
import static org.raml.jaxrs.converter.model.Utilities.toRamlResources;

public class JaxRsRamlResource implements RamlResource {
    private final JaxRsResource resource;

    private JaxRsRamlResource(JaxRsResource resource) {
        this.resource = resource;
    }

    public static JaxRsRamlResource create(JaxRsResource resource) {
        checkNotNull(resource);

        return new JaxRsRamlResource(resource);
    }

    @Override
    public String getPath() {
        return resource.getPath().getStringRepresentation();
    }

    @Override
    public List<RamlResource> getChildren() {
        return toRamlResources(this.resource.getChildren()).toList();
    }

    @Override
    public List<RamlResourceMethod> getMethods() {
        return toRamlMethods(this.resource.getMethods()).toList();
    }
}
