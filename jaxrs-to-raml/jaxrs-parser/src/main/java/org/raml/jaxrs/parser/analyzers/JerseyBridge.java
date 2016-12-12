package org.raml.jaxrs.parser.analyzers;

import com.google.common.collect.FluentIterable;

import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.server.model.RuntimeResource;

import java.util.List;

//For testing purposes mostly.
interface JerseyBridge {
    FluentIterable<Resource> resourcesFrom(Iterable<Class<?>> classes);
    List<RuntimeResource> runtimeResourcesFrom(FluentIterable<Resource> resources);
}
