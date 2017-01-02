package org.raml.jaxrs.generator.builders.extensions.types;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.Names;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.UnionTypeDeclaration;

/**
 * Created by Jean-Philippe Belanger on 1/2/17.
 * Just potential zeroes and ones
 */
public class JacksonUnionExtension extends TypeExtensionHelper {

    @Override
    public void onUnionType(CurrentBuild currentBuild, TypeSpec.Builder builder, TypeDeclaration typeDeclaration) {

        if (! (typeDeclaration instanceof UnionTypeDeclaration)) {

            return;
        }

        UnionTypeDeclaration unionTypeDeclaration = (UnionTypeDeclaration) typeDeclaration;
        ClassName deserializer = ClassName.get(currentBuild.getSupportPackage(), Names.typeName(unionTypeDeclaration.name(), "deserializer"));
        builder.addAnnotation(
                AnnotationSpec.builder(JsonDeserialize.class).addMember(
                        "using", "$T.class", deserializer
                ).build());

        currentBuild.newSupportGenerator(new UnionSerializationGenerator(currentBuild, unionTypeDeclaration, deserializer));

    }
}
