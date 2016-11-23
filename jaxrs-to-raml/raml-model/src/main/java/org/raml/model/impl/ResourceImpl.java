package org.raml.model.impl;

import com.google.common.collect.ImmutableList;

import org.raml.model.HttpMethod;
import org.raml.model.Resource;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class ResourceImpl implements Resource {
    private final String path;
    private final ImmutableList<Resource> children;
    private final ImmutableList<HttpMethod> methods;

    private ResourceImpl(String path, ImmutableList<Resource> children, ImmutableList<HttpMethod> methods) {
        this.path = path;
        this.children = children;
        this.methods = methods;
    }

    public static ResourceImpl create(String path, Iterable<Resource> children, Iterable<HttpMethod> methods) {
        checkNotNull(path);
        checkArgument(!path.trim().isEmpty(), "resource path should contain one meaningful character at least");
        checkNotNull(children);
        checkNotNull(methods);

        //TODO: use the utility to format path in JaxRsResource
        if (!path.startsWith("/")) {
            path = "/" + path;
        }

        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        return new ResourceImpl(path, ImmutableList.copyOf(children), ImmutableList.copyOf(methods));
    }

    @Override
    public String getPath() {
        return this.path;
    }

    @Override
    public List<Resource> getChildren() {
        return this.children;
    }

    @Override
    public List<HttpMethod> getMethods() {
        return this.methods;
    }
}
