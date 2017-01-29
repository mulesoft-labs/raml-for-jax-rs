package org.raml.jaxrs.generator.builders.extensions.resources;

import com.squareup.javapoet.MethodSpec;
import org.raml.jaxrs.generator.extension.resources.ResourceContext;
import org.raml.jaxrs.generator.extension.resources.ResourceMethodExtension;
import org.raml.jaxrs.generator.ramltypes.GMethod;

/**
 * Created by Jean-Philippe Belanger on 1/6/17.
 * Just potential zeroes and ones
 */
public class TrialResourceMethodExtension implements ResourceMethodExtension<GMethod> {

    @Override
    public MethodSpec.Builder onMethod(ResourceContext context, GMethod method, MethodSpec.Builder methodSpec) {
        return methodSpec;
    }


}
