package org.raml.jaxrs.parser.analyzers;

import org.raml.jaxrs.model.JaxRsApplication;

/**
 * {@link Analyzer}s are classes that are meant to make sense of a given configuration.
 *
 * Meaning, for example, an analyzer is meant to construct a {@link JaxRsApplication} from
 * a set of files, or classes.
 */
public interface Analyzer {

    /**
     * @return A {@link JaxRsApplication} constructed by the specified implementation configuration.
     */
    JaxRsApplication analyze();
}
