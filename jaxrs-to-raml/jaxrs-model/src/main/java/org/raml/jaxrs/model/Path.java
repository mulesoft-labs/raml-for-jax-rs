package org.raml.jaxrs.model;

import java.util.List;

public interface Path extends Comparable<Path> {
    List<PathFragment> getFragments();
    String getStringRepresentation(); //Same as joining all fragments.
    Path resolve(Path other);
}
