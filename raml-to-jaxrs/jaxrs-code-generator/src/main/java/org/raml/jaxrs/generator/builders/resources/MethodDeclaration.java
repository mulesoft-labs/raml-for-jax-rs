package org.raml.jaxrs.generator.builders.resources;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.Names;
import org.raml.jaxrs.generator.ScalarTypes;
import org.raml.jaxrs.generator.builders.CodeContainer;
import org.raml.jaxrs.generator.builders.OutputBuilder;
import org.raml.jaxrs.generator.builders.SpecFixer;

import javax.lang.model.element.Modifier;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import static org.raml.jaxrs.generator.HTTPMethods.methodNameToAnnotation;
import static org.raml.jaxrs.generator.builders.TypeBuilderHelpers.forParameter;

/**
 * Created by Jean-Philippe Belanger on 10/30/16.
 * Just potential zeroes and ones
 */
public class MethodDeclaration implements MethodBuilder {

    private final CurrentBuild currentBuild;
    private final String name;
    private final String returnClass;
    private final String httpMethodType;
    private AnnotationSpec.Builder consumerAnnotationBuilder;

    private List<OutputBuilder<MethodSpec.Builder>> builders = new ArrayList<>();


    public MethodDeclaration(CurrentBuild currentBuild, String name, String returnClass, String httpMethodType) {

        this.currentBuild = currentBuild;
        this.name = name;
        this.returnClass = returnClass;
        this.httpMethodType = httpMethodType;
    }

    @Override
    public MethodBuilder addQueryParameter(final String name, final String type) {

        builders.add(new OutputBuilder<MethodSpec.Builder>() {
            @Override
            public void build(MethodSpec.Builder parent) {

                currentBuild.javaTypeName(type, forParameter(parent, Names.buildVariableName(name), addAnnotationFix(name, QueryParam.class)));
            }
        });

        return this;
    }

    @Override
    public MethodBuilder addEntityParameter(final String name, final String type) {

        builders.add(new OutputBuilder<MethodSpec.Builder>() {
            @Override
            public void build(MethodSpec.Builder parent) {

                currentBuild.javaTypeName(type, forParameter(parent, name));
            }
        });
        return this;
    }

    @Override
    public MethodBuilder addPathParameter(final String name, final String type) {

        builders.add(new OutputBuilder<MethodSpec.Builder>() {
            @Override
            public void build(MethodSpec.Builder parent) {

                currentBuild.javaTypeName(type, forParameter(parent, Names.buildVariableName(name),
                        addAnnotationFix(name, PathParam.class)));
            }
        });

        return this;
    }

    private SpecFixer<ParameterSpec.Builder> addAnnotationFix(final String name, final Class<? extends Annotation> annotationType) {
        return new SpecFixer<ParameterSpec.Builder>() {
            @Override
            public void adjust(ParameterSpec.Builder spec) {

                AnnotationSpec.Builder annotation = AnnotationSpec.builder(annotationType);
                annotation.addMember("value","$S", name);
                spec.addAnnotation(annotation.build());
            }
        };
    }

    @Override
    public MethodBuilder addConsumeAnnotation(final String mimeType) {

        builders.add(new OutputBuilder<MethodSpec.Builder>() {
            @Override
            public void build(MethodSpec.Builder parent) {

                AnnotationSpec.Builder annotationBuilder = AnnotationSpec.builder(Consumes.class);
                annotationBuilder.addMember("value", "$S", mimeType);
                parent.addAnnotation(annotationBuilder.build());
            }
        });
        return this;
    }

    @Override
    public MethodBuilder addPathAnnotation(final String path) {
        builders.add(new OutputBuilder<MethodSpec.Builder>() {
            @Override
            public void build(MethodSpec.Builder parent) {

                parent.addAnnotation(AnnotationSpec.builder(Path.class).addMember("value", "$S" , path).build());
            }
        });

        return this;
    }


    @Override
    public void output(CodeContainer<MethodSpec.Builder> container) throws IOException {

        MethodSpec.Builder method = MethodSpec.methodBuilder(name)
                .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                .returns(TypeVariableName.get(returnClass))
                .addAnnotation(AnnotationSpec.builder(methodNameToAnnotation(httpMethodType)).build());

        for (OutputBuilder<MethodSpec.Builder> builder : builders) {

            builder.build(method);
        }

        container.into(method);
    }
}
