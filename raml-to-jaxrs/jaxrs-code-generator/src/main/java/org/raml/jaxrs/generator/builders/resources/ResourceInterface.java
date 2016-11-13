package org.raml.jaxrs.generator.builders.resources;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.Names;

import javax.lang.model.element.Modifier;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jean-Philippe Belanger on 10/27/16.
 * Abstraction of creation.
 */
public class ResourceInterface implements ResourceBuilder {

    private final TypeSpec.Builder typeSpec;
    private final CurrentBuild build;
    private List<MethodSpec.Builder> methods = new ArrayList<MethodSpec.Builder>();
    private List<TypeSpec.Builder> responseTypes = new ArrayList<TypeSpec.Builder>();
    private List<ResponseClassBuilder> responseClassBuilders = new ArrayList<>();
    private List<MethodBuilder> methodBuilders = new ArrayList<>();

    public ResourceInterface(CurrentBuild build, String name, String relativeURI) {
        this.build = build;
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
    public MethodBuilder createMethod(String method, String fullMethodName, String returnClass) {

/*
        MethodSpec.Builder spec = MethodSpec.methodBuilder(method + additionalNames).returns(TypeVariableName.get(returnClass))
                .addAnnotation(AnnotationSpec.builder(methodNameToAnnotation(method)).build());
        methods.add(spec);
*/

        MethodBuilder md = new MethodDeclaration(build, typeSpec, fullMethodName, returnClass, method);
        methodBuilders.add(md);
        return md;
    }

    @Override
    public ResponseClassBuilder createResponseClassBuilder(String method, String additionalNames) {

        ResponseClassBuilderImpl responseClassBuilder = new ResponseClassBuilderImpl(build, typeSpec, Names.buildTypeName(method) + additionalNames);
        responseClassBuilders.add(responseClassBuilder);
        return responseClassBuilder;
    }

    @Override
    public void output(String rootDirectory) throws IOException {

        for (MethodBuilder methodBuilder : methodBuilders) {
            methodBuilder.output();
        }

        for (ResponseClassBuilder responseClassBuilder : responseClassBuilders) {
            responseClassBuilder.output();
        }

        JavaFile.Builder file = JavaFile.builder(build.getDefaultPackage(), typeSpec.build());
        file.build().writeTo(new File(rootDirectory));
    }
}
