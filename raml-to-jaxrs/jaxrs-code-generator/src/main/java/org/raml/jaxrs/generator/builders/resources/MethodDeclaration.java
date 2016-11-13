package org.raml.jaxrs.generator.builders.resources;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.Names;
import org.raml.jaxrs.generator.ScalarTypes;

import javax.lang.model.element.Modifier;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import static org.raml.jaxrs.generator.HTTPMethods.methodNameToAnnotation;

/**
 * Created by Jean-Philippe Belanger on 10/30/16.
 * Just potential zeroes and ones
 */
public class MethodDeclaration implements MethodBuilder {

    private final MethodSpec.Builder builder;
    private final CurrentBuild currentBuild;
    private final TypeSpec.Builder typeSpec;
    private AnnotationSpec.Builder consumerAnnotationBuilder;

    public MethodDeclaration(CurrentBuild currentBuild, TypeSpec.Builder typeSpec, String name, String returnClass, String httpMethodType) {
        this.currentBuild = currentBuild;
        this.typeSpec = typeSpec;
        this.builder = MethodSpec.methodBuilder(name)
                .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                .returns(TypeVariableName.get(returnClass))
                .addAnnotation(AnnotationSpec.builder(methodNameToAnnotation(httpMethodType)).build());
    }

    @Override
    public MethodBuilder addQueryParameter(String name, String type) {

        ParameterSpec.Builder param = ParameterSpec.builder(ScalarTypes.scalarToJavaType(type), Names.buildVariableName(name));
        AnnotationSpec.Builder annotation = AnnotationSpec.builder(QueryParam.class);
        annotation.addMember("value","$S", name);
        param.addAnnotation(annotation.build());

        builder.addParameter(param.build());
        return this;
    }

    @Override
    public MethodBuilder addEntityParameter(String name, String type) {

        ParameterSpec.Builder param = ParameterSpec.builder(ScalarTypes.scalarToJavaType(type), Names.buildVariableName(name));

        builder.addParameter(param.build());
        return this;
    }

    @Override
    public MethodBuilder addPathParameter(String name, String type) {

        ParameterSpec.Builder param = ParameterSpec.builder(ScalarTypes.scalarToJavaType(type), Names.buildVariableName(name));
        AnnotationSpec.Builder annotation = AnnotationSpec.builder(PathParam.class);
        annotation.addMember("value","$S", name);
        param.addAnnotation(annotation.build());

        builder.addParameter(param.build());
        return this;
    }

    @Override
    public MethodBuilder addConsumeAnnotation(String mimeType) {

        if ( consumerAnnotationBuilder == null ) {
            consumerAnnotationBuilder = AnnotationSpec.builder(Consumes.class);
        }

        consumerAnnotationBuilder.addMember("value", "$S", mimeType);

        return this;
    }

    @Override
    public MethodBuilder addPathAnnotation(String path) {
        builder.addAnnotation(AnnotationSpec.builder(Path.class).addMember("value", "$S" , path).build());
        return this;
    }

    @Override
    public void output() {
        if ( consumerAnnotationBuilder != null ) {
            builder.addAnnotation(consumerAnnotationBuilder.build());
        }
        typeSpec.addMethod(builder.build());
    }
}
