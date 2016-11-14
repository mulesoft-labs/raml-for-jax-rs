package org.raml.jaxrs.parser.analyzers;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import org.glassfish.jersey.server.model.Resource;
import org.raml.jaxrs.model.JaxRsApplication;
import org.raml.jaxrs.model.JaxRsResource;
import org.raml.jaxrs.model.impl.JaxRsApplicationImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

public class JerseyAnalyzer implements Analyzer {

    private static final Logger logger = LoggerFactory.getLogger(JerseyAnalyzer.class);

    private final Set<Class<?>> jaxRsClasses;
    private final JerseyResourceResolver resourceResolver;

    private JerseyAnalyzer(Set<Class<?>> jaxRsClasses, JerseyResourceResolver resourceResolver) {
        this.jaxRsClasses = jaxRsClasses;
        this.resourceResolver = resourceResolver;
    }

    public static JerseyAnalyzer create(Iterable<Class<?>> classes) {
        checkNotNull(classes);

        return new JerseyAnalyzer(ImmutableSet.copyOf(classes), JerseyResourceResolver.create());
    }

    @Override
    public JaxRsApplication analyze() {
        Iterable<Resource> jerseyResources = resourcesFor(jaxRsClasses);

        Iterable<JaxRsResource> ourResources = resourceResolver.fromJerseyToOurResources(jerseyResources);

        return JaxRsApplicationImpl.create(ourResources);

    }

    private static Iterable<Resource> resourcesFor(Set<Class<?>> jaxRsClasses) {
        return Iterables.transform(
                jaxRsClasses,
                new Function<Class<?>, Resource>() {
                    @Nullable
                    @Override
                    public Resource apply(@Nullable Class<?> aClass) {
                        return Resource.from(aClass);
                    }
                }
        );
    }
}
