package org.raml.jaxrs.parser.gatherers;

import com.google.common.collect.Lists;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.model.Resource;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkState;
import static java.lang.String.format;

public class Sandbox {

    public static void main(String[] args) throws MalformedURLException {

        Path path = Paths.get("/home/phil/projects/raml-for-jax-rs/jaxrs-to-raml/jaxrs-test-resources/target/jaxrs-test-resources-2.0.0-SNAPSHOT.jar");

//        checkState(Files.isReadable(path));
//
//        URLClassLoader urlClassLoader = new URLClassLoader(new URL[] {path.toUri().toURL()}, null);
//
//        Thread.currentThread().setContextClassLoader(urlClassLoader);
//
//        Set<Class<?>> classes = new ResourceConfig().packages("org.raml").getClasses();

        Set<Class<?>> classes = JerseyGatherer.forApplication(path).jaxRsClasses();

        System.out.println("number of classes: " + classes.size());
        System.out.println(classes);

//        List<Resource> resources = Lists.newArrayList();
//        for (Class<?> clazz : classes) {
//            resources.add(Resource.from(clazz));
//        }
//
//        System.out.println(resources);
    }
}
