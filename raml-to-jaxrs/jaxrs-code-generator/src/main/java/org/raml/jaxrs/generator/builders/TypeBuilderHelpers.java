package org.raml.jaxrs.generator.builders;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.GenerationException;
import org.raml.jaxrs.generator.Names;

import javax.lang.model.element.Modifier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

                String[] pAndA = splitIntoPackageAndClass(name);

                getSpec.returns(ClassName.get(pAndA[0], pAndA[1]));
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
                String[] pAndA = splitIntoPackageAndClass(typeName);
                ParameterSpec.Builder builder = ParameterSpec
                        .builder(ClassName.get(pAndA[0], pAndA[1]), name);
                fix(builder, fixers);

                getSpec.addParameter(
                        builder
                                .build());
            }
        };
    }

    public static TypeDescriber forField(final TypeSpec.Builder getSpec, final String name, final SpecFixer<FieldSpec.Builder>... fixers) {
        return new TypeDescriber() {
            @Override
            public void asJavaType(CurrentBuild currentBuild, Class c) {

                FieldSpec.Builder builder = FieldSpec.builder(c, name).addModifiers(Modifier.PRIVATE);
                fix(builder, fixers);
                getSpec.addField(builder.build());
            }

            @Override
            public void asBuiltType(CurrentBuild currentBuild, String typeName) {

                String[] pAndA = splitIntoPackageAndClass(typeName);
                FieldSpec.Builder builder = FieldSpec.builder(
                        ClassName.get(pAndA[0], pAndA[1]), name)
                        .addModifiers(Modifier.PRIVATE);

                fix(builder, fixers);
                getSpec.addField(
                        builder
                                .build()
                );
            }
        };
    }

    private static final Pattern SPLIT = Pattern.compile("^(.*)\\.([^.]+)$");
    private static String[] splitIntoPackageAndClass(String name) {

        Matcher m = SPLIT.matcher(name);
        if ( m.matches()) {

            return new String[] {m.group(1), m.group(2)};
        }

        throw new GenerationException("invalid generated class name: " + name);
    }
}
