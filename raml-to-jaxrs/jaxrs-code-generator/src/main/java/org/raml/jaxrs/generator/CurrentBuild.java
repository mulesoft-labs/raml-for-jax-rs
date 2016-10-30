package org.raml.jaxrs.generator;

import org.raml.jaxrs.generator.builders.CompositeResourceBuilder;
import org.raml.jaxrs.generator.builders.ResourceBuilder;
import org.raml.jaxrs.generator.builders.ResourceImplementation;
import org.raml.jaxrs.generator.builders.ResourceInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.raml.jaxrs.generator.Paths.relativize;

/**
 * Created by Jean-Philippe Belanger on 10/26/16.
 * The art of building stuff is here.
 */
public class CurrentBuild {

    private final String defaultPackage;

    private final  List<ResourceBuilder> resources = new ArrayList<ResourceBuilder>();

    public CurrentBuild(String defaultPackage) {

        this.defaultPackage = defaultPackage;
    }

    public ResourceBuilder createResource(String name, String relativeURI) {

        ResourceBuilder builder = new CompositeResourceBuilder(
                new ResourceImplementation(defaultPackage, Names.buildResourceInterfaceName(name)),
                new ResourceInterface(defaultPackage, Names.buildResourceInterfaceName(name), relativize(relativeURI))
        );

        resources.add(builder);
        return builder;
    }

    public void generate(String rootDirectory) throws IOException {

        for (ResourceBuilder resource : resources) {
            resource.output(rootDirectory);
        }
    }
}
