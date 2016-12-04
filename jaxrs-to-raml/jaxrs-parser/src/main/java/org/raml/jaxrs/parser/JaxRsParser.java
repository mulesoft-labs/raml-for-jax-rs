package org.raml.jaxrs.parser;

import org.raml.jaxrs.model.JaxRsApplication;

import java.nio.file.Path;

public interface JaxRsParser {
    JaxRsApplication parse(Path jaxRsResource) throws JaxRsParsingException;
}
