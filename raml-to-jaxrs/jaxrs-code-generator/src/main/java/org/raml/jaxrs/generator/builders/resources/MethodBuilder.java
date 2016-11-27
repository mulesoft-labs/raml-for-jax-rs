package org.raml.jaxrs.generator.builders.resources;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.raml.jaxrs.generator.builders.Generator;

/**
 * Created by Jean-Philippe Belanger on 10/30/16.
 * Just potential zeroes and ones
 */
public interface MethodBuilder extends Generator<MethodSpec.Builder> {
    MethodBuilder addQueryParameter(String name, String type);
    MethodBuilder addPathParameter(String name, String type);
    MethodBuilder addEntityParameter(String name, String type);
    MethodBuilder addConsumeAnnotation(String type);
    MethodBuilder addPathAnnotation(String path);
}
