package org.raml.jaxrs.parser.gatherers;

import com.google.common.collect.Iterables;

import org.junit.Test;
import org.raml.jaxrs.examples.path.ClassAnnotatedWithPath;
import org.raml.jaxrs.examples.path.ClassWithAMethodAnnotatedWithPath;
import org.raml.jaxrs.examples.path.ClassWithBoth;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 */
public class ClasspathGathererTest {

    ClasspathGatherer forPaths() {
        return ClasspathGatherer.forPackage("org.raml.jaxrs.examples.path");
    }

    @Test
    public void testClassAnnotatedWithPath() {
        ClasspathGatherer gatherer = forPaths();
        Set<Class<?>> classesWithPath = gatherer.getClassesAnnotatedWithPath();

        assertEquals(2, classesWithPath.size());
        assertTrue(classesWithPath.contains(ClassAnnotatedWithPath.class));
        assertTrue(classesWithPath.contains(ClassWithBoth.class));
    }

    @Test
    public void testMethodsAnnotatedWithPath() {
        ClasspathGatherer gatherer = forPaths();
        Set<Class<?>> classesWithPath = gatherer.getClassesWithMethodsAnnotatedWithPath();

        assertEquals(2, classesWithPath.size());
        assertTrue(classesWithPath.contains(ClassWithAMethodAnnotatedWithPath.class));
        assertTrue(classesWithPath.contains(ClassWithBoth.class));
    }

    @Test
    public void testClassesContainingPaths() {
        ClasspathGatherer gatherer = forPaths();
        Set<Class<?>> classesWithPath = gatherer.getClassesContainingPaths();

        assertEquals(3, classesWithPath.size());
        assertTrue(classesWithPath.contains(ClassWithAMethodAnnotatedWithPath.class));
        assertTrue(classesWithPath.contains(ClassAnnotatedWithPath.class));
        assertTrue(classesWithPath.contains(ClassWithBoth.class));
    }

}
