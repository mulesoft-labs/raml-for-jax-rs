//package org.raml.jaxrs.parser.gatherers;
//
//import com.google.common.collect.Iterables;
//
//import org.junit.Test;
//import org.raml.jaxrs.examples.delete.ClassWithADelete;
//import org.raml.jaxrs.examples.get.ClassWithAGet;
//import org.raml.jaxrs.examples.head.ClassWithAHead;
//import org.raml.jaxrs.examples.path.ClassAnnotatedWithPath;
//import org.raml.jaxrs.examples.path.ClassWithAMethodAnnotatedWithPath;
//import org.raml.jaxrs.examples.path.ClassWithBoth;
//import org.raml.jaxrs.examples.post.ClassWithAPost;
//import org.raml.jaxrs.examples.put.ClassWithAPut;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertTrue;
//
//public class ReflectionsGatherTest {
//
//    private static ReflectionsGatherer forPaths() {
//        return ReflectionsGatherer.forPackage("org.raml.jaxrs.examples.path");
//    }
//
//    private static ReflectionsGatherer forGets() {
//        return ReflectionsGatherer.forPackage("org.raml.jaxrs.examples.get");
//    }
//
//    private static ReflectionsGatherer forPuts() {
//        return ReflectionsGatherer.forPackage("org.raml.jaxrs.examples.put");
//    }
//
//    private static ReflectionsGatherer forPosts() {
//        return ReflectionsGatherer.forPackage("org.raml.jaxrs.examples.post");
//    }
//
//    private static ReflectionsGatherer forDeletes() {
//        return ReflectionsGatherer.forPackage("org.raml.jaxrs.examples.delete");
//    }
//
//    private static ReflectionsGatherer forHeads() {
//        return ReflectionsGatherer.forPackage("org.raml.jaxrs.examples.head");
//    }
//
//    @Test
//    public void testClassAnnotatedWithPath() {
//        assertClassesContain(forPaths().getClassesAnnotatedWithPath(), ClassAnnotatedWithPath.class, ClassWithBoth.class);
//    }
//
//    @Test
//    public void testMethodsAnnotatedWithPath() {
//        assertClassesContain(forPaths().getClassesWithMethodsAnnotatedWithPath(), ClassWithAMethodAnnotatedWithPath.class, ClassWithBoth.class);
//    }
//
//    @Test
//    public void testClassesContainingPaths() {
//        assertClassesContain(forPaths().getClassesWithPaths(), ClassWithAMethodAnnotatedWithPath.class, ClassAnnotatedWithPath.class, ClassWithBoth.class);
//    }
//
//    @Test
//    public void testClassesWithGets() {
//        assertClassesContain(forGets().getClassesWithGets(), ClassWithAGet.class);
//    }
//
//    @Test
//    public void testClassesWithPuts() {
//        assertClassesContain(forPuts().getClassesWithPuts(), ClassWithAPut.class);
//    }
//
//    @Test
//    public void testClassesWithPosts() {
//        assertClassesContain(forPosts().getClassesWithPosts(), ClassWithAPost.class);
//    }
//
//    @Test
//    public void testClassesWithDeletes() {
//        assertClassesContain(forDeletes().getClassesWithDeletes(), ClassWithADelete.class);
//    }
//
//    @Test
//    public void testClassesWithHeads() {
//        assertClassesContain(forHeads().getClassesWithHeads(), ClassWithAHead.class);
//    }
//
//    private void assertClassesContain(Iterable<Class<?>> effectiveClasses, Class<?>... expectedClasses) {
//        assertEquals(expectedClasses.length, Iterables.size(effectiveClasses));
//
//        for (Class<?> clazz : expectedClasses) {
//            assertTrue(Iterables.contains(effectiveClasses, clazz));
//        }
//    }
//
//
//}
