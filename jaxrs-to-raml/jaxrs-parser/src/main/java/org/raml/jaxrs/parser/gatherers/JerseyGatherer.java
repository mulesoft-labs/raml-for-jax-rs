package org.raml.jaxrs.parser.gatherers;

import com.google.common.annotations.VisibleForTesting;

import org.glassfish.jersey.server.ResourceConfig;
import org.raml.jaxrs.parser.util.ClassLoaderUtils;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class JerseyGatherer implements JaxRsClassesGatherer {

    private final ResourceConfig resourceConfig;

    @VisibleForTesting
    JerseyGatherer(ResourceConfig resourceConfig) {
        this.resourceConfig = resourceConfig;
    }

    private static JerseyGatherer create(ResourceConfig resourceConfig) {
        checkNotNull(resourceConfig);

        return new JerseyGatherer(resourceConfig);
    }

    public static JerseyGatherer forApplication(Path application) {
        checkNotNull(application);
        checkArgument(Files.isRegularFile(application));

        try {
            return create(new ResourceConfig().files(application.toString()).setClassLoader(ClassLoaderUtils.classLoaderFor(application)));
        } catch (Exception e) {
            throw new RuntimeException("unable to instantiate gatherer", e);
        }
    }

    @Override
    public Set<Class<?>> jaxRsClasses() {
        return resourceConfig.getClasses();
    }

    @VisibleForTesting
    ResourceConfig getResourceConfig() {
        return resourceConfig;
    }
}
