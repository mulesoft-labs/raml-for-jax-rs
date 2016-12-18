package org.raml.jaxrs.parser.source;

import java.nio.file.Path;

import static com.google.common.base.Preconditions.checkNotNull;

public class JavaParserSourceParser implements SourceParser {
    private final Path sourceRootDirectory;

    private JavaParserSourceParser(Path sourceRootDirectory) {
        this.sourceRootDirectory = sourceRootDirectory;
    }

    public static JavaParserSourceParser create(Path sourceRootDirectory) {
        checkNotNull(sourceRootDirectory);

        return new JavaParserSourceParser(sourceRootDirectory);
    }
}
