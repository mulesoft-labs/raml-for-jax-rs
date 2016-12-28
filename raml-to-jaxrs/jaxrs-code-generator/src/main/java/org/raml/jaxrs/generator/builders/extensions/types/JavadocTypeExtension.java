package org.raml.jaxrs.generator.builders.extensions.types;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.v2.api.model.v10.datamodel.ExampleSpec;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

/**
 * Created by Jean-Philippe Belanger on 12/4/16.
 * Just potential zeroes and ones
 */
public class JavadocTypeExtension extends TypeExtensionHelper {

    private interface JavadocAdder {

        void addJavadoc(String format, Object... args);
    }

    @Override
    public void onTypeDeclaration(CurrentBuild currentBuild, final TypeSpec.Builder typeSpec,
            TypeDeclaration typeDeclaration) {

        if ( typeDeclaration.description() != null ) {
            typeSpec.addJavadoc("$L\n", typeDeclaration.description().value());
        }

        javadocExamples(new JavadocAdder() {
            @Override
            public void addJavadoc(String format, Object... args) {

                typeSpec.addJavadoc(format, args);
            }
        }, typeDeclaration);
    }

    @Override
    public void onGetterMethodDeclaration(CurrentBuild currentBuild, final MethodSpec.Builder typeSpec,
            TypeDeclaration typeDeclaration) {
        if ( typeDeclaration.description() != null ) {
            typeSpec.addJavadoc("$L\n", typeDeclaration.description().value());
        }

        javadocExamples(new JavadocAdder() {
            @Override
            public void addJavadoc(String format, Object... args) {
                typeSpec.addJavadoc(format, args);
            }
        }, typeDeclaration);
    }

    public void javadocExamples(JavadocAdder adder, TypeDeclaration typeDeclaration) {
        ExampleSpec example = typeDeclaration.example();
        if ( example != null ) {

            javadoc(adder, example);
        }

        for (ExampleSpec exampleSpec : typeDeclaration.examples()) {
            javadoc(adder, exampleSpec);
        }
    }

    public void javadoc(JavadocAdder adder, ExampleSpec exampleSpec) {
        adder.addJavadoc("Example:\n");

        if ( exampleSpec.name() != null ) {
            adder.addJavadoc(" $L\n", exampleSpec.name());
        }

        adder.addJavadoc(" $L\n",  "<pre>\n{@code\n" + exampleSpec.value() + "\n}</pre>");
    }
}
