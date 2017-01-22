package org.raml.jaxrs.generator.extension.types;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.v10.V10GType;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

/**
 * Created by Jean-Philippe Belanger on 11/30/16.
 * Just potential zeroes and ones
 * This interface is too big.
 */
public interface TypeExtension {

    void onTypeImplementation(CurrentBuild currentBuild, TypeSpec.Builder typeSpec, TypeDeclaration typeDeclaration);
    void onFieldImplementation(CurrentBuild currentBuild, FieldSpec.Builder typeSpec, TypeDeclaration typeDeclaration);
    void onGetterMethodImplementation(CurrentBuild currentBuild, MethodSpec.Builder typeSpec,
            TypeDeclaration typeDeclaration);
    void onSetterMethodImplementation(CurrentBuild currentBuild, MethodSpec.Builder typeSpec, ParameterSpec.Builder param,
            TypeDeclaration typeDeclaration);

    void onTypeDeclaration(CurrentBuild currentBuild, TypeSpec.Builder typeSpec, V10GType type);
    void onGetterMethodDeclaration(CurrentBuild currentBuild, MethodSpec.Builder typeSpec, TypeDeclaration typeDeclaration);
    void onSetterMethodDeclaration(CurrentBuild currentBuild, MethodSpec.Builder typeSpec, ParameterSpec.Builder param,
            TypeDeclaration typeDeclaration);

    void onEnumConstant(CurrentBuild currentBuild, TypeSpec.Builder builder, TypeDeclaration typeDeclaration, String name);
    void onEnumerationClass(CurrentBuild currentBuild, TypeSpec.Builder builder, TypeDeclaration typeDeclaration);
    void onEnumField(CurrentBuild currentBuild, FieldSpec.Builder field, TypeDeclaration typeDeclaration);

    void onUnionType(CurrentBuild currentBuild, TypeSpec.Builder builder, V10GType typeDeclaration);
}
