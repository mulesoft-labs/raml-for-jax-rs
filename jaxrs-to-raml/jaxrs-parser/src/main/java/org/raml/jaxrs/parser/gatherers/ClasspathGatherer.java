package org.raml.jaxrs.parser.gatherers;

import com.google.common.annotations.VisibleForTesting;
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

import java.lang.reflect.Method;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * This class is made to gather all Jax RS
 * resource classes.
 */
public class ClasspathGatherer implements Gatherer {

    private final Reflections reflections;


    private ClasspathGatherer(Reflections reflections) {
        this.reflections = reflections;
    }

    public static ClasspathGatherer forPackage(String packageRoot) {
        checkNotNull(packageRoot);

        return new ClasspathGatherer(reflectionsForPackage(packageRoot));
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

        return null;
    }

    @VisibleForTesting
    Set<Class<?>> getClassesWithPaths() {
        return Sets.union(getClassesAnnotatedWithPath(), getClassesWithMethodsAnnotatedWithPath());
    }

    @VisibleForTesting
    Set<Class<?>> getClassesWithGets() {
        return classesOfMethods(reflections.getMethodsAnnotatedWith(GET.class));
    }

    @VisibleForTesting
    Set<Class<?>> getClassesWithPuts() {
        return classesOfMethods(reflections.getMethodsAnnotatedWith(PUT.class));
    }

    @VisibleForTesting
    Set<Class<?>> getClassesAnnotatedWithPath() {
        return reflections.getTypesAnnotatedWith(Path.class);
    }

    @VisibleForTesting
    Set<Class<?>> getClassesWithMethodsAnnotatedWithPath() {
        return classesOfMethods(reflections.getMethodsAnnotatedWith(Path.class));
    }

    private Set<Class<?>> classesOfMethods(Iterable<Method> methodsAnnotatedWithPath) {
        Set<Class<?>> theirClasses = Sets.newHashSet();
        for (Method method : methodsAnnotatedWithPath) {
            theirClasses.add(method.getDeclaringClass());
        }
        return theirClasses;
    }


}
