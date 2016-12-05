package org.raml.jaxrs.converter;

import org.raml.api.RamlMediaType;

public interface RamlConfiguration {
    String getTitle();
    String getBaseUri();
    String getVersion();
    RamlMediaType getDefaultMediaType();
}
