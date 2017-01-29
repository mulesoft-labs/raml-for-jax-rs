package org.raml.jaxrs.generator.builders.extensions.resources;

import com.squareup.javapoet.TypeSpec;
import org.raml.jaxrs.generator.extension.resources.ResourceClassExtension;
import org.raml.jaxrs.generator.extension.resources.ResourceContext;
import org.raml.jaxrs.generator.ramltypes.GResource;

/**
 * Created by Jean-Philippe Belanger on 1/6/17.
 * Just potential zeroes and ones
 */
public class TrialResourceClassExtension implements ResourceClassExtension<GResource> {

    @Override
    public TypeSpec.Builder onResource(ResourceContext context, GResource resource, TypeSpec.Builder typeSpec) {

        return typeSpec;
    }
}
