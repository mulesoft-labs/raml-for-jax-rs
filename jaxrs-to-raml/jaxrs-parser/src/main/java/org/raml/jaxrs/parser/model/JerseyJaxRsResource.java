package org.raml.jaxrs.parser.model;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

import org.glassfish.jersey.server.model.ResourceMethod;
import org.glassfish.jersey.server.model.RuntimeResource;
import org.raml.jaxrs.model.JaxRsResource;
import org.raml.jaxrs.model.JaxRsMethod;
import org.raml.jaxrs.model.Path;
import org.raml.jaxrs.parser.source.SourceParser;

import java.util.List;

import javax.annotation.Nullable;

import static jersey.repackaged.com.google.common.base.Preconditions.checkNotNull;

class JerseyJaxRsResource implements JaxRsResource {

    private final RuntimeResource runtimeResource;
    private final SourceParser sourceParser;

    private JerseyJaxRsResource(RuntimeResource runtimeResource, SourceParser sourceParser) {
        this.runtimeResource = runtimeResource;
        this.sourceParser = sourceParser;
    }

    public static JaxRsResource create(RuntimeResource runtimeResource, SourceParser sourceParser) {
        checkNotNull(runtimeResource);
        checkNotNull(sourceParser);

        return new JerseyJaxRsResource(runtimeResource, sourceParser);
    }

    @Override
    public Path getPath() {
        return JerseyJaxRsPath.fromRuntimeResource(runtimeResource);
    }

    @Override
    public List<JaxRsMethod> getMethods() {
        return FluentIterable.from(runtimeResource.getResourceMethods()).transform(
                new Function<ResourceMethod, JaxRsMethod>() {
                    @Nullable
                    @Override
                    public JaxRsMethod apply(@Nullable ResourceMethod resourceMethod) {
                        return ourMethodOf(resourceMethod, sourceParser);
                    }
                }
        ).toList();
    }

    @Override
    public List<JaxRsResource> getChildren() {
        return FluentIterable.from(runtimeResource.getChildRuntimeResources()).transform(
                new Function<RuntimeResource, JaxRsResource>() {
                    @Nullable
                    @Override
                    public JaxRsResource apply(@Nullable RuntimeResource runtimeResource) {
                        return create(runtimeResource, sourceParser);
                    }
                }
        ).toList();
    }

    private static JaxRsMethod ourMethodOf(ResourceMethod resourceMethod, SourceParser sourceParser) {
        return JerseyJaxRsMethod.create(resourceMethod, sourceParser);
    }
}
