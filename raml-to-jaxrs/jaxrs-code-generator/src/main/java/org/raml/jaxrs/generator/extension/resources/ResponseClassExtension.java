package org.raml.jaxrs.generator.extension.resources;

import com.squareup.javapoet.TypeSpec;
import org.raml.jaxrs.generator.extension.Context;
import org.raml.jaxrs.generator.v10.V10GMethod;

/**
 * Created by Jean-Philippe Belanger on 1/12/17.
 * Just potential zeroes and ones
 */
public interface ResponseClassExtension {

    ResponseClassExtension NULL_EXTENSION = new ResponseClassExtension() {

        @Override
        public TypeSpec.Builder onMethod(Context context, V10GMethod method, TypeSpec.Builder typeSpec) {
            return typeSpec;
        }
    };

    TypeSpec.Builder onMethod(Context context, V10GMethod method, TypeSpec.Builder typeSpec);

}
