package org.raml.jaxrs.generator.builders.types;

import com.squareup.javapoet.MethodSpec;

/**
 * Created by Jean-Philippe Belanger on 11/13/16.
 * Just potential zeroes and ones
 */
public interface TypeDescriber {

    void asJavaType(Class c);
    void asBuiltType(String name);
}
