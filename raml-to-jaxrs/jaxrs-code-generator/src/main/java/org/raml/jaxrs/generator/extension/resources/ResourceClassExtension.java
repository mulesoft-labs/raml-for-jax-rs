package org.raml.jaxrs.generator.extension.resources;

import com.squareup.javapoet.TypeSpec;
import org.raml.jaxrs.generator.extension.Context;
import org.raml.jaxrs.generator.ramltypes.GResource;

/**
 * Created by Jean-Philippe Belanger on 1/12/17.
 * Just potential zeroes and ones
 */
public interface ResourceClassExtension<T extends GResource> {
    ResourceClassExtension NULL_EXTENSION = new ResourceClassExtension<GResource>() {

        @Override
        public TypeSpec.Builder onResource(Context context, GResource resource, TypeSpec.Builder typeSpec) {
            return typeSpec;
        }
    };

    TypeSpec.Builder onResource(Context context, T resource, TypeSpec.Builder typeSpec);
}
