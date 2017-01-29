package org.raml.jaxrs.generator.builders.extensions.resources;

import com.squareup.javapoet.MethodSpec;
import org.raml.jaxrs.generator.extension.resources.ResourceContext;
import org.raml.jaxrs.generator.extension.resources.ResponseMethodExtension;
import org.raml.jaxrs.generator.ramltypes.GResponse;

/**
 * Created by Jean-Philippe Belanger on 1/12/17.
 * Just potential zeroes and ones
 */
public class TrialResponseMethodExtension implements ResponseMethodExtension<GResponse> {

    @Override
    public MethodSpec.Builder onMethod(ResourceContext context, GResponse method, MethodSpec.Builder methodSpec) {

        return methodSpec;
    }
}
