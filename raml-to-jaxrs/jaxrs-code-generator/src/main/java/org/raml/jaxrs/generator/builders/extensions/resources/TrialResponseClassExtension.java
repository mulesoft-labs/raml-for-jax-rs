package org.raml.jaxrs.generator.builders.extensions.resources;

import com.squareup.javapoet.TypeSpec;
import org.raml.jaxrs.generator.extension.resources.ResourceContext;
import org.raml.jaxrs.generator.extension.resources.ResponseClassExtension;
import org.raml.jaxrs.generator.ramltypes.GMethod;

/**
 * Created by Jean-Philippe Belanger on 1/12/17.
 * Just potential zeroes and ones
 */
public class TrialResponseClassExtension implements ResponseClassExtension<GMethod> {

    @Override
    public TypeSpec.Builder onMethod(ResourceContext context, GMethod response, TypeSpec.Builder typeSpec) {

        return typeSpec;
    }
}
