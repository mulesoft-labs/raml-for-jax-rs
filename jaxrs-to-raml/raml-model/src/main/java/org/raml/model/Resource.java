package org.raml.model;

import java.lang.reflect.Method;
import java.util.List;

public interface Resource {
    String getPath();
    List<Resource> getChildren();
    List<HttpMethod> getMethods();
}
