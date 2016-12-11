package org.raml.jaxrs.generator;

import org.raml.jaxrs.generator.v10.V10GResource;
import org.raml.jaxrs.generator.v10.V10GType;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

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

    public GType newType(String resource, String method, String resonseCode, String type,  TypeDeclaration typeDeclaration) {
        return new V10GType(resource, method, resonseCode, type, typeDeclaration);
    }

    public GType newType(String resource, String method, String type,  TypeDeclaration typeDeclaration) {
        return new V10GType(resource, method, type, typeDeclaration);
    }

    public GType newType(TypeDeclaration typeDeclaration) {
        return new V10GType(typeDeclaration);
    }

}
