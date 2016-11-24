package org.raml.jaxrs.parser.gatherers;

import org.raml.jaxrs.model.JaxRsApplication;
import org.raml.jaxrs.parser.analyzers.Analyzer;
import org.raml.jaxrs.parser.analyzers.JerseyAnalyzer;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

public class Sandbox {

    public static void main(String[] args) throws MalformedURLException {

        Path path = Paths.get("/home/phil/projects/raml-for-jax-rs/jaxrs-to-raml/jaxrs-test-resources/target/jaxrs-test-resources-2.0.0-SNAPSHOT.jar");


        JerseyGatherer gatherer = JerseyGatherer.forApplication(path);
        final Set<Class<?>> classes = gatherer.jaxRsClasses();

        System.out.println("number of classes: " + classes.size());
        System.out.println(classes);


        Analyzer analyzer = JerseyAnalyzer.create(classes);

        JaxRsApplication application = analyzer.analyze();

        System.out.println("put a breakpoint here");

    }
}
