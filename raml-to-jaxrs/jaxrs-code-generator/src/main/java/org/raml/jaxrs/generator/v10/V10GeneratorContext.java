package org.raml.jaxrs.generator.v10;

import org.raml.jaxrs.generator.GeneratorContext;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

/**
 * Created by Jean-Philippe Belanger on 12/8/16.
 * Just potential zeroes and ones
 */
public class V10GeneratorContext implements GeneratorContext {
    private final TypeDeclaration typeDeclaration;
    private final Resource resource;
    private final Method method;
    private final Response response;

    public V10GeneratorContext(TypeDeclaration typeDeclaration) {

        this(null, null, null, typeDeclaration);
    }

    public V10GeneratorContext(Resource resource, Method method, Response response, TypeDeclaration typeDeclaration) {

        this.resource = resource;
        this.method = method;
        this.response = response;
        this.typeDeclaration = typeDeclaration;
    }

    public V10GeneratorContext(Resource resource, Method method, TypeDeclaration typeDeclaration) {
        this(resource, method, null, typeDeclaration);
    }

    public TypeDeclaration getTypeDeclaration() {
        return typeDeclaration;
    }

    public Resource getResource() {
        return resource;
    }

    public Method getMethod() {
        return method;
    }

    public Response getResponse() {
        return response;
    }
}
