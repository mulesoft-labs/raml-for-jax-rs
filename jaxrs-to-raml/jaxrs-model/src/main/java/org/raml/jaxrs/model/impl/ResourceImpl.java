package org.raml.jaxrs.model.impl;

import org.raml.jaxrs.model.Method;
import org.raml.jaxrs.model.Path;
import org.raml.jaxrs.model.Resource;

import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public class ResourceImpl implements Resource {

    private final Path path;

    private ResourceImpl(Path path) {
        this.path = path;
    }

    public static ResourceImpl create(Path path) {
        checkNotNull(path);

        return new ResourceImpl(path);
    }

    @Override
    public Path getPath() {
        return path;
    }

    @Override
    public Set<Method> getMethods() {
        throw new UnsupportedOperationException("unimplemented yet thank you very much");
    }
}
