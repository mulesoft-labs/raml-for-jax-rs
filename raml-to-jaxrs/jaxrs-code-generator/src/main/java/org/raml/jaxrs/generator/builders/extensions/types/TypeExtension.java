package org.raml.jaxrs.generator.builders.extensions.types;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

/**
 * Created by Jean-Philippe Belanger on 11/30/16.
 * Just potential zeroes and ones
 */
public interface TypeExtension {

    void onTypeImplementation(TypeSpec.Builder typeSpec, TypeDeclaration typeDeclaration);
    void onFieldImplementation(FieldSpec.Builder typeSpec, TypeDeclaration typeDeclaration);
    void onGetterMethodImplementation(MethodSpec.Builder typeSpec, TypeDeclaration typeDeclaration);
    void onSetterMethodImplementation(MethodSpec.Builder typeSpec, ParameterSpec.Builder param, TypeDeclaration typeDeclaration);

    void onTypeDeclaration(TypeSpec.Builder typeSpec, TypeDeclaration typeDeclaration);
    void onGetterMethodDeclaration(MethodSpec.Builder typeSpec, TypeDeclaration typeDeclaration);
    void onSetterMethodDeclaration(MethodSpec.Builder typeSpec, ParameterSpec.Builder param, TypeDeclaration typeDeclaration);

    void onEnumConstant(TypeSpec.Builder builder, TypeDeclaration typeDeclaration, String name);
    void onEnumerationClass(TypeSpec.Builder builder, TypeDeclaration typeDeclaration);

    void onEnumField(FieldSpec.Builder field, TypeDeclaration typeDeclaration);
}
