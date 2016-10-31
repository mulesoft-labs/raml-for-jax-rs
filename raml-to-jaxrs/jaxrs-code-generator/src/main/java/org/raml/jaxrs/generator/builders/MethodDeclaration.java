package org.raml.jaxrs.generator.builders;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import org.raml.jaxrs.generator.Names;

import javax.lang.model.element.Modifier;
import javax.ws.rs.QueryParam;

/**
 * Created by Jean-Philippe Belanger on 10/30/16.
 * Just potential zeroes and ones
 */
public class MethodDeclaration implements MethodBuilder {

    private final MethodSpec.Builder builder;

    public MethodDeclaration(MethodSpec.Builder spec) {

        this.builder = spec;
        builder.addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC);
    }

    @Override
    public MethodBuilder addParameter(String name, String type) {

        ParameterSpec.Builder param = ParameterSpec.builder(TypeName.INT, Names.buildVariableName(name));
        AnnotationSpec.Builder annotation = AnnotationSpec.builder(QueryParam.class);
        annotation.addMember("value","$S", name);
        param.addAnnotation(annotation.build());

        builder.addParameter(param.build());
        return this;
    }
}
