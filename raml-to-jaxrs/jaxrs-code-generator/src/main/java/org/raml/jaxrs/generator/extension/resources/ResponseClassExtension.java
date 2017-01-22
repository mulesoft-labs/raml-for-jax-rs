package org.raml.jaxrs.generator.extension.resources;

import com.squareup.javapoet.TypeSpec;
import org.raml.jaxrs.generator.extension.Context;
import org.raml.jaxrs.generator.ramltypes.GMethod;

/**
 * Created by Jean-Philippe Belanger on 1/12/17.
 * Just potential zeroes and ones
 */
public interface ResponseClassExtension<T extends GMethod> {

    ResponseClassExtension<GMethod> NULL_EXTENSION = new ResponseClassExtension<GMethod>() {

        @Override
        public TypeSpec.Builder onMethod(Context context, GMethod method, TypeSpec.Builder typeSpec) {
            return typeSpec;
        }
    };

    TypeSpec.Builder onMethod(Context context, T method, TypeSpec.Builder typeSpec);

}
