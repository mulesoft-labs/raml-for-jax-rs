package org.raml.jaxrs.generator.builders;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.FileWriter;
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
        this.typeSpec = TypeSpec.classBuilder(className + "Impl")
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ClassName.get(pack, className)); // this could be wrong.
    }

    public ResourceImplementation withDocumentation(String docs) {

        typeSpec.addJavadoc(docs);
        return this;
    }

    public void output(String rootDirectory) throws IOException {

        JavaFile.Builder file = JavaFile.builder(pack, typeSpec.build());
        file.build().writeTo(new File(rootDirectory));
    }
}
