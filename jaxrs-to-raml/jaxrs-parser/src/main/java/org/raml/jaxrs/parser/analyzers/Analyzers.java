package org.raml.jaxrs.parser.analyzers;

import java.util.Set;

public class Analyzers {

    private Analyzers() {}

    public static Analyzer jerserAnalyzerFor(Iterable<Class<?>> classes) {
        return JerseyAnalyzer.create(classes, new JerseyBridgeImpl());
    }
}
