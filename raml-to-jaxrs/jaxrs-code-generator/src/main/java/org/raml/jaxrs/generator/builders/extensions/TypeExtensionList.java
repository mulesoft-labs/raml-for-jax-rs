package org.raml.jaxrs.generator.builders.extensions;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jean-Philippe Belanger on 11/30/16.
 * Just potential zeroes and ones
 */
public class TypeExtensionList implements TypeExtension {

    private List<TypeExtension> extensions = new ArrayList<>();


    @Override
    public void onTypeImplementation(TypeSpec.Builder typeSpec, TypeDeclaration typeDeclaration) {

        for (TypeExtension extension : extensions) {
            extension.onTypeImplementation(typeSpec, typeDeclaration);
        }
    }

    @Override
    public void onFieldImplementation(FieldSpec.Builder fieldSpec, TypeDeclaration typeDeclaration) {

        for (TypeExtension extension : extensions) {
            extension.onFieldImplementation(fieldSpec, typeDeclaration);
        }
    }

    @Override
    public void onGetterMethodImplementation(MethodSpec.Builder methodSpec, TypeDeclaration typeDeclaration) {

        for (TypeExtension extension : extensions) {
            extension.onGetterMethodImplementation(methodSpec, typeDeclaration);
        }
    }


    @Override
    public void onSetterMethodImplementation(MethodSpec.Builder typeSpec, ParameterSpec.Builder param,
            TypeDeclaration typeDeclaration) {

        for (TypeExtension extension : extensions) {
            extension.onSetterMethodImplementation(typeSpec, param, typeDeclaration);
        }

    }

    @Override
    public void onTypeDeclaration(TypeSpec.Builder typeSpec, TypeDeclaration typeDeclaration) {

        for (TypeExtension extension : extensions) {
            extension.onTypeDeclaration(typeSpec, typeDeclaration);
        }

    }

    @Override
    public void onGetterMethodDeclaration(MethodSpec.Builder methodSpec, TypeDeclaration typeDeclaration) {

        for (TypeExtension extension : extensions) {
            extension.onGetterMethodDeclaration(methodSpec, typeDeclaration);
        }
    }

    @Override
    public void onSetterMethodDeclaration(MethodSpec.Builder typeSpec, ParameterSpec.Builder param,
            TypeDeclaration typeDeclaration) {

        for (TypeExtension extension : extensions) {
            extension.onSetterMethodDeclaration(typeSpec, param, typeDeclaration);
        }
    }

    public void addExtension(TypeExtension typeExtension) {
        extensions.add(typeExtension);
    }
}
