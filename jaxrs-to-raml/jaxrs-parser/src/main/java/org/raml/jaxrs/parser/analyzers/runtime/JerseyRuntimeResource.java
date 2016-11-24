package org.raml.jaxrs.parser.analyzers.runtime;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

import org.glassfish.jersey.server.model.ResourceMethod;
import org.glassfish.jersey.server.model.RuntimeResource;
import org.raml.jaxrs.model.HttpVerb;
import org.raml.jaxrs.model.JaxRsResource;
import org.raml.jaxrs.model.Method;
import org.raml.jaxrs.model.Path;
import org.raml.jaxrs.model.impl.PathImpl;
import org.raml.jaxrs.parser.model.JerseyJaxRsMethod;

import java.util.List;

import javax.annotation.Nullable;

import static jersey.repackaged.com.google.common.base.Preconditions.checkNotNull;

class JerseyRuntimeResource implements JaxRsResource {

    private final RuntimeResource runtimeResource;

    private JerseyRuntimeResource(RuntimeResource runtimeResource) {
        this.runtimeResource = runtimeResource;
    }

    static JaxRsResource from(RuntimeResource runtimeResource) {
        checkNotNull(runtimeResource);

        return new JerseyRuntimeResource(runtimeResource);
    }

    @Override
    public Path getPath() {
        return PathImpl.fromString(runtimeResource.getRegex());
    }

    @Override
    public List<Method> getMethods() {
        return FluentIterable.from(runtimeResource.getResourceMethods()).transform(
                new Function<ResourceMethod, Method>() {
                    @Nullable
                    @Override
                    public Method apply(@Nullable ResourceMethod resourceMethod) {
                        return ourMethodOf(resourceMethod);
                    }
                }
        ).toList();
    }

    private static Method ourMethodOf(ResourceMethod resourceMethod) {
        return JerseyJaxRsMethod.create(resourceMethod);
    }

    @Override
    public List<JaxRsResource> getChildren() {
        return FluentIterable.from(runtimeResource.getChildRuntimeResources()).transform(
                new Function<RuntimeResource, JaxRsResource>() {
                    @Nullable
                    @Override
                    public JaxRsResource apply(@Nullable RuntimeResource runtimeResource) {
                        return from(runtimeResource);
                    }
                }
        ).toList();
    }
}
