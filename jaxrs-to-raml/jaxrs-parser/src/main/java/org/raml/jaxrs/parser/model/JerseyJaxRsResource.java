package org.raml.jaxrs.parser.model;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

import org.glassfish.jersey.server.model.ResourceMethod;
import org.glassfish.jersey.server.model.RuntimeResource;
import org.raml.jaxrs.model.JaxRsResource;
import org.raml.jaxrs.model.Method;
import org.raml.jaxrs.model.Path;

import java.util.List;

import javax.annotation.Nullable;

import static jersey.repackaged.com.google.common.base.Preconditions.checkNotNull;

public class JerseyJaxRsResource implements JaxRsResource {

    private final RuntimeResource runtimeResource;

    private JerseyJaxRsResource(RuntimeResource runtimeResource) {
        this.runtimeResource = runtimeResource;
    }

    public static JaxRsResource create(RuntimeResource runtimeResource) {
        checkNotNull(runtimeResource);

        return new JerseyJaxRsResource(runtimeResource);
    }

    @Override
    public Path getPath() {
        return JerseyJaxRsPath.fromRuntimeResource(runtimeResource);
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
                        return create(runtimeResource);
                    }
                }
        ).toList();
    }
}
