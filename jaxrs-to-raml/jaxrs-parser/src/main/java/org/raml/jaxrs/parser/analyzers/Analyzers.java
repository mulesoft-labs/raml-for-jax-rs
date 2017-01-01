package org.raml.jaxrs.parser.analyzers;

import org.raml.jaxrs.parser.source.SourceParser;

public class Analyzers {

    private Analyzers() {}

    public static Analyzer jerserAnalyzerFor(Iterable<Class<?>> classes, SourceParser sourceParser) {
        return JerseyAnalyzer.create(classes, new JerseyBridgeImpl(), sourceParser);
    }
}
