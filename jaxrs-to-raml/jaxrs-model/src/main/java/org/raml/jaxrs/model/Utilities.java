package org.raml.jaxrs.model;

import static com.google.common.base.Preconditions.checkNotNull;

public class Utilities {

    /**
     * @param path The path to format
     * @return The same path but starting with "/" and ending without "/" if that is not already
     * the case.
     */
    public static String uniformizePath(String path) {
        checkNotNull(path);

        String result = withStartingSlash(path);

        return withoutEndingSlash(result);
    }

    private static String withStartingSlash(String path) {
        return path.startsWith("/") ? path : "/" + path;
    }

    private static String withoutEndingSlash(String result) {
        return result.endsWith("/") ? result.substring(0, result.length() - 1) : result;
    }
}
