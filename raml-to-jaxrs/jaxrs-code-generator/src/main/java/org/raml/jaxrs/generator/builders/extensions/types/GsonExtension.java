package org.raml.jaxrs.generator.builders.extensions.types;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.FieldSpec;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

/**
 * Created by Jean-Philippe Belanger on 12/15/16.
 * Just potential zeroes and ones
 */
public class GsonExtension extends TypeExtensionHelper {

    @Override
    public void onFieldImplementation(CurrentBuild currentBuild, FieldSpec.Builder fieldSpec,
            TypeDeclaration typeDeclaration) {

        fieldSpec.addAnnotation(AnnotationSpec.builder(SerializedName.class).addMember("value", "$S", typeDeclaration.name()).build());
        fieldSpec.addAnnotation(AnnotationSpec.builder(Expose.class).build());
    }
}
