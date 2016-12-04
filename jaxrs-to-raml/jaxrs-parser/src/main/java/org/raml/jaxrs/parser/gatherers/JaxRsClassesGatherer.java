package org.raml.jaxrs.parser.gatherers;

import java.util.Set;

/**
 * An interface to define objects whose role is to extract the set
 * of JaxRs related classes.
 */
public interface JaxRsClassesGatherer {

    /**
     * @return The set of JaxRs related classes. How those are extracted and
     * from where depends entirely on the implementation.
     */
    Set<Class<?>> jaxRsClasses();
}
