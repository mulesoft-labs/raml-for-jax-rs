package org.raml.jaxrs.parser.analyzers;

import org.raml.jaxrs.model.JaxRsApplication;

public interface Analyzer {
    JaxRsApplication analyze();
}
