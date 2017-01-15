package org.raml.jaxrs.generator.extension;

import com.squareup.javapoet.MethodSpec;
import org.raml.jaxrs.generator.v10.V10GResponse;

/**
 * Created by Jean-Philippe Belanger on 1/12/17.
 * Just potential zeroes and ones
 */
public interface ResponseMethodExtension {

    ResponseMethodExtension NULL_EXTENSION = new ResponseMethodExtension() {

        @Override
        public MethodSpec.Builder onMethod(Context context, V10GResponse method, MethodSpec.Builder methodSpec) {
            return methodSpec;
        }
    };

    MethodSpec.Builder onMethod(Context context, V10GResponse method, MethodSpec.Builder methodSpec);
}
