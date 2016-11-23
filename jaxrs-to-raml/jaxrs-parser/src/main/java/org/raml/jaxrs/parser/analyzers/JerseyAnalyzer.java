package org.raml.jaxrs.parser.analyzers;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.server.model.RuntimeResource;
import org.glassfish.jersey.server.model.RuntimeResourceModel;
import org.raml.jaxrs.model.JaxRsApplication;
import org.raml.jaxrs.model.JaxRsResource;
import org.raml.jaxrs.model.impl.JaxRsApplicationImpl;
import org.raml.jaxrs.parser.analyzers.runtime.OneToOneRuntimeResourceResolver;
import org.raml.utilities.format.Joiners;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

public class JerseyAnalyzer implements Analyzer {

    private static final Logger logger = LoggerFactory.getLogger(JerseyAnalyzer.class);

    private final ImmutableSet<Class<?>> jaxRsClasses;
    private final ResourceResolver<RuntimeResource> resourceResolver;

    private JerseyAnalyzer(ImmutableSet<Class<?>> jaxRsClasses, ResourceResolver<RuntimeResource> resourceResolver) {
        this.jaxRsClasses = jaxRsClasses;
        this.resourceResolver = resourceResolver;
    }

    public static JerseyAnalyzer withDefaultResolver(Iterable<Class<?>> classes) {
        return new JerseyAnalyzer(ImmutableSet.copyOf(classes), OneToOneRuntimeResourceResolver.create());
    }

    @Override
    public JaxRsApplication analyze() {
        logger.debug("analyzing...");

        List<Resource> jerseyResources = resourcesFor(jaxRsClasses);

        if (logger.isDebugEnabled()) {
            logger.debug("found jersey resources: \n{}", Joiners.squareBracketsPerLineJoiner().join(jerseyResources));
        }

        RuntimeResourceModel resourceModel = new RuntimeResourceModel(jerseyResources);
        List<RuntimeResource> runtimeResources = resourceModel.getRuntimeResources();

        if (logger.isDebugEnabled()) {
            logger.debug("found runtime resources: \n{}", Joiners.squareBracketsPerLineJoiner().join(runtimeResources));
        }

        Iterable<JaxRsResource> ourResources = resourceResolver.resolve(runtimeResources);

        return JaxRsApplicationImpl.create(ourResources);

    }

    private static List<Resource> resourcesFor(Set<Class<?>> jaxRsClasses) {
        return FluentIterable.from(jaxRsClasses).transform(
                new Function<Class<?>, Resource>() {
                    @Nullable
                    @Override
                    public Resource apply(@Nullable Class<?> aClass) {
                        return Resource.from(aClass);
                    }
                }
        ).toList();
    }
}
