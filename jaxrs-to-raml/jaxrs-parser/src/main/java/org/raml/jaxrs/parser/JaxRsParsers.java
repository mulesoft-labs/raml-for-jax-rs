package org.raml.jaxrs.parser;

import org.raml.jaxrs.parser.source.SourceParser;

import java.nio.file.Path;

public class JaxRsParsers {

    private JaxRsParsers() {}

    public static JaxRsParser usingJerseyWith(Path classesPath, SourceParser sourceParser) {
        return JerseyJaxRsParser.create(classesPath, sourceParser);
    }
}
