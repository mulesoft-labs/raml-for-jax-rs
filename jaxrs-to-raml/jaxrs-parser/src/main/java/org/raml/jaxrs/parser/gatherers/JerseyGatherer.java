package org.raml.jaxrs.parser.gatherers;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import org.glassfish.jersey.server.ResourceConfig;
import org.raml.jaxrs.parser.util.ClassLoaderUtils;
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

        try {
            return new JerseyGatherer(new ResourceConfig().files(application.toString()).setClassLoader(ClassLoaderUtils.classLoaderFor(application)));
        } catch (MalformedURLException e) {
            throw new RuntimeException("unable to instantiate gather", e);
        }
    }

    @Override
    public Set<Class<?>> jaxRsClasses() {
        return resourceConfig.getClasses();

    }
}
