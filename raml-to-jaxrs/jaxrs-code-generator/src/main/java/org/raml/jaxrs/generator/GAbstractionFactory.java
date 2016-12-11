package org.raml.jaxrs.generator;

import org.raml.jaxrs.generator.v08.V08GResource;
import org.raml.jaxrs.generator.v10.V10GResource;
import org.raml.jaxrs.generator.v10.V10GType;
import org.raml.v2.api.model.v08.api.Api;
import org.raml.v2.api.model.v08.resources.Resource;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import java.util.Set;

/**
 * Created by Jean-Philippe Belanger on 12/10/16.
 * Just potential zeroes and ones
 */
public class GAbstractionFactory {

    public  GResource newResource(final org.raml.v2.api.model.v10.resources.Resource resource) {

        return new V10GResource(this, resource);
    }

    public GResource newResource(final GResource parent, final org.raml.v2.api.model.v10.resources.Resource resource) {

        return new V10GResource(this, parent, resource);
    }

    public GType newType(String internalTypeName, String type, TypeDeclaration implementation) {

        return new V10GType(internalTypeName, implementation);
    }


    public GResource newResource(Set<String> globalSchemas, Resource resource) {
        return new V08GResource(this, resource, globalSchemas);
    }

    public GResource newResource(Set<String> globalSchemas, GResource parent, org.raml.v2.api.model.v08.resources.Resource resource) {
        return new V08GResource(this, parent, resource, globalSchemas);
    }

}
