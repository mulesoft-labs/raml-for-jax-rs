package org.raml.jaxrs.model.impl;

import com.google.common.collect.ImmutableList;

import org.raml.jaxrs.model.JaxRsResource;
import org.raml.jaxrs.model.Method;
import org.raml.jaxrs.model.Path;

import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public class JaxRsResourceImpl implements JaxRsResource {

    private final Path path;
    private final ImmutableList<JaxRsResource> children;
    private final ImmutableList<Method> methods;

    private JaxRsResourceImpl(Path path, ImmutableList<JaxRsResource> children, ImmutableList<Method> methods) {
        this.path = path;
        this.children = children;
        this.methods = methods;
    }

    public static JaxRsResourceImpl create(Path path, Iterable<JaxRsResource> children, Iterable<Method> methods) {
        checkNotNull(path);
        checkNotNull(children);
        checkNotNull(methods);

        return new JaxRsResourceImpl(path, ImmutableList.copyOf(checkChildren(children)), ImmutableList.copyOf(methods));
    }

    private static Iterable<JaxRsResource> checkChildren(Iterable<JaxRsResource> children) {
        for (JaxRsResource child : children) {
            checkNotNull(child, "cannot have null child");
        }
        return children;
    }

    @Override
    public Path getPath() {
        return path;
    }

    @Override
    public List<Method> getMethods() {
        return this.methods;
    }

    @Override
    public List<JaxRsResource> getChildren() {
        return children;
    }
}
