package org.raml.jaxrs.generator.extension.types;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.raml.jaxrs.generator.extension.Context;
import org.raml.jaxrs.generator.v10.V10GProperty;
import org.raml.jaxrs.generator.v10.V10GType;

/**
 * Created by Jean-Philippe Belanger on 1/26/17.
 * Just potential zeroes and ones
 */
public interface PropertyExtension {

    /* enough ... ? */
    void onProperty(Context context, TypeSpec.Builder builder, V10GType containingType, V10GProperty property);
    void onProperty(Context context, FieldSpec.Builder builder, V10GType containingType, V10GProperty property);
    void onProperty(Context context, MethodSpec.Builder builder, V10GType containingType, V10GProperty property);
}
