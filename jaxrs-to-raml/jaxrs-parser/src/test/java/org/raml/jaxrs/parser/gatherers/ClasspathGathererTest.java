package org.raml.jaxrs.parser.gatherers;

import org.junit.Test;
import org.raml.jaxrs.examples.get.ClassWithAGet;
import org.raml.jaxrs.examples.path.ClassAnnotatedWithPath;
import org.raml.jaxrs.examples.path.ClassWithAMethodAnnotatedWithPath;
import org.raml.jaxrs.examples.path.ClassWithBoth;
import org.raml.jaxrs.examples.put.ClassWithAPut;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 */
public class ClasspathGathererTest {

    private static ClasspathGatherer forPaths() {
        return ClasspathGatherer.forPackage("org.raml.jaxrs.examples.path");
    }

    private static ClasspathGatherer forGets() {
        return ClasspathGatherer.forPackage("org.raml.jaxrs.examples.get");
    }

    private static ClasspathGatherer forPuts() {
        return ClasspathGatherer.forPackage("org.raml.jaxrs.examples.put");
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
        Set<Class<?>> classesWithPath = gatherer.getClassesWithPaths();

        assertEquals(3, classesWithPath.size());
        assertTrue(classesWithPath.contains(ClassWithAMethodAnnotatedWithPath.class));
        assertTrue(classesWithPath.contains(ClassAnnotatedWithPath.class));
        assertTrue(classesWithPath.contains(ClassWithBoth.class));
    }

    @Test
    public void testClassesWithGets() {
        ClasspathGatherer gatherer = forGets();

        Set<Class<?>> classesWithPath = gatherer.getClassesWithGets();

        assertEquals(1, classesWithPath.size());
        assertTrue(classesWithPath.contains(ClassWithAGet.class));
    }

    @Test
    public void testClassesWithPuts() {
        ClasspathGatherer gatherer = forPuts();

        Set<Class<?>> classesWithPath = gatherer.getClassesWithPuts();

        assertEquals(1, classesWithPath.size());
        assertTrue(classesWithPath.contains(ClassWithAPut.class));
    }


}
