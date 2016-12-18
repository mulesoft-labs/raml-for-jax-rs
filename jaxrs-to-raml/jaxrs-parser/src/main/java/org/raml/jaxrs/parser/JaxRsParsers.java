package org.raml.jaxrs.parser;

import com.google.common.base.Optional;

import org.raml.utilities.builder.NonNullableField;

import java.nio.file.Path;

public class JaxRsParsers {

    private JaxRsParsers() {}

    public static JaxRsParser usingJerseyForPaths(Path classesPath, NonNullableField<Path> sourceDirectoryRoot) {
        return JerseyJaxRsParser.create(classesPath, sourceDirectoryRoot);
    }
}
