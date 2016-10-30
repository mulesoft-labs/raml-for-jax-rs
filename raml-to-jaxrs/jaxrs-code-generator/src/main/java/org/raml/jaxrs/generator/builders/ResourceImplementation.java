package org.raml.jaxrs.generator.builders;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;

/**
 * Created by Jean-Philippe Belanger on 10/27/16.
 * Abstraction of creation.
 */
public class ResourceImplementation implements ResourceBuilder {

    private final String pack;
    private final TypeSpec.Builder typeSpec;

    public ResourceImplementation(String pack, String className) {
        this.pack = pack;
        this.typeSpec = TypeSpec.classBuilder(className);
    }

    public ResourceImplementation withDocumentation(String docs) {

        typeSpec.addJavadoc(docs);
        return this;
    }

    public void output(Appendable appendable) throws IOException {

        JavaFile.Builder file = JavaFile.builder(pack, typeSpec.build());
        file.build().writeTo(appendable);
    }
}
