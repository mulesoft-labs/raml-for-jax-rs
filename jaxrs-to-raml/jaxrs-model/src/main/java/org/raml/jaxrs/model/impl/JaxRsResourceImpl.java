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

    private JaxRsResourceImpl(Path path, ImmutableList<JaxRsResource> children) {
        this.path = path;
        this.children = children;
    }

    public static JaxRsResourceImpl create(Path path, Iterable<JaxRsResource> children) {
        checkNotNull(path);
        checkNotNull(children);


        return new JaxRsResourceImpl(path, ImmutableList.copyOf(checkChildren(children)));
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
    public Set<Method> getMethods() {
        throw new UnsupportedOperationException("unimplemented yet thank you very much");
    }

    @Override
    public List<JaxRsResource> getChildren() {
        return null;
    }
}
