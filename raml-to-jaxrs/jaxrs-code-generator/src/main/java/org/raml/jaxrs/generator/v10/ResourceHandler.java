package org.raml.jaxrs.generator.v10;

import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.GAbstractionFactory;
import org.raml.jaxrs.generator.builders.resources.TopResource;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.resources.Resource;

/**
 * Created by Jean-Philippe Belanger on 10/26/16.
 * These handlers take care of different model types (v08 vs v10).
 */
public class ResourceHandler {

    private final CurrentBuild build;

    public ResourceHandler(CurrentBuild build) {
        this.build = build;
    }

    public void handle(final Resource resource) {

        GAbstractionFactory factory = new GAbstractionFactory();

        TopResource rg = new TopResource(build, factory.newResource(resource), resource.displayName().value(),
                resource.relativeUri().value());

        build.newResource(rg);
    }
}
