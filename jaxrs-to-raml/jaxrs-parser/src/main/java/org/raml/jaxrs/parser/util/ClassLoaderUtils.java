package org.raml.jaxrs.parser.util;

import com.google.common.collect.FluentIterable;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.concurrent.Callable;

public class ClassLoaderUtils {

    private ClassLoaderUtils() {}

    public static ClassLoader classLoaderFor(URL firstUrl, URL... theRest) {

        URL[] allOfDem = FluentIterable.of(theRest).append(firstUrl).toArray(URL.class);

        return new URLClassLoader(allOfDem);
    }

    public static ClassLoader classLoaderFor(Path path) throws MalformedURLException {
        return classLoaderFor(path.toUri().toURL());
    }
}
