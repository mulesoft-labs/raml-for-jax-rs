package org.raml.jaxrs.generator.extension;

import com.squareup.javapoet.TypeSpec;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.v10.V10GMethod;

/**
 * Created by Jean-Philippe Belanger on 1/12/17.
 * Just potential zeroes and ones
 */
public interface ResponseClassExtension {

    ResponseClassExtension NULL_EXTENSION = new ResponseClassExtension() {

        @Override
        public TypeSpec.Builder onMethod(CurrentBuild build, V10GMethod method, TypeSpec.Builder typeSpec) {
            return typeSpec;
        }
    };

    TypeSpec.Builder onMethod(CurrentBuild build, V10GMethod method, TypeSpec.Builder typeSpec);

}
