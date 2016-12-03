package org.raml.jaxrs.generator.builders;

import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

/**
 * Created by Jean-Philippe Belanger on 11/20/16.
 * Just potential zeroes and ones
 */
public interface TypeGenerator<T> extends Generator<T> {

    TypeName getGeneratedJavaType();
    // is a property declared here or in my parents ?
    boolean declaresProperty(String name);

}
