package org.raml.jaxrs.generator.extension.resources;

import com.squareup.javapoet.TypeSpec;
import org.raml.jaxrs.generator.extension.Context;
import org.raml.jaxrs.generator.v10.V10GResource;

/**
 * Created by Jean-Philippe Belanger on 1/12/17.
 * Just potential zeroes and ones
 */
public interface ResourceClassExtension {
    ResourceClassExtension NULL_EXTENSION = new ResourceClassExtension() {

        @Override
        public TypeSpec.Builder onResource(Context context, V10GResource resource, TypeSpec.Builder typeSpec) {
            return typeSpec;
        }
    };

    TypeSpec.Builder onResource(Context context, V10GResource resource, TypeSpec.Builder typeSpec);
}
