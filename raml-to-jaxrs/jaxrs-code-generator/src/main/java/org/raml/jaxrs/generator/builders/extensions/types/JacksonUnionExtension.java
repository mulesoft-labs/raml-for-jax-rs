package org.raml.jaxrs.generator.builders.extensions.types;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeSpec;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.Names;
import org.raml.jaxrs.generator.v10.V10GType;
import org.raml.v2.api.model.v10.datamodel.UnionTypeDeclaration;

/**
 * Created by Jean-Philippe Belanger on 1/2/17.
 * Just potential zeroes and ones
 */
public class JacksonUnionExtension extends TypeExtensionHelper {

    @Override
    public void onUnionType(CurrentBuild currentBuild, TypeSpec.Builder builder, V10GType typeDeclaration) {

        if (! (typeDeclaration.implementation() instanceof UnionTypeDeclaration)) {

            return;
        }

        UnionTypeDeclaration unionTypeDeclaration = (UnionTypeDeclaration) typeDeclaration.implementation();
        ClassName deserializer = ClassName.get(currentBuild.getSupportPackage(), Names.typeName(unionTypeDeclaration.name(), "deserializer"));
        ClassName serializer = ClassName.get(currentBuild.getSupportPackage(), Names.typeName(unionTypeDeclaration.name(), "serializer"));
        builder.addAnnotation(
                AnnotationSpec.builder(JsonDeserialize.class).addMember(
                        "using", "$T.class", deserializer
                ).build());
        builder.addAnnotation(
                AnnotationSpec.builder(JsonSerialize.class).addMember(
                        "using", "$T.class", serializer
                ).build());

        currentBuild.newSupportGenerator(new UnionDeserializationGenerator(currentBuild, typeDeclaration, deserializer));
        currentBuild.newSupportGenerator(new UnionSerializationGenerator(currentBuild, typeDeclaration, serializer));

    }
}
