package org.raml.jaxrs.converter.model;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

import org.raml.model.MediaType;

public class Utilities {

    public static FluentIterable<MediaType> toRamlMediaTypes(Iterable<javax.ws.rs.core.MediaType> mediaTypes) {
        return FluentIterable.from(mediaTypes).transform(
                new Function<javax.ws.rs.core.MediaType, MediaType>() {
                    @Override
                    public MediaType apply(javax.ws.rs.core.MediaType mediaType) {
                        return JaxRsRamlMediaType.create(mediaType);
                    }
                }
        );
    }
}
