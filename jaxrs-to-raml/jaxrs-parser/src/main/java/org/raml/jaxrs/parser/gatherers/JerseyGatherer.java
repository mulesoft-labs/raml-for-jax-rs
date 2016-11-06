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
    private final ClassLoader classLoader;

    private JerseyGatherer(ResourceConfig resourceConfig, ClassLoader classLoader) {
        this.resourceConfig = resourceConfig;
        this.classLoader = classLoader;
    }

    private static JerseyGatherer create(ResourceConfig resourceConfig, ClassLoader classLoader) {
        checkNotNull(resourceConfig);
        checkNotNull(classLoader);

        return new JerseyGatherer(resourceConfig, classLoader);
    }

    public static JerseyGatherer forApplication(Path application) {
        checkNotNull(application);
        checkArgument(Files.isRegularFile(application));

        final ClassLoader loader = getClassLoaderFor(application);

        return inClassLoaderContextUnchecked(
                loader,
                new Callable<JerseyGatherer>() {
                    @Override
                    public JerseyGatherer call() throws Exception {
                        Iterable<String> rootPackages = getRootPackages();
                        return new JerseyGatherer(resourceConfigForPackages(rootPackages), loader);
                    }
                }
        );
    }

    private static Iterable<String> getRootPackages() {
        Reflections reflections = new Reflections(new SubTypesScanner(false));
        return getPackagesFromTypes(reflections.getAllTypes());
    }

    private static ClassLoader getClassLoaderFor(Path application) {
        try {
            return new URLClassLoader(new URL[]{application.toUri().toURL()}, null);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private static ResourceConfig resourceConfigForPackages(Iterable<String> packages) {
        checkNotNull(packages);
        checkArgument(!Iterables.isEmpty(packages));
        checkPackages(packages);

        return new ResourceConfig().packages(Iterables.toArray(packages, String.class));
    }

    private static void checkPackages(Iterable<String> packages) {
        for (String packageName : packages) {
            checkNotNull(packageName);
            checkArgument(packageName.trim().length() > 0, "package name should have at least one meaningful character");
        }
    }

    private static Iterable<String> getPackagesFromTypes(Set<String> allTypes) {
        Set<String> rootPackages = Sets.newHashSet();

        for (String type : allTypes) {

            String rootPackage = parseRootPackageOfType(type);
            if (rootPackages.contains(rootPackage)) {

                continue;
            }
            rootPackages.add(rootPackage);
        }

        return rootPackages;
    }

    private static String parseRootPackageOfType(String type) {
        return type.split("\\.")[0];
    }

    @Override
    public Set<Class<?>> jaxRsClasses() {
        return inClassLoaderContextUnchecked(
                classLoader,
                new Callable<Set<Class<?>>>() {
                    @Override
                    public Set<Class<?>> call() throws Exception {
                        return resourceConfig.getClasses();
                    }
                }
        );
    }

    //Only there temporary until I fix the classloading issue.
    public ClassLoader getClassLoader() {
        return classLoader;
    }
}
