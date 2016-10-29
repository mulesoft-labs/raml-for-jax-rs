package org.raml.jaxrs.generator;

import com.squareup.javapoet.TypeSpec;
import org.raml.jaxrs.generator.builders.ResourceImplementation;

/**
 * Created by Jean-Philippe Belanger on 10/26/16.
 * The art of building stuff is here.
 */
public class CurrentBuild {

    private final String defaultPackage;

    public CurrentBuild(String defaultPackage) {

        this.defaultPackage = defaultPackage;
    }

    public ResourceImplementation createResource(String name) {

        return new ResourceImplementation(defaultPackage, Names.buildResourceInterfaceName(name));
    }
}
