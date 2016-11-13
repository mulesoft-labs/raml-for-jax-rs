package org.raml.model;

import java.util.List;

public interface RamlApi {
    String getTitle();
    String getVersion();
    String getBaseUri();
    List<Resource> getResources();
}
