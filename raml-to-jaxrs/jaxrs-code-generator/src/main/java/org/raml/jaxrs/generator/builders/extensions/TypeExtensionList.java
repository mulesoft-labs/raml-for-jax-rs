package org.raml.jaxrs.generator.builders.extensions;

import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jean-Philippe Belanger on 11/30/16.
 * Just potential zeroes and ones
 */
public class TypeExtensionList implements TypeExtension {

    private List<TypeExtension> extensions = new ArrayList<>();

    @Override
    public void onTypeDeclaration() {

        for (TypeExtension extension : extensions) {
            extension.onTypeDeclaration();
        }
    }

    @Override
    public void onTypeImplementation(TypeSpec.Builder typeSpec) {

        for (TypeExtension extension : extensions) {
            extension.onTypeImplementation(typeSpec);
        }
    }

    @Override
    public void onFieldDeclaration() {

    }

    @Override
    public void onMethodDeclaration() {

    }

    public void addExtension(TypeExtension typeExtension) {
        extensions.add(typeExtension);
    }
}
