package org.raml.jaxrs.generator.builders;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.raml.jaxrs.generator.Names;

import javax.lang.model.element.Modifier;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.raml.jaxrs.generator.HTTPMethods.methodNameToAnnotation;

/**
 * Created by Jean-Philippe Belanger on 10/27/16.
 * Abstraction of creation.
 */
public class ResourceInterface implements ResourceBuilder {

    private final String pack;
    private final TypeSpec.Builder typeSpec;
    private List<MethodSpec.Builder> methods = new ArrayList<MethodSpec.Builder>();

    public ResourceInterface(String pack, String name, String relativeURI) {
        this.pack = pack;
        this.typeSpec = TypeSpec.interfaceBuilder(Names.buildTypeName(name))
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(AnnotationSpec.builder(Path.class).addMember("value", "$S", relativeURI).build());
    }

    @Override
    public ResourceInterface withDocumentation(String docs) {

        typeSpec.addJavadoc(docs);
        return this;
    }

    @Override
    public ResourceBuilder mediaType(List<String> mimeTypes) {

        AnnotationSpec.Builder p = AnnotationSpec.builder(Produces.class);
        AnnotationSpec.Builder c = AnnotationSpec.builder(Consumes.class);
        for (String mimeType : mimeTypes) {
            p.addMember("value", "$S", mimeType);
            c.addMember("value", "$S", mimeType);
        }
        typeSpec.addAnnotation(p.build());
        typeSpec.addAnnotation(c.build());
        return this;
    }

    @Override
    public MethodBuilder createMethod(String method, String additionalNames) {

        MethodSpec.Builder spec = MethodSpec.methodBuilder(method + additionalNames)
                .addAnnotation(AnnotationSpec.builder(methodNameToAnnotation(method)).build());
        methods.add(spec);

        return new MethodDeclaration(spec);
    }

    public void output(String rootDirectory) throws IOException {

        for (MethodSpec.Builder method : methods) {

            typeSpec.addMethod(method.build());
        }

        JavaFile.Builder file = JavaFile.builder(pack, typeSpec.build());
        file.build().writeTo(new File(rootDirectory));
    }
}
