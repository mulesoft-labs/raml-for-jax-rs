package org.raml.jaxrs.parser;

public class JaxRsParsers {

    private JaxRsParsers() {}

    public static JaxRsParser usingJersey() {
        return JerseyJaxRsParser.create();
    }
}
