package org.raml.jaxrs.generator.extension.resources;

import com.squareup.javapoet.MethodSpec;
import org.raml.jaxrs.generator.extension.Context;
import org.raml.jaxrs.generator.ramltypes.GResponse;

/**
 * Created by Jean-Philippe Belanger on 1/12/17.
 * Just potential zeroes and ones
 */
public interface ResponseMethodExtension<T extends GResponse> {

    ResponseMethodExtension<GResponse> NULL_EXTENSION = new ResponseMethodExtension<GResponse>() {

        @Override
        public MethodSpec.Builder onMethod(Context context, GResponse method, MethodSpec.Builder methodSpec) {
            return methodSpec;
        }
    };

    MethodSpec.Builder onMethod(Context context, T method, MethodSpec.Builder methodSpec);
}
