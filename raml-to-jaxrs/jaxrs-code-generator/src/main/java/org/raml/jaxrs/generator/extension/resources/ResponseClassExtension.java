package org.raml.jaxrs.generator.extension.resources;

import com.squareup.javapoet.TypeSpec;
import org.raml.jaxrs.generator.ramltypes.GMethod;

/**
 * Created by Jean-Philippe Belanger on 1/12/17.
 * Just potential zeroes and ones
 */
public interface ResponseClassExtension<T extends GMethod> {

    ResponseClassExtension<GMethod> NULL_EXTENSION = new ResponseClassExtension<GMethod>() {

        @Override
        public TypeSpec.Builder onMethod(ResourceContext context, GMethod method, TypeSpec.Builder typeSpec) {
            return typeSpec;
        }
    };

    TypeSpec.Builder onMethod(ResourceContext context, T method, TypeSpec.Builder typeSpec);

}
