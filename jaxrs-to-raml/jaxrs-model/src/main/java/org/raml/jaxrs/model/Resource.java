package org.raml.jaxrs.model;

import java.util.List;
import java.util.Set;

public interface Resource {
    Path getPath();
    Set<Method> getMethods();
    List<Resource> getChildren();
}
