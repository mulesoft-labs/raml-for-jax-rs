package org.raml.jaxrs.parser.gatherers;

import java.util.Set;

/**
 * A {@link Gatherer} is an object that will extract all
 * Jax RS resources from a given archive.
 */
public interface Gatherer {
    Set<Class<?>> gather();
}
