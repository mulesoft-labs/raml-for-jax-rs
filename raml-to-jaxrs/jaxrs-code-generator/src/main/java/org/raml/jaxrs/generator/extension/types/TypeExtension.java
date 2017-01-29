package org.raml.jaxrs.generator.extension.types;

import com.squareup.javapoet.TypeSpec;
import org.raml.jaxrs.generator.builders.BuildPhase;
import org.raml.jaxrs.generator.v10.V10GType;

/**
 * Created by Jean-Philippe Belanger on 1/26/17.
 * Just potential zeroes and ones
 */
public interface TypeExtension {

    TypeSpec.Builder onType(TypeContext context, TypeSpec.Builder builder, V10GType type, BuildPhase btype);
}
