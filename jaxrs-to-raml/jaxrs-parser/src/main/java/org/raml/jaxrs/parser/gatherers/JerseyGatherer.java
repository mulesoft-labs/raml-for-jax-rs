package org.raml.jaxrs.parser.gatherers;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import org.glassfish.jersey.server.ResourceConfig;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.Callable;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.raml.jaxrs.parser.util.ClassLoaderUtils.inClassLoaderContextUnchecked;

public class JerseyGatherer implements JaxRsClassesGatherer {

    private final ResourceConfig resourceConfig;

    private JerseyGatherer(ResourceConfig resourceConfig) {
        this.resourceConfig = resourceConfig;
    }

    private static JerseyGatherer create(ResourceConfig resourceConfig) {
        checkNotNull(resourceConfig);

        return new JerseyGatherer(resourceConfig);
    }

    public static JerseyGatherer forApplication(Path application) {
        checkNotNull(application);
        checkArgument(Files.isRegularFile(application));

        return new JerseyGatherer(new ResourceConfig().files(application.toString()));
    }

    @Override
    public Set<Class<?>> jaxRsClasses() {
        return resourceConfig.getClasses();

    }
}
