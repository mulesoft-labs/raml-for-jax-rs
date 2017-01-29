package org.raml.jaxrs.generator.builders.extensions.resources;

import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.builders.extensions.ContextImpl;
import org.raml.jaxrs.generator.extension.resources.ResourceContext;

/**
 * Created by Jean-Philippe Belanger on 1/29/17.
 * Just potential zeroes and ones
 */
public class ResourceContextImpl extends ContextImpl implements ResourceContext {

    public ResourceContextImpl(CurrentBuild build) {
        super(build);
    }
}
