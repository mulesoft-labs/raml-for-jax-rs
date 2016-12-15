package org.raml.jaxrs.generator.builders.extensions;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

/**
 * Created by Jean-Philippe Belanger on 12/4/16.
 * Just potential zeroes and ones
 */
public class TypeExtensionHelper implements TypeExtension {
    @Override
    public void onTypeImplementation(TypeSpec.Builder typeSpec, TypeDeclaration typeDeclaration) {

    }

    @Override
    public void onFieldImplementation(FieldSpec.Builder typeSpec, TypeDeclaration typeDeclaration) {

    }

    @Override
    public void onGetterMethodImplementation(MethodSpec.Builder typeSpec, TypeDeclaration typeDeclaration) {

    }

    @Override
    public void onSetterMethodImplementation(MethodSpec.Builder typeSpec, ParameterSpec.Builder param,
            TypeDeclaration typeDeclaration) {

    }

    @Override
    public void onTypeDeclaration(TypeSpec.Builder typeSpec, TypeDeclaration typeDeclaration) {

    }

    @Override
    public void onGetterMethodDeclaration(MethodSpec.Builder typeSpec, TypeDeclaration typeDeclaration) {

    }

    @Override
    public void onSetterMethodDeclaration(MethodSpec.Builder typeSpec, ParameterSpec.Builder param,
            TypeDeclaration typeDeclaration) {

    }
}
