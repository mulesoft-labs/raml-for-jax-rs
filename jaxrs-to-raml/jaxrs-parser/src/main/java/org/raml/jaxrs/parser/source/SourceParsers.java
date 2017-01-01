package org.raml.jaxrs.parser.source;

import com.google.common.base.Optional;

import java.lang.reflect.Method;
import java.nio.file.Path;

public class SourceParsers {

    private static final SourceParser NO_OP_PARSER = new SourceParser() {
        @Override
        public Optional<String> getDocumentationFor(Method method) {
            return Optional.absent(); //Do nothing on purpose.
        }
    };

    public static SourceParser usingRoasterParser(Path path) {
        return RoasterSourceParser.create(path);
    }

    public static SourceParser nullParser() {
        return NO_OP_PARSER;
    }
}
