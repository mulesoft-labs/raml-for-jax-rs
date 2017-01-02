package org.raml.jaxrs.generator.builders.extensions.types;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.TypeSpec;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.GType;
import org.raml.jaxrs.generator.Names;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

/**
 * Created by Jean-Philippe Belanger on 1/1/17.
 * Just potential zeroes and ones
 */
public class JacksonDiscriminatorInheritanceTypeExtension extends TypeExtensionHelper {

    @Override
    public void onTypeDeclaration(CurrentBuild currentBuild, TypeSpec.Builder typeSpec, TypeDeclaration typeDeclaration) {

        ObjectTypeDeclaration otr = (ObjectTypeDeclaration) typeDeclaration;

        if ( otr.discriminator() != null  && currentBuild.childClasses(typeDeclaration.name()).size() > 0 ) {

                typeSpec.addAnnotation(
                        AnnotationSpec.builder(JsonTypeInfo.class)
                                .addMember("use", "$T.Id.NAME", JsonTypeInfo.class)
                                .addMember("include", "$T.As.PROPERTY", JsonTypeInfo.class)
                                .addMember("property", "$S", otr.discriminator())
                                .build()
                );

                AnnotationSpec.Builder subTypes = AnnotationSpec.builder(JsonSubTypes.class);
                for (GType gType : currentBuild.childClasses(typeDeclaration.name())) {

                    subTypes.addMember("value", "$L",
                            AnnotationSpec.builder(JsonSubTypes.Type.class).addMember("value", "$L", gType.defaultJavaTypeName() + "Impl.class").build()
                    );
                }

                typeSpec.addAnnotation(
                        subTypes.build()
                );

            }

            if ( currentBuild.childClasses(typeDeclaration.name()).size() == 0) {

                typeSpec.addAnnotation(AnnotationSpec.builder(JsonDeserialize.class).addMember("as", "$L.class", Names.typeName(
                        typeDeclaration.name()) + "Impl").build());
            }
    }
}
