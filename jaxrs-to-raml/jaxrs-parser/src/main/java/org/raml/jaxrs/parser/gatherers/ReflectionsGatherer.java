package org.raml.jaxrs.parser.gatherers;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.MemberUsageScanner;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.MethodParameterNamesScanner;
import org.reflections.scanners.MethodParameterScanner;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.Scanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.scanners.TypeElementsScanner;
import org.reflections.util.ConfigurationBuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.Set;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;


/**
 * This class is made to gather all Jax RS
 * resource classes.
 */
public class ReflectionsGatherer implements Gatherer {

    private final Reflections reflections;


    private ReflectionsGatherer(Reflections reflections) {
        this.reflections = reflections;
    }

    public static ReflectionsGatherer forApplication(java.nio.file.Path application) {
        checkNotNull(application);

        return new ReflectionsGatherer(reflectionsForApplication(application));
    }

    private static Reflections reflectionsForApplication(java.nio.file.Path application) {
        URLClassLoader classLoader;

        try {
            classLoader = new URLClassLoader(new URL[]{application.toUri().toURL()}, null);

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        ConfigurationBuilder builder = new ConfigurationBuilder().addScanners(scanners())
                .forPackages("");
//        builder.setClassLoaders(new ClassLoader[] {classLoader} );

        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(classLoader);
            return new Reflections(builder);
        } finally {
            Thread.currentThread().setContextClassLoader(currentClassLoader);
        }
    }

    public static ReflectionsGatherer forPackage(String packageRoot) {
        checkNotNull(packageRoot);

        return new ReflectionsGatherer(reflectionsForPackage(packageRoot));
    }

    private static Reflections reflectionsForPackage(String packageRoot) {
        return new Reflections(packageRoot, scanners());
    }

    private static Scanner[] scanners() {
        return new Scanner[]{
                new FieldAnnotationsScanner(), new MemberUsageScanner(),
                new MethodAnnotationsScanner(), new MethodParameterNamesScanner(),
                new MethodParameterScanner(), new ResourcesScanner(),
                new SubTypesScanner(), new TypeAnnotationsScanner(),
                new TypeElementsScanner()};
    }

    public Set<Class<?>> gather() {
        Set<Class<?>> classesAnnoted = getJaxRsClasses();

        return null;
    }

    @VisibleForTesting
    Set<Class<?>> getJaxRsClasses() {
        Set<Class<?>> result =
                ImmutableSet.<Class<?>>builder().addAll(getClassesWithPaths()).addAll(getClassesWithGets())
                        .addAll(getClassesWithPuts()).addAll(getClassesWithPosts()).addAll(getClassesWithDeletes()).addAll(getClassesWithHeads())
                        .build();
        return result;
    }

    @VisibleForTesting
    Set<Class<?>> getClassesWithPaths() {
        return Sets.union(getClassesAnnotatedWithPath(), getClassesWithMethodsAnnotatedWithPath());
    }

    @VisibleForTesting
    Set<Class<?>> getClassesWithGets() {
        return classesOfMethodsAnnotatedWith(GET.class);
    }

    @VisibleForTesting
    Set<Class<?>> getClassesWithPuts() {
        return classesOfMethodsAnnotatedWith(PUT.class);
    }

    @VisibleForTesting
    Set<Class<?>> getClassesWithPosts() {
        return classesOfMethodsAnnotatedWith(POST.class);
    }

    @VisibleForTesting
    Set<Class<?>> getClassesWithDeletes() {
        return classesOfMethodsAnnotatedWith(DELETE.class);
    }

    @VisibleForTesting
    Iterable<Class<?>> getClassesWithHeads() {
        return classesOfMethodsAnnotatedWith(HEAD.class);
    }

    @VisibleForTesting
    Set<Class<?>> getClassesAnnotatedWithPath() {
        return reflections.getTypesAnnotatedWith(Path.class);
    }

    @VisibleForTesting
    Set<Class<?>> getClassesWithMethodsAnnotatedWithPath() {
        return classesOfMethodsAnnotatedWith(Path.class);
    }

    private Set<Class<?>> classesOfMethodsAnnotatedWith(Class<? extends Annotation> clazz) {
        return classesOfMethods(reflections.getMethodsAnnotatedWith(clazz));
    }


    private static Set<Class<?>> classesOfMethods(Iterable<Method> methodsAnnotatedWithPath) {
        Set<Class<?>> theirClasses = Sets.newHashSet();
        for (Method method : methodsAnnotatedWithPath) {
            theirClasses.add(method.getDeclaringClass());
        }
        return theirClasses;
    }

    public static void main(String[] args) {
//        ReflectionsGatherer gatherer = forPackage("");
        ReflectionsGatherer gatherer = forApplication(Paths.get("/home/phil/projects/raml-for-jax-rs/jaxrs-to-raml/jaxrs-test-resources/target/jaxrs-test-resources-2.0.0-SNAPSHOT.jar"));

        Set<Class<?>> jaxRsClasses = gatherer.getJaxRsClasses();

        System.out.println(format("no classes = %s", jaxRsClasses.size()));
        for (Class<?> clazz : jaxRsClasses) {
            System.out.println(clazz.getCanonicalName());
        }

        System.out.println(Test.class.getSimpleName());
        System.out.println(format("methods: %s", Test.class.getDeclaredMethods()));
    }
}
