package org.raml.jaxrs.generator.builders;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import org.raml.jaxrs.generator.Names;
import org.raml.jaxrs.generator.ScalarTypes;

import javax.lang.model.element.Modifier;
import javax.ws.rs.Consumes;
import javax.ws.rs.QueryParam;

/**
 * Created by Jean-Philippe Belanger on 10/30/16.
 * Just potential zeroes and ones
 */
public class MethodDeclaration implements MethodBuilder {

    private final MethodSpec.Builder builder;
    private AnnotationSpec.Builder annotationBuilder;

    public MethodDeclaration(MethodSpec.Builder spec) {

        this.builder = spec;
        builder.addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC);
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
    public MethodBuilder addConsumeAnnotation(String mimeType) {

        if ( annotationBuilder == null ) {
            annotationBuilder = AnnotationSpec.builder(Consumes.class);
        }

        annotationBuilder.addMember("value", "$S", mimeType);

        return this;
    }

    @Override
    public void output() {
        if ( annotationBuilder != null ) {
            builder.addAnnotation(annotationBuilder.build());
        }
    }
}
