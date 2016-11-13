package org.raml.jaxrs.parser.analyzers;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.glassfish.jersey.server.model.Resource;
import org.raml.jaxrs.model.JaxRsApplication;
import org.raml.jaxrs.model.Path;
import org.raml.jaxrs.model.impl.JaxRsApplicationImpl;
import org.raml.jaxrs.model.impl.PathImpl;
import org.raml.jaxrs.model.impl.ResourceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

public class JerseyAnalyzer implements Analyzer {

    private static final Logger logger = LoggerFactory.getLogger(JerseyAnalyzer.class);

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
        Map<Path, List<Resource>> jerseyResourcesPerPath = Maps.newHashMap();

        logger.debug("about to analyze {} jersey resources", Iterables.size(jerseyResources));
        for (Resource resource : jerseyResources) {
            logger.debug("analyzing jersey resource: {}", resource);

            Path path = PathImpl.fromString(resource.getPath());

            if (jerseyResourcesPerPath.containsKey(path)) {
                jerseyResourcesPerPath.get(path).add(resource);
            } else {
                jerseyResourcesPerPath.put(path, Lists.newArrayList(resource));
            }
        }

        logger.debug("found {} unique resources", jerseyResourcesPerPath.size());

        return mergeResources(jerseyResourcesPerPath);
    }

    private static Iterable<org.raml.jaxrs.model.Resource> mergeResources(Map<Path, List<Resource>> jerseyResourcesPerPath) {
        List<org.raml.jaxrs.model.Resource> jaxRsResources = Lists.newArrayList();

        for (Map.Entry<Path, List<Resource>> entry : jerseyResourcesPerPath.entrySet()) {
            jaxRsResources.add(mergeResources(entry.getKey(), entry.getValue()));
        }

        return jaxRsResources;
    }

    private static org.raml.jaxrs.model.Resource mergeResources(Path key, List<Resource> value) {
        //TODO: implement this logic, like merging children and all that good stuff.

        return ResourceImpl.create(key);
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
