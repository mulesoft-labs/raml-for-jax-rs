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

    /**
     * Rename a method defined in a JavaPoet method builder.
     *
     * This creates an identical method builder with a new name.
     * @param builder
     * @param name
     * @return
     */
    MethodSpec.Builder rename(MethodSpec.Builder builder, String name);

    /**
     * Rename a class/interface defined in a JavaPoet type builder.
     *
     * This creates an identical method builder with a new name.
     * @param builder
     * @param name
     * @return
     */

    TypeSpec.Builder rename(TypeSpec.Builder builder, String name);
}
