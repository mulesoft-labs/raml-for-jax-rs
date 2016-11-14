package org.raml.jaxrs.model.impl;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

import org.raml.jaxrs.model.Path;
import org.raml.jaxrs.model.Utilities;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class PathImpl implements Path {

    private static final PathImpl EMPTY_PATH = new PathImpl("");

    //TODO: try to reuse java path instead... after unit tests.
    private final String pathString;

    private PathImpl(String pathString) {
        this.pathString = pathString;
    }

    public static PathImpl fromString(String pathString) {
        checkNotNull(pathString);

        return new PathImpl(Utilities.uniformizePath(pathString));
    }

    public static PathImpl empty() {
        return EMPTY_PATH;
    }

    @Override
    public Iterable<Path> getFragments() {
        return Iterables.transform(
                Splitter.on("/").omitEmptyStrings().split(this.pathString),
                new Function<String, Path>() {
                    @Override
                    public Path apply(String s) {
                        return fromString(s);
                    }
                }
        );
    }

    @Override
    public Path resolve(Path other) {
        return PathImpl.fromString(this.pathString + other.getStringRepresentation());
    }

    @Override
    public String getStringRepresentation() {
        return this.pathString;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PathImpl path = (PathImpl) o;

        return pathString.equals(path.pathString);

    }

    @Override
    public int hashCode() {
        return pathString.hashCode();
    }

    @Override
    public boolean isSuperPathOf(Path key) {
        return key.getStringRepresentation().startsWith(this.getStringRepresentation());
    }

    @Override
    public Path relativize(Path other) {
        checkArgument(this.isSuperPathOf(other));

        return PathImpl.fromString(other.getStringRepresentation().substring(this.getStringRepresentation().length()));
    }

    @Override
    public int compareTo(Path o) {
        return this.getStringRepresentation().compareTo(o.getStringRepresentation());
    }
}
