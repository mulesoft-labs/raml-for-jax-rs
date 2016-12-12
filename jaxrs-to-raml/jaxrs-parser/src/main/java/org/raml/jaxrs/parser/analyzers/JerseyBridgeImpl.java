package org.raml.jaxrs.parser.analyzers;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.server.model.RuntimeResource;
import org.glassfish.jersey.server.model.RuntimeResourceModel;

import java.util.List;

import javax.annotation.Nullable;

class JerseyBridgeImpl implements JerseyBridge {

    @Override
    public FluentIterable<Resource> resourcesFrom(Iterable<Class<?>> jaxRsClasses) {
        return FluentIterable.from(jaxRsClasses).transform(
                new Function<Class<?>, Resource>() {
                    @Nullable
                    @Override
                    public Resource apply(@Nullable Class<?> aClass) {
                        return Resource.from(aClass);
                    }
                }
        );
    }

    @Override
    public List<RuntimeResource> runtimeResourcesFrom(FluentIterable<Resource> resources) {
        RuntimeResourceModel resourceModel = new RuntimeResourceModel(resources.toList());
        return resourceModel.getRuntimeResources();
    }
}
