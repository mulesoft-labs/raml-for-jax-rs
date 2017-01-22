package org.raml.jaxrs.generator.extension.resources;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.raml.jaxrs.generator.extension.Context;
import org.raml.jaxrs.generator.ramltypes.GMethod;
import org.raml.jaxrs.generator.ramltypes.GResource;
import org.raml.jaxrs.generator.ramltypes.GResponse;

/**
 * Created by Jean-Philippe Belanger on 1/22/17.
 * Just potential zeroes and ones
 *
 * QUick and dirty
 */
public interface GlobalResourceExtension<M extends GMethod, R extends GResource, S extends GResponse> extends
        ResponseClassExtension<M>,
        ResourceClassExtension<R>,
        ResponseMethodExtension<S>,
        ResourceMethodExtension<M> {

    GlobalResourceExtension<GMethod, GResource, GResponse> NULL_EXTENSION = new GlobalResourceExtension<GMethod, GResource, GResponse>() {
        @Override
        public TypeSpec.Builder onResource(Context context, GResource resource, TypeSpec.Builder typeSpec) {
            return typeSpec;
        }

        @Override
        public MethodSpec.Builder onMethod(Context context, GMethod method, MethodSpec.Builder methodSpec) {
            return methodSpec;
        }

        @Override
        public TypeSpec.Builder onMethod(Context context, GMethod method, TypeSpec.Builder typeSpec) {
            return typeSpec;
        }

        @Override
        public MethodSpec.Builder onMethod(Context context, GResponse method, MethodSpec.Builder methodSpec) {
            return methodSpec;
        }
    };

}
