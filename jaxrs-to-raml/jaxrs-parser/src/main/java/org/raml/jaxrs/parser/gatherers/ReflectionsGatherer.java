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
public class ReflectionsGatherer implements JaxRsClassesGatherer {

    private final Reflections reflections;


    private ReflectionsGatherer(Reflections reflections) {
        this.reflections = reflections;
    }

    private static Scanner[] scanners() {
        return new Scanner[]{
                new FieldAnnotationsScanner(), new MemberUsageScanner(),
                new MethodAnnotationsScanner(), new MethodParameterNamesScanner(),
                new MethodParameterScanner(), new ResourcesScanner(),
                new SubTypesScanner(), new TypeAnnotationsScanner(),
                new TypeElementsScanner()};
    }

    public Set<Class<?>> jaxRsClasses() {
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
}
