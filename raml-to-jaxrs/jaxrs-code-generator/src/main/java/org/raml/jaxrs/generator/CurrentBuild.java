package org.raml.jaxrs.generator;

import com.squareup.javapoet.TypeSpec;

/**
 * Created by Jean-Philippe Belanger on 10/26/16.
 * Just potential zeroes and ones
 */
public class CurrentBuild {

    private final String defaultPackage;

    public CurrentBuild(String defaultPackage) {

        this.defaultPackage = defaultPackage;
    }

    public ResourceCreator createResource(String name) {

        return new ResourceCreator(TypeSpec.classBuilder(name));
    }
}
