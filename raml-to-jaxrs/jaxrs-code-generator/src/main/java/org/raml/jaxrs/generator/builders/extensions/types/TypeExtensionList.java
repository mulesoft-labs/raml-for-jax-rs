package org.raml.jaxrs.generator.builders.extensions.types;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.extension.types.LegacyTypeExtension;
import org.raml.jaxrs.generator.v10.V10GType;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jean-Philippe Belanger on 11/30/16.
 * Just potential zeroes and ones
 */
public class TypeExtensionList implements LegacyTypeExtension {

    private List<LegacyTypeExtension> extensions = new ArrayList<>();


    @Override
    public void onTypeImplementation(CurrentBuild currentBuild, TypeSpec.Builder typeSpec, TypeDeclaration typeDeclaration) {

        for (LegacyTypeExtension extension : extensions) {
            extension.onTypeImplementation(currentBuild, typeSpec, typeDeclaration);
        }
    }

    @Override
    public void onFieldImplementation(CurrentBuild currentBuild, FieldSpec.Builder fieldSpec,
            TypeDeclaration typeDeclaration) {

        for (LegacyTypeExtension extension : extensions) {
            extension.onFieldImplementation(currentBuild, fieldSpec, typeDeclaration);
        }
    }

    @Override
    public void onGetterMethodImplementation(CurrentBuild currentBuild, MethodSpec.Builder methodSpec,
            TypeDeclaration typeDeclaration) {

        for (LegacyTypeExtension extension : extensions) {
            extension.onGetterMethodImplementation(currentBuild, methodSpec, typeDeclaration);
        }
    }


    @Override
    public void onSetterMethodImplementation(CurrentBuild currentBuild, MethodSpec.Builder typeSpec,
            ParameterSpec.Builder param,
            TypeDeclaration typeDeclaration) {

        for (LegacyTypeExtension extension : extensions) {
            extension.onSetterMethodImplementation(currentBuild, typeSpec, param, typeDeclaration);
        }

    }

    @Override
    public void onTypeDeclaration(CurrentBuild currentBuild, TypeSpec.Builder typeSpec, V10GType type) {

        for (LegacyTypeExtension extension : extensions) {
            extension.onTypeDeclaration(currentBuild, typeSpec, type);
        }

    }

    @Override
    public void onGetterMethodDeclaration(CurrentBuild currentBuild, MethodSpec.Builder methodSpec,
            TypeDeclaration typeDeclaration) {

        for (LegacyTypeExtension extension : extensions) {
            extension.onGetterMethodDeclaration(currentBuild, methodSpec, typeDeclaration);
        }
    }

    @Override
    public void onSetterMethodDeclaration(CurrentBuild currentBuild, MethodSpec.Builder typeSpec,
            ParameterSpec.Builder param,
            TypeDeclaration typeDeclaration) {

        for (LegacyTypeExtension extension : extensions) {
            extension.onSetterMethodDeclaration(currentBuild, typeSpec, param, typeDeclaration);
        }
    }

    @Override
    public void onEnumConstant(CurrentBuild currentBuild, TypeSpec.Builder builder, TypeDeclaration typeDeclaration,
            String name) {

        for (LegacyTypeExtension extension : extensions) {
            extension.onEnumConstant(currentBuild, builder, typeDeclaration, name);
        }
    }

    @Override
    public void onEnumerationClass(CurrentBuild currentBuild, TypeSpec.Builder builder, TypeDeclaration typeDeclaration) {

        for (LegacyTypeExtension extension : extensions) {
            extension.onEnumerationClass(currentBuild, builder, typeDeclaration);
        }
    }

    @Override
    public void onEnumField(CurrentBuild currentBuild, FieldSpec.Builder field, TypeDeclaration typeDeclaration) {

        for (LegacyTypeExtension extension : extensions) {
            extension.onEnumField(currentBuild, field, typeDeclaration);
        }
    }

    @Override
    public void onUnionType(CurrentBuild currentBuild, TypeSpec.Builder builder, V10GType typeDeclaration) {

        for (LegacyTypeExtension extension : extensions) {
            extension.onUnionType(currentBuild, builder, typeDeclaration);
        }
    }

    public void addExtension(LegacyTypeExtension legacyTypeExtension) {
        extensions.add(legacyTypeExtension);
    }
}
