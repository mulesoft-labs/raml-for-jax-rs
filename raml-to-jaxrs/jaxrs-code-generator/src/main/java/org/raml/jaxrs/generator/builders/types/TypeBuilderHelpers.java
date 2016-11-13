package org.raml.jaxrs.generator.builders.types;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.Names;

import javax.lang.model.element.Modifier;

/**
 * Created by Jean-Philippe Belanger on 11/13/16.
 * Just potential zeroes and ones
 */
public class TypeBuilderHelpers {

    static TypeDescriber forReturnValue(final CurrentBuild build, final MethodSpec.Builder getSpec) {
        return new TypeDescriber() {
            @Override
            public void asJavaType(Class c) {

                getSpec.returns(c);
            }

            @Override
            public void asBuiltType(String name) {

                getSpec.returns(ClassName.get(build.getDefaultPackage(), Names.buildTypeName(name)));
            }
        };
    }


    static  TypeDescriber forParameter(final CurrentBuild build, final MethodSpec.Builder getSpec, final String name) {
        return new TypeDescriber() {
            @Override
            public void asJavaType(Class c) {

                    getSpec.addParameter(ParameterSpec.builder(c, name).build());
            }

            @Override
            public void asBuiltType(String name) {
                getSpec.addParameter(
                        ParameterSpec.builder(ClassName.get(build.getDefaultPackage(), Names.buildTypeName(name)), name)
                                .build());
            }
        };
    }

    static  TypeDescriber forField(final CurrentBuild build, final TypeSpec.Builder getSpec, final String name) {
        return new TypeDescriber() {
            @Override
            public void asJavaType(Class c) {

                getSpec.addField(FieldSpec.builder(c, name).addModifiers(Modifier.PRIVATE).build());
            }

            @Override
            public void asBuiltType(String name) {
                getSpec.addField(
                        FieldSpec.builder(
                                ClassName.get(build.getDefaultPackage(), Names.buildTypeName(name)), name)
                                .addModifiers(Modifier.PRIVATE)
                                .build()
                );
            }
        };
    }

}
