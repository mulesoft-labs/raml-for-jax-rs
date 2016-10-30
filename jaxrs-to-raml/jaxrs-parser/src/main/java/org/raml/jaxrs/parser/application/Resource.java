package org.raml.jaxrs.parser.application;

import java.util.Set;

public interface Resource {
    Path getPath();
    Set<Method> getMethods();
}
