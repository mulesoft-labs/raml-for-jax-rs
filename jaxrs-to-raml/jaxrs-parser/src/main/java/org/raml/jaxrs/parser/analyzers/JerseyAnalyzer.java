package org.raml.jaxrs.parser.analyzers;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import org.glassfish.jersey.server.model.Resource;
import org.raml.jaxrs.model.JaxRsApplication;
import org.raml.jaxrs.model.impl.JaxRsApplicationImpl;

import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

public class JerseyAnalyzer implements Analyzer {

    private final Set<Class<?>> jaxRsClasses;

    private JerseyAnalyzer(Set<Class<?>> jaxRsClasses) {
        this.jaxRsClasses = jaxRsClasses;
    }

    public static JerseyAnalyzer create(Iterable<Class<?>> classes) {
        checkNotNull(classes);

        return new JerseyAnalyzer(ImmutableSet.copyOf(classes));
    }

    @Override
    public JaxRsApplication analyze() {
        Iterable<Resource> jerseyResources = resourcesFor(jaxRsClasses);

        Iterable<org.raml.jaxrs.model.Resource> ourResources = jerseyResourcesToOurs(jerseyResources);

        return JaxRsApplicationImpl.create(ourResources);

    }

    private static Iterable<org.raml.jaxrs.model.Resource> jerseyResourcesToOurs(Iterable<Resource> jerseyResources) {
        return Iterables.transform(
                jerseyResources,
                new Function<Resource, org.raml.jaxrs.model.Resource>() {
                    @Nullable
                    @Override
                    public org.raml.jaxrs.model.Resource apply(@Nullable Resource resource) {
                        return org.raml.jaxrs.model.impl.ResourceImpl.create(resource.getPath());
                    }
                }
        );
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
