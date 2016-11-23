package org.raml.jaxrs.parser.analyzers;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import org.glassfish.jersey.server.model.Resource;
import org.raml.jaxrs.model.JaxRsApplication;
import org.raml.jaxrs.model.JaxRsResource;
import org.raml.jaxrs.model.impl.JaxRsApplicationImpl;
import org.raml.utilities.format.Joiners;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

public class JerseyAnalyzer implements Analyzer {

    private static final Logger logger = LoggerFactory.getLogger(JerseyAnalyzer.class);

    private final Set<Class<?>> jaxRsClasses;
    private final ResourceResolver<Resource> resourceResolver;

    private JerseyAnalyzer(Set<Class<?>> jaxRsClasses, ResourceResolver<Resource> resourceResolver) {
        this.jaxRsClasses = jaxRsClasses;
        this.resourceResolver = resourceResolver;
    }

    public static JerseyAnalyzer withDefaultResolver(Iterable<Class<?>> classes) {
        return withResolver(classes, OneToOneResourceResolver.create());
    }

    public static JerseyAnalyzer withResolver(Iterable<Class<?>> classes, ResourceResolver<Resource> resourceResolver) {
        checkNotNull(classes);
        checkNotNull(resourceResolver);

        if (logger.isDebugEnabled()) {
            logger.debug("creating {} with classes: \n{}\nand resource resolver: {}",
                    JerseyAnalyzer.class.getSimpleName(), Joiners.squareBracketsPerLineJoiner().join(classes), resourceResolver);
        }

        return new JerseyAnalyzer(ImmutableSet.copyOf(classes), resourceResolver);
    }

    @Override
    public JaxRsApplication analyze() {
        Iterable<Resource> jerseyResources = resourcesFor(jaxRsClasses);

        Iterable<JaxRsResource> ourResources = resourceResolver.resolve(jerseyResources);

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
