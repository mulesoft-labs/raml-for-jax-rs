package org.raml.jaxrs.parser.providers;


import com.google.common.net.MediaType;

public interface EnityProvider {
    Iterable<MediaType> getMediaTypes();
}
