package org.raml.jaxrs.generator.extension.resources;

import com.squareup.javapoet.MethodSpec;
import org.raml.jaxrs.generator.extension.Context;
import org.raml.jaxrs.generator.v10.V10GMethod;

/**
 * Created by Jean-Philippe Belanger on 1/12/17.
 * Just potential zeroes and ones
 */
public interface ResourceMethodExtension {

    ResourceMethodExtension NULL_EXTENSION = new ResourceMethodExtension() {

        @Override
        public MethodSpec.Builder onMethod(Context context, V10GMethod method, MethodSpec.Builder methodSpec) {
            return methodSpec;
        }
    };

    MethodSpec.Builder onMethod(Context context, V10GMethod method, MethodSpec.Builder methodSpec);

}
