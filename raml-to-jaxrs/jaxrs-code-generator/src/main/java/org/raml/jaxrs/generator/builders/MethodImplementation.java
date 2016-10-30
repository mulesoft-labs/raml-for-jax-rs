package org.raml.jaxrs.generator.builders;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.MethodSpec;

/**
 * Created by Jean-Philippe Belanger on 10/30/16.
 * Just potential zeroes and ones
 */
public class MethodImplementation implements MethodBuilder {

    private final MethodSpec.Builder builder;

    public MethodImplementation(MethodSpec.Builder spec) {

        this.builder = spec;
        builder.addAnnotation(AnnotationSpec.builder(Override.class).build());
    }
}
