package org.raml.jaxrs.model;

public interface Path extends Comparable<Path> {
    Iterable<Path> getFragments();
    String getStringRepresentation(); //Same as joining all fragments.
    Path resolve(Path other);
    boolean isSuperPathOf(Path key);
    Path relativize(Path other);
}
