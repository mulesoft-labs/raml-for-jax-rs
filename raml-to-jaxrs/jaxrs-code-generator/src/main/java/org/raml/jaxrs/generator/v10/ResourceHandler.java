package org.raml.jaxrs.generator.v10;

import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.builders.ResourceImplementation;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.resources.Resource;

/**
 * Created by Jean-Philippe Belanger on 10/26/16.
 * These handlers take care of different model types (v08 vs v10).
 */
public class ResourceHandler {

    public void handle(CurrentBuild build, Api api, Resource resource) {

        ResourceImplementation creator = build
                .createResource(resource.displayName().value());
        if ( resource.description() != null ) {
                creator.withDocumentation(resource.description().value() + "\n");
        }
    }


}
