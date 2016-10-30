package org.raml.jaxrs.parser.application;

import java.util.List;

public interface Path {
    List<PathFragment> getFragments();
    String getStringRepresentation(); //Same as joining all fragments.
}
