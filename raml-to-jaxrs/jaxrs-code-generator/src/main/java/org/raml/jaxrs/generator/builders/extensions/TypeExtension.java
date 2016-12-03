package org.raml.jaxrs.generator.builders.extensions;

import com.squareup.javapoet.TypeSpec;

/**
 * Created by Jean-Philippe Belanger on 11/30/16.
 * Just potential zeroes and ones
 */
public interface TypeExtension {

    void onTypeImplementation(TypeSpec.Builder typeSpec);
}
