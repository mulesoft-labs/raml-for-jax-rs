package org.raml.jaxrs.generator.v10;

import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.GAbstractionFactory;
import org.raml.jaxrs.generator.builders.resources.ResourceBuilder;
import org.raml.jaxrs.generator.v08.V08TypeRegistry;
import org.raml.v2.api.model.v10.resources.Resource;

import java.util.Set;

/**
 * Created by Jean-Philippe Belanger on 10/26/16.
 * These handlers take care of different model types (v08 vs v10).
 */
public class ResourceHandler {

    private final CurrentBuild build;

    public ResourceHandler(CurrentBuild build) {
        this.build = build;
    }

    public void handle(V10TypeRegistry registry, final Resource resource) {

        GAbstractionFactory factory = new GAbstractionFactory();

        ResourceBuilder rg = new ResourceBuilder(build, factory.newResource(registry, resource), resource.displayName().value(),
                resource.relativeUri().value());

        build.newResource(rg);
    }

    public void handle(Set<String> globalSchemas, V08TypeRegistry registry, final org.raml.v2.api.model.v08.resources.Resource resource) {

        GAbstractionFactory factory = new GAbstractionFactory();

        ResourceBuilder rg = new ResourceBuilder(build, factory.newResource(globalSchemas, registry, resource), resource.displayName(),
                resource.relativeUri().value());

        build.newResource(rg);
    }

}
