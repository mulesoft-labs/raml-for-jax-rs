package org.raml.jaxrs.parser.analyzers;

import org.raml.jaxrs.model.JaxRsResource;

public interface ResourceResolver<T> {
    Iterable<JaxRsResource> resolve(Iterable<T> resources);
}
