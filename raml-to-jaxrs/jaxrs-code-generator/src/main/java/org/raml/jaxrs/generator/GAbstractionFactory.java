package org.raml.jaxrs.generator;

import org.raml.jaxrs.generator.v08.V08GResource;
import org.raml.jaxrs.generator.v08.V08TypeRegistry;
import org.raml.jaxrs.generator.v10.V10GResource;
import org.raml.jaxrs.generator.v10.V10TypeRegistry;
import org.raml.v2.api.model.v08.resources.Resource;

import java.util.Set;

/**
 * Created by Jean-Philippe Belanger on 12/10/16.
 * Just potential zeroes and ones
 */
public class GAbstractionFactory {

    public  GResource newResource(V10TypeRegistry registry, final org.raml.v2.api.model.v10.resources.Resource resource) {

        return new V10GResource(registry, this, resource);
    }

    public GResource newResource(V10TypeRegistry registry, final GResource parent, final org.raml.v2.api.model.v10.resources.Resource resource) {

        return new V10GResource(registry, this, parent, resource);
    }



    public GResource newResource(Set<String> globalSchemas, V08TypeRegistry registry, Resource resource) {
        return new V08GResource(this, resource, globalSchemas, registry);
    }

    public GResource newResource(Set<String> globalSchemas, V08TypeRegistry registry, GResource parent, org.raml.v2.api.model.v08.resources.Resource resource) {
        return new V08GResource(this, parent, resource, globalSchemas, registry);
    }

}
