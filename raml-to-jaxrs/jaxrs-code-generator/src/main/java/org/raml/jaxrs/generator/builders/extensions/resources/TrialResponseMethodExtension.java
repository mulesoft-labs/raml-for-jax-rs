package org.raml.jaxrs.generator.builders.extensions.resources;

import com.squareup.javapoet.MethodSpec;
import org.raml.jaxrs.generator.extension.Context;
import org.raml.jaxrs.generator.extension.resources.ResponseMethodExtension;
import org.raml.jaxrs.generator.ramltypes.GResponse;
import org.raml.jaxrs.generator.v10.V10GResponse;

/**
 * Created by Jean-Philippe Belanger on 1/12/17.
 * Just potential zeroes and ones
 */
public class TrialResponseMethodExtension implements ResponseMethodExtension<GResponse> {

    @Override
    public MethodSpec.Builder onMethod(Context context, GResponse method, MethodSpec.Builder methodSpec) {

        return methodSpec;
    }
}
