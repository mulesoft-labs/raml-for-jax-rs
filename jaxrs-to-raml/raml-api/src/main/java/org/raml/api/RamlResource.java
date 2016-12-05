package org.raml.api;

import java.util.List;

public interface RamlResource {
    String getPath();
    List<RamlResource> getChildren();
    List<RamlResourceMethod> getMethods();
}
