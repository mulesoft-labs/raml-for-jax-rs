package org.raml.jaxrs.generator;

import com.squareup.javapoet.TypeSpec;

/**
 * Created by Jean-Philippe Belanger on 10/27/16.
 * Just potential zeroes and ones
 */
public class ResourceCreator {

    private final TypeSpec.Builder typeSpec;

    public ResourceCreator(TypeSpec.Builder typeSpec) {
        this.typeSpec = typeSpec;
    }

    public ResourceCreator withDocumentation(String docs) {

        typeSpec.addJavadoc(docs);
        return this;
    }
}
