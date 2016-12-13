package org.raml.jaxrs.parser;

import java.nio.file.Path;

public class JaxRsParsers {

    private JaxRsParsers() {}

    public static JaxRsParser usingJerseyForPath(Path classesPath) {
        return JerseyJaxRsParser.create(classesPath);
    }
}
