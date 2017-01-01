package org.raml.jaxrs.parser.source;

import com.google.common.base.Optional;

import java.lang.reflect.Method;

public interface SourceParser {
    Optional<String> getDocumentationFor(Method method);
}
