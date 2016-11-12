package org.raml.jaxrs.parser;

import org.raml.jaxrs.model.JaxRsApplication;
import org.raml.jaxrs.parser.analyzers.JerseyAnalyzer;
import org.raml.jaxrs.parser.gatherers.JerseyGatherer;

import java.nio.file.Path;

public class JaxRsParser {

    private JaxRsParser() {}

    public static JaxRsParser create() {
        return new JaxRsParser();
    }

    public JaxRsApplication parse(Path jaxRsResource) throws JaxRsParsingException {
        return JerseyAnalyzer.create(JerseyGatherer.forApplication(jaxRsResource).jaxRsClasses()).analyze();
    }
}
