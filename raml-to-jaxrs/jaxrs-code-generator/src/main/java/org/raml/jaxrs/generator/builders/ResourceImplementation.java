package org.raml.jaxrs.generator.builders;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jean-Philippe Belanger on 10/27/16.
 * Abstraction of creation.
 */
public class ResourceImplementation implements ResourceBuilder {

    private final String pack;
    private final TypeSpec.Builder typeSpec;
    private final List<MethodSpec.Builder> methods = new ArrayList<MethodSpec.Builder>();

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

    @Override
    public ResourceBuilder mediaType(List<String> mimeTypes) {

        return this;
    }

    @Override
    public MethodBuilder createMethod(String method) {

        MethodSpec.Builder spec = MethodSpec.methodBuilder(method);
        methods.add(spec);

        return new MethodImplementation(spec);
    }

    public void output(String rootDirectory) throws IOException {

        for (MethodSpec.Builder method : methods) {

            typeSpec.addMethod(method.build());
        }

        JavaFile.Builder file = JavaFile.builder(pack, typeSpec.build());
        file.build().writeTo(new File(rootDirectory));
    }
}
