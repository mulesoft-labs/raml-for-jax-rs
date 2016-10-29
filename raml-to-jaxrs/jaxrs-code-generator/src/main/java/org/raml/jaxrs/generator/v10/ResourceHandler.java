package org.raml.jaxrs.generator.v10;

import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.ResourceCreator;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.resources.Resource;

/**
 * Created by Jean-Philippe Belanger on 10/26/16.
 * Just potential zeroes and ones
 */
public class ResourceHandler {

    public void handle(CurrentBuild build, Api api, Resource resource) {

        ResourceCreator creator = build
                .createResource(resource.displayName().value())
                .withDocumentation(resource.description().toString());

    }
}
