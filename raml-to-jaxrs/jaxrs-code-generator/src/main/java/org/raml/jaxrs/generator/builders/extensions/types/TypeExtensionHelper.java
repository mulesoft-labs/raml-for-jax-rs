package org.raml.jaxrs.generator.builders.extensions.types;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.extension.types.LegacyTypeExtension;
import org.raml.jaxrs.generator.v10.V10GType;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

/**
 * Created by Jean-Philippe Belanger on 12/4/16.
 * Just potential zeroes and ones
 */
public class TypeExtensionHelper implements LegacyTypeExtension {
    @Override
    public void onTypeImplementation(CurrentBuild currentBuild, TypeSpec.Builder typeSpec, TypeDeclaration typeDeclaration) {

    }

    @Override
    public void onFieldImplementation(CurrentBuild currentBuild, FieldSpec.Builder typeSpec, TypeDeclaration typeDeclaration) {

    }

    @Override
    public void onGetterMethodImplementation(CurrentBuild currentBuild, MethodSpec.Builder typeSpec,
            TypeDeclaration typeDeclaration) {

    }

    @Override
    public void onSetterMethodImplementation(CurrentBuild currentBuild, MethodSpec.Builder typeSpec,
            ParameterSpec.Builder param,
            TypeDeclaration typeDeclaration) {

    }

    @Override
    public void onTypeDeclaration(CurrentBuild currentBuild, TypeSpec.Builder typeSpec, V10GType type) {

    }

    @Override
    public void onGetterMethodDeclaration(CurrentBuild currentBuild, MethodSpec.Builder typeSpec,
            TypeDeclaration typeDeclaration) {

    }

    @Override
    public void onSetterMethodDeclaration(CurrentBuild currentBuild, MethodSpec.Builder typeSpec,
            ParameterSpec.Builder param,
            TypeDeclaration typeDeclaration) {

    }

    @Override
    public void onEnumConstant(CurrentBuild currentBuild, TypeSpec.Builder builder, TypeDeclaration typeDeclaration,
            String name) {

    }

    @Override
    public void onEnumerationClass(CurrentBuild currentBuild, TypeSpec.Builder builder, TypeDeclaration typeDeclaration) {

    }

    @Override
    public void onEnumField(CurrentBuild currentBuild, FieldSpec.Builder field, TypeDeclaration typeDeclaration) {

    }

    @Override
    public void onUnionType(CurrentBuild currentBuild, TypeSpec.Builder builder, V10GType typeDeclaration) {

    }
}
