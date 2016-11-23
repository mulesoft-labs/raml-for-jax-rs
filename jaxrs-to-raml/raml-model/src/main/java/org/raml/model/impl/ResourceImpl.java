package org.raml.model.impl;

import com.google.common.collect.ImmutableList;

import org.raml.model.Resource;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class ResourceImpl implements Resource {
    private final String path;
    private final List<Resource> children;

    private ResourceImpl(String path, List<Resource> children) {
        this.path = path;
        this.children = children;
    }

    public static ResourceImpl create(String path, Iterable<Resource> children) {
        checkNotNull(path);
        checkArgument(!path.trim().isEmpty(), "resource path should contain one meaningful character at least");
        checkNotNull(children);

        //TODO: use the utility to format path in JaxRsResource
        if (!path.startsWith("/")) {
            path = "/" + path;
        }

        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        return new ResourceImpl(path, ImmutableList.copyOf(children));
    }

    @Override
    public String getPath() {
        return this.path;
    }

    @Override
    public List<Resource> getChildren() {
        return this.children;
    }
}
