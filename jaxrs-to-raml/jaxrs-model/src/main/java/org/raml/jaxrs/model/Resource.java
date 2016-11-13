package org.raml.jaxrs.model;

import java.util.Set;

public interface Resource {
    Path getPath();
    Set<Method> getMethods();
}
