package org.raml.jaxrs.model;

import java.util.List;
import java.util.Set;

public interface JaxRsResource {
    Path getPath();
    List<Method> getMethods();
    List<JaxRsResource> getChildren();
}
