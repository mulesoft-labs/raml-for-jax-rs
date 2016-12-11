package org.raml.utilities.builder;

public interface Field<T> {
    T get();
    boolean isSet();
}
