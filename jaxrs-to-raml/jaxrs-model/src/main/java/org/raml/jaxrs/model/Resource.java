package org.raml.jaxrs.model;

import java.util.Set;

public interface Resource {
    String getPath();
    Set<Method> getMethods();
}
