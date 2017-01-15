package org.raml.jaxrs.generator.extension;

import com.squareup.javapoet.MethodSpec;
import org.raml.jaxrs.generator.v10.V10GMethod;

/**
 * Created by Jean-Philippe Belanger on 1/12/17.
 * Just potential zeroes and ones
 */
public interface MethodExtension {

    MethodExtension NULL_EXTENSION = new MethodExtension() {

        @Override
        public MethodSpec.Builder onMethod(Context context, V10GMethod method, MethodSpec.Builder methodSpec) {
            return methodSpec;
        }
    };

    MethodSpec.Builder onMethod(Context context, V10GMethod method, MethodSpec.Builder methodSpec);

}
