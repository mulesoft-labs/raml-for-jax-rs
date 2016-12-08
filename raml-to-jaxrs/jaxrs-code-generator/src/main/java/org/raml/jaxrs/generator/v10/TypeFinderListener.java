package org.raml.jaxrs.generator.v10;

import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

/**
 * Created by Jean-Philippe Belanger on 12/7/16.
 * Just potential zeroes and ones
 */
public interface TypeFinderListener {

    void newType(TypeDeclaration typeDeclaration);
    void newType(Resource resource, Method method, Response response, TypeDeclaration typeDeclaration);
    void newType(Resource resource, Method method, TypeDeclaration typeDeclaration);
}
