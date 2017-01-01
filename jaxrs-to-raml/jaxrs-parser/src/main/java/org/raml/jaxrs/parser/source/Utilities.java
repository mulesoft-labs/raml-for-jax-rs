package org.raml.jaxrs.parser.source;

import com.google.common.base.Splitter;

import java.nio.file.Path;
import java.nio.file.Paths;

class Utilities {

    public static Path getSourceFileRelativePath(Class<?> declaringClass) {
        Path packageRelativePath = transformIntoPath(declaringClass);
        String declaringFileName = getSourceFileName(declaringClass);
        return packageRelativePath.resolve(declaringFileName);
    }

    private static String getSourceFileName(Class<?> declaringClass) {
        final String SOURCE_FILE_EXTENSION = ".java";
        return declaringClass.getSimpleName() + SOURCE_FILE_EXTENSION;
    }

    private static Path transformIntoPath(Class<?> clazz) {
        Splitter splitter = Splitter.on('.').omitEmptyStrings();
        Iterable<String> subPackages = splitter.split(clazz.getPackage().getName());

        Path current = Paths.get("");
        for (String subPackage : subPackages) {
            current = current.resolve(subPackage);
        }

        return current;
    }
}
