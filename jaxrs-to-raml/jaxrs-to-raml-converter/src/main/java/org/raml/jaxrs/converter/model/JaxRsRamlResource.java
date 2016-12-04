package org.raml.jaxrs.converter.model;

import org.raml.jaxrs.model.JaxRsResource;
import org.raml.model.Resource;
import org.raml.model.ResourceMethod;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.raml.jaxrs.converter.model.Utilities.toRamlMethods;
import static org.raml.jaxrs.converter.model.Utilities.toRamlResources;

public class JaxRsRamlResource implements Resource {
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
    public List<Resource> getChildren() {
        return toRamlResources(this.resource.getChildren()).toList();
    }

    @Override
    public List<ResourceMethod> getMethods() {
        return toRamlMethods(this.resource.getMethods()).toList();
    }
}
