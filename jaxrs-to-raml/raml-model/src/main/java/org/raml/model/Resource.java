package org.raml.model;

import java.util.List;

public interface Resource {
    String getPath();
    List<Resource> getChildren();
    List<ResourceMethod> getMethods();
}
