package org.raml.jaxrs.generator.extension;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

/**
 * Created by Jean-Philippe Belanger on 1/14/17.
 * Just potential zeroes and ones
 */
public interface Context {

    String getResourcePackage();
    String getModelPackage();
    String getSupportPackage();

    MethodSpec.Builder rename(MethodSpec.Builder builder, String name);

    TypeSpec.Builder rename(TypeSpec.Builder builder, String name);
}
