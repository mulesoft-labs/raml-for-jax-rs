package org.raml.jaxrs.parser.gatherers;

import org.raml.jaxrs.model.JaxRsApplication;
import org.raml.jaxrs.parser.analyzers.Analyzer;
import org.raml.jaxrs.parser.analyzers.JerseyAnalyzer;
import org.raml.jaxrs.parser.util.ClassLoaderUtils;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.concurrent.Callable;

public class Sandbox {

    public static void main(String[] args) throws MalformedURLException {

        Path path = Paths.get("/home/phil/projects/raml-for-jax-rs/jaxrs-to-raml/jaxrs-test-resources/target/jaxrs-test-resources-2.0.0-SNAPSHOT.jar");

        JerseyGatherer gatherer = JerseyGatherer.forApplication(path);
        final Set<Class<?>> classes = gatherer.jaxRsClasses();

        System.out.println("number of classes: " + classes.size());
        System.out.println(classes);


        ClassLoaderUtils.inClassLoaderContextUnchecked(
                gatherer.getClassLoader(),
                new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        Analyzer analyzer = JerseyAnalyzer.create(classes);

                        JaxRsApplication application = analyzer.analyze();

                        System.out.println("put a breakpoint here");
                        return null;
                    }
                }
        );

    }
}
