package org.raml.jaxrs.parser.analyzers;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;

import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.server.model.RuntimeResource;
import org.raml.jaxrs.model.JaxRsApplication;
import org.raml.jaxrs.parser.model.JerseyJaxRsApplication;
import org.raml.utilities.format.Joiners;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link Analyzer} implementation leveraging Jersey code to extract a JaxRsApplication
 * from a set of {@link Class} that contain JAX-RS code.
 */
class JerseyAnalyzer implements Analyzer {

    private static final Logger logger = LoggerFactory.getLogger(JerseyAnalyzer.class);

    private final ImmutableSet<Class<?>> jaxRsClasses;
    private final JerseyBridge jerseyBridge;

    private JerseyAnalyzer(ImmutableSet<Class<?>> jaxRsClasses, JerseyBridge jerseyBridge) {
        this.jaxRsClasses = jaxRsClasses;
        this.jerseyBridge = jerseyBridge;
    }

    static JerseyAnalyzer create(Iterable<Class<?>> classes, JerseyBridge jerseyBridge) {
        checkNotNull(classes);
        checkNotNull(jerseyBridge);

        return new JerseyAnalyzer(ImmutableSet.copyOf(classes), jerseyBridge);
    }

    @Override
    public JaxRsApplication analyze() {
        logger.debug("analyzing...");

        //The first step is to extract the Jersey resources from the classes.
        FluentIterable<Resource> jerseyResources = jerseyBridge.resourcesFrom(jaxRsClasses);

        if (logger.isDebugEnabled()) {
            logger.debug("found jersey resources: \n{}", Joiners.squareBracketsPerLineJoiner().join(jerseyResources));
        }

        //We then transform them into what they call RuntimeResources, which are basically
        //the resolved resources.
        List<RuntimeResource> runtimeResources = jerseyBridge.runtimeResourcesFrom(jerseyResources);

        if (logger.isDebugEnabled()) {
            logger.debug("found runtime resources: \n{}", Joiners.squareBracketsPerLineJoiner().join(runtimeResources));
        }

        return JerseyJaxRsApplication.fromRuntimeResources(runtimeResources);
    }
}
