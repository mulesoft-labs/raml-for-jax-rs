package org.raml.api;

import java.util.List;

public interface RamlApi {
    String getTitle();
    String getVersion();
    String getBaseUri();
    List<RamlResource> getResources();
    RamlMediaType getDefaultMediaType();
}
