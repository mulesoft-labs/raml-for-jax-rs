package org.raml.jaxrs.converter.model;

import org.raml.api.RamlMediaType;

import static com.google.common.base.Preconditions.checkNotNull;

public class JaxRsRamlMediaType implements RamlMediaType {
    private final javax.ws.rs.core.MediaType mediaType;

    private JaxRsRamlMediaType(javax.ws.rs.core.MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public static JaxRsRamlMediaType create(javax.ws.rs.core.MediaType mediaType) {
        checkNotNull(mediaType);

        return new JaxRsRamlMediaType(mediaType);
    }

    @Override
    public String toStringRepresentation() {
        return mediaType.toString();
    }

    @Override
    public String toString() {
        return toStringRepresentation();
    }
}
