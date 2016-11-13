package org.raml.jaxrs.model.impl;

import com.google.common.collect.ImmutableList;

import org.raml.jaxrs.model.Method;
import org.raml.jaxrs.model.Path;
import org.raml.jaxrs.model.Resource;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public class ResourceImpl implements Resource {

    private final Path path;

    private final ImmutableList<Resource> children;

    private ResourceImpl(Path path, ImmutableList<Resource> children) {
        this.path = path;
        this.children = children;
    }

    public static ResourceImpl create(Path path, Iterable<Resource> children) {
        checkNotNull(path);

        return new ResourceImpl(path, ImmutableList.copyOf(checkChildren(children)));
    }

    private static Iterable<Resource> checkChildren(Iterable<Resource> children) {
        for (Resource child : children) {
            checkNotNull(child, "cannot have null child");
        }
        return children;
    }

    @Override
    public Path getPath() {
        return path;
    }

    @Override
    public Set<Method> getMethods() {
        throw new UnsupportedOperationException("unimplemented yet thank you very much");
    }

    @Override
    public List<Resource> getChildren() {
        return null;
    }
}
