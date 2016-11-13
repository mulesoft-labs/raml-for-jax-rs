package org.raml.jaxrs.generator.builders;

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

    public static TypeDescriber forReturnValue(final MethodSpec.Builder getSpec) {
        return new TypeDescriber() {
            @Override
            public void asJavaType(CurrentBuild currentBuild, Class c) {

                getSpec.returns(c);
            }

            @Override
            public void asBuiltType(CurrentBuild currentBuild, String name) {

                getSpec.returns(ClassName.get(currentBuild.getDefaultPackage(), Names.buildTypeName(name)));
            }
        };
    }


    private static<T> void fix(T builder, SpecFixer<T>... fixers) {

        for (SpecFixer<T> fixer : fixers) {
            fixer.adjust(builder);
        }
    }

    public static  TypeDescriber forParameter(final MethodSpec.Builder getSpec, final String name, final SpecFixer<ParameterSpec.Builder>... fixers) {
        return new TypeDescriber() {
            @Override
            public void asJavaType(CurrentBuild currentBuild, Class c) {

                ParameterSpec.Builder builder = ParameterSpec.builder(c, name);
                fix(builder, fixers);

                getSpec.addParameter(builder.build());
            }

            @Override
            public void asBuiltType(CurrentBuild currentBuild, String typeName) {
                ParameterSpec.Builder builder = ParameterSpec
                        .builder(ClassName.get(currentBuild.getDefaultPackage(), Names.buildTypeName(typeName)), name);
                fix(builder, fixers);

                getSpec.addParameter(
                        builder
                                .build());
            }
        };
    }

    public static TypeDescriber forField(final TypeSpec.Builder getSpec, final String name) {
        return new TypeDescriber() {
            @Override
            public void asJavaType(CurrentBuild currentBuild, Class c) {

                getSpec.addField(FieldSpec.builder(c, name).addModifiers(Modifier.PRIVATE).build());
            }

            @Override
            public void asBuiltType(CurrentBuild currentBuild, String name) {
                getSpec.addField(
                        FieldSpec.builder(
                                ClassName.get(currentBuild.getDefaultPackage(), Names.buildTypeName(name)), name)
                                .addModifiers(Modifier.PRIVATE)
                                .build()
                );
            }
        };
    }

}
