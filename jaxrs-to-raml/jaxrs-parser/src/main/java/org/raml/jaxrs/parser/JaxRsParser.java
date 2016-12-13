package org.raml.jaxrs.parser;

import org.raml.jaxrs.model.JaxRsApplication;

import java.nio.file.Path;

/**
 * A parser that parser the {@link JaxRsApplication} found using the given
 * implementation configuration.
 */
public interface JaxRsParser {
    JaxRsApplication parse() throws JaxRsParsingException;
}
