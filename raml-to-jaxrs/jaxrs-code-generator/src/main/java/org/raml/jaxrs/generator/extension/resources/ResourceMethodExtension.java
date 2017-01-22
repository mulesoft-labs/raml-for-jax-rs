package org.raml.jaxrs.generator.extension.resources;

import com.squareup.javapoet.MethodSpec;
import org.raml.jaxrs.generator.extension.Context;
import org.raml.jaxrs.generator.ramltypes.GMethod;

/**
 * Created by Jean-Philippe Belanger on 1/12/17.
 * Just potential zeroes and ones
 */
public interface ResourceMethodExtension<T extends GMethod> {

    ResourceMethodExtension<GMethod> NULL_EXTENSION = new ResourceMethodExtension<GMethod>() {

        @Override
        public MethodSpec.Builder onMethod(Context context, GMethod method, MethodSpec.Builder methodSpec) {
            return methodSpec;
        }
    };

    MethodSpec.Builder onMethod(Context context, T method, MethodSpec.Builder methodSpec);

}
