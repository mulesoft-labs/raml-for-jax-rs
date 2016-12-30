package org.raml.jaxrs.generator;

import org.raml.jaxrs.generator.v10.V10GType;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

/**
 * Created by Jean-Philippe Belanger on 12/30/16.
 * Just potential zeroes and ones
 */
public interface TypeRegistry {

    GType fetchType(String name);
/*
    V10GType fetchType(Resource resource, Method method, TypeDeclaration typeDeclaration);
    V10GType fetchType(Resource resource, Method method, Response response,
            TypeDeclaration typeDeclaration);
    V10GType fetchType(String name, TypeDeclaration typeDeclaration);
    V10GType fetchType(TypeDeclaration typeDeclaration);
*/
}
