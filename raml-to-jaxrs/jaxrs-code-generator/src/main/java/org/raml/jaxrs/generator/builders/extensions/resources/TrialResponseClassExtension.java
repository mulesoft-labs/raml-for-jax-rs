package org.raml.jaxrs.generator.builders.extensions.resources;

import com.squareup.javapoet.TypeSpec;
import org.raml.jaxrs.generator.extension.Context;
import org.raml.jaxrs.generator.extension.resources.ResponseClassExtension;
import org.raml.jaxrs.generator.ramltypes.GMethod;
import org.raml.jaxrs.generator.v10.V10GMethod;

/**
 * Created by Jean-Philippe Belanger on 1/12/17.
 * Just potential zeroes and ones
 */
public class TrialResponseClassExtension implements ResponseClassExtension<GMethod> {

    @Override
    public TypeSpec.Builder onMethod(Context context, GMethod response, TypeSpec.Builder typeSpec) {

        return typeSpec;
    }
}
