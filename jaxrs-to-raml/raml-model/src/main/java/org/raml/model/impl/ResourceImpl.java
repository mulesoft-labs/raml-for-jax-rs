package org.raml.model.impl;

import org.raml.model.Resource;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class ResourceImpl implements Resource {
    private final String path;

    private ResourceImpl(String path) {
        this.path = path;
    }

    public static ResourceImpl create(String path) {
        checkNotNull(path);
        checkArgument(!path.trim().isEmpty(), "resource path should contain one meaningful character at least");

        if (!path.startsWith("/")) {
            path = "/" + path;
        }

        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        return new ResourceImpl(path);
    }

    @Override
    public String getPath() {
        return this.path;
    }
}
