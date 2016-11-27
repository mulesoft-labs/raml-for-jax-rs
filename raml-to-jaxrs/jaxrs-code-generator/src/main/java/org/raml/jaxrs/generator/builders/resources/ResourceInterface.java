package org.raml.jaxrs.generator.builders.resources;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.Names;
import org.raml.jaxrs.generator.builders.CodeContainer;
import org.raml.jaxrs.generator.builders.OutputBuilder;
import org.raml.jaxrs.generator.builders.types.RamlTypeGenerator;

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
public class ResourceInterface implements ResourceGenerator {

    private final CurrentBuild build;
    private final String name;
    private final String relativeURI;

    private List<OutputBuilder<TypeSpec.Builder>> builders = new ArrayList<>();
    private List<ResponseClassBuilder> responseClassBuilders = new ArrayList<>();
    private List<MethodBuilder> methodBuilders = new ArrayList<>();
    private List<RamlTypeGenerator> internalTypes = new ArrayList<>();

    public ResourceInterface(CurrentBuild build, String name, String relativeURI) {
        this.build = build;
        this.name = name;
        this.relativeURI = relativeURI;
    }

    @Override
    public ResourceInterface withDocumentation(final String docs) {

        builders.add(new OutputBuilder<TypeSpec.Builder>() {
            @Override
            public void build(TypeSpec.Builder parent) {

                parent.addJavadoc(docs);
            }
        });

        return this;
    }

    @Override
    public ResourceGenerator mediaType(final List<String> mimeTypes) {

        builders.add(new OutputBuilder<TypeSpec.Builder>() {
            @Override
            public void build(TypeSpec.Builder parent) {

                AnnotationSpec.Builder p = AnnotationSpec.builder(Produces.class);
                AnnotationSpec.Builder c = AnnotationSpec.builder(Consumes.class);
                for (String mimeType : mimeTypes) {
                    p.addMember("value", "$S", mimeType);
                    c.addMember("value", "$S", mimeType);
                }
                parent.addAnnotation(p.build());
                parent.addAnnotation(c.build());
            }
        });

        return this;
    }

    @Override
    public MethodBuilder createMethod(String method, String fullMethodName, String returnClass) {

        MethodBuilder md = new MethodDeclaration(build, fullMethodName, returnClass, method);
        methodBuilders.add(md);
        return md;
    }

    @Override
    public ResponseClassBuilder createResponseClassBuilder(String method, String additionalNames) {

        ResponseClassBuilderImpl responseClassBuilder = new ResponseClassBuilderImpl(build, Names.buildTypeName(method) + additionalNames);
        responseClassBuilders.add(responseClassBuilder);
        return responseClassBuilder;
    }

    @Override
    public void addInternalType(RamlTypeGenerator internalGenerator) {

        internalTypes.add(internalGenerator);
    }

    @Override
    public void output(CodeContainer<TypeSpec> container) throws IOException {

        final TypeSpec.Builder typeSpec = TypeSpec.interfaceBuilder(Names.buildTypeName(name))
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(AnnotationSpec.builder(Path.class)
                        .addMember("value", "$S", relativeURI).build());

        for (OutputBuilder<TypeSpec.Builder> builder : builders) {

            builder.build(typeSpec);
        }

        for (MethodBuilder methodBuilder : methodBuilders) {
            methodBuilder.output(new CodeContainer<MethodSpec.Builder>() {
                @Override
                public void into(MethodSpec.Builder g) throws IOException {
                    typeSpec.addMethod(g.build());
                }
            });
        }

        for (ResponseClassBuilder responseClassBuilder : responseClassBuilders) {
            responseClassBuilder.output(new CodeContainer<TypeSpec.Builder>() {
                @Override
                public void into(TypeSpec.Builder g) throws IOException {
                    typeSpec.addType(g.build());
                }
            });
        }

        for (final RamlTypeGenerator internalType : internalTypes) {
            internalType.output(new CodeContainer<TypeSpec.Builder>() {
                @Override
                public void into(TypeSpec.Builder g) throws IOException {
                    g.addModifiers(Modifier.STATIC);
                    typeSpec.addType(g.build());
                }
            });
        }
        container.into(typeSpec.build());
    }
}
