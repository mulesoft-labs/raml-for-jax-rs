package org.raml.jaxrs.generator.builders.extensions.resources;

import com.squareup.javapoet.TypeSpec;
import org.raml.jaxrs.generator.extension.Context;
import org.raml.jaxrs.generator.extension.resources.ResourceClassExtension;
import org.raml.jaxrs.generator.v10.V10GResource;

/**
 * Created by Jean-Philippe Belanger on 1/6/17.
 * Just potential zeroes and ones
 */
public class TrialResourceClassExtension implements ResourceClassExtension {

    @Override
    public TypeSpec.Builder onResource(Context context, V10GResource resource, TypeSpec.Builder typeSpec) {

        return typeSpec;
    }
}
