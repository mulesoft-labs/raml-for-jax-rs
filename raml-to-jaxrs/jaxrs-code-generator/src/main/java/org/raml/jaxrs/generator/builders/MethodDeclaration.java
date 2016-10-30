package org.raml.jaxrs.generator.builders;

import com.squareup.javapoet.MethodSpec;

import javax.lang.model.element.Modifier;

/**
 * Created by Jean-Philippe Belanger on 10/30/16.
 * Just potential zeroes and ones
 */
public class MethodDeclaration implements MethodBuilder {

    private final MethodSpec.Builder builder;

    public MethodDeclaration(MethodSpec.Builder spec) {

        this.builder = spec;
        builder.addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC);
    }

}
