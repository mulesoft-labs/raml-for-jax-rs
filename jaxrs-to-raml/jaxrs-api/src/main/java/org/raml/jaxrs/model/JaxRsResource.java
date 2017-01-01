package org.raml.jaxrs.model;

import java.util.List;

public interface JaxRsResource {
    Path getPath();
    List<JaxRsMethod> getMethods();
    List<JaxRsResource> getChildren();
}
