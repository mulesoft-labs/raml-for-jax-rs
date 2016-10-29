package org.raml.jaxrs.generator.builders;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;

/**
 * Created by Jean-Philippe Belanger on 10/27/16.
 * Abstraction of creation.
 */
public class ResourceInterface implements ResourceBuilder {

    private final String pack;
    private final TypeSpec.Builder typeSpec;

    public ResourceInterface(String pack, TypeSpec.Builder typeSpec) {
        this.pack = pack;
        this.typeSpec = typeSpec;
    }

    @Override
    public ResourceInterface withDocumentation(String docs) {

        typeSpec.addJavadoc(docs);
        return this;
    }

    @Override
    public void output(Appendable appendable) throws IOException {

        JavaFile.Builder file = JavaFile.builder(pack, typeSpec.build());
        file.build().writeTo(appendable);
    }
}
