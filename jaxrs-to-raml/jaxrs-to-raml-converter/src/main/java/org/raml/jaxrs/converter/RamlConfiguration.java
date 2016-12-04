package org.raml.jaxrs.converter;

import org.raml.model.MediaType;

public interface RamlConfiguration {
    String getTitle();
    String getBaseUri();
    String getVersion();
    MediaType getDefaultMediaType();
}
