package org.raml.jaxrs.generator;

import org.raml.jaxrs.generator.builders.CompositeResourceBuilder;
import org.raml.jaxrs.generator.builders.ResourceBuilder;
import org.raml.jaxrs.generator.builders.ResourceImplementation;
import org.raml.jaxrs.generator.builders.ResourceInterface;

/**
 * Created by Jean-Philippe Belanger on 10/26/16.
 * The art of building stuff is here.
 */
public class CurrentBuild {

    private final String defaultPackage;

    public CurrentBuild(String defaultPackage) {

        this.defaultPackage = defaultPackage;
    }

    public ResourceBuilder createResource(String name) {

        return new CompositeResourceBuilder(
                new ResourceImplementation(defaultPackage, Names.buildResourceInterfaceName(name)),
                new ResourceInterface(defaultPackage, Names.buildResourceInterfaceName(name) + "Impl")
        );
    }
}
