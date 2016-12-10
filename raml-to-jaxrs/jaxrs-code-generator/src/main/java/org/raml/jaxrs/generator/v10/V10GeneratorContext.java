package org.raml.jaxrs.generator.v10;

import org.raml.jaxrs.generator.GenerationException;
import org.raml.jaxrs.generator.GeneratorContext;
import org.raml.jaxrs.generator.GeneratorType;
import org.raml.jaxrs.generator.Names;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.datamodel.JSONTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.XMLTypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

import static org.raml.jaxrs.generator.v10.V10ObjectType.JSON_OBJECT_TYPE;
import static org.raml.jaxrs.generator.v10.V10ObjectType.PLAIN_OBJECT_TYPE;
import static org.raml.jaxrs.generator.v10.V10ObjectType.XML_OBJECT_TYPE;

/**
 * Created by Jean-Philippe Belanger on 12/8/16.
 * Just potential zeroes and ones
 */
public class V10GeneratorContext implements GeneratorContext {
    private final TypeDeclaration typeDeclaration;
    private final Resource resource;
    private final Method method;
    private final Response response;
    private final Api api;

    public V10GeneratorContext(Api api, TypeDeclaration typeDeclaration) {

        this(api, null, null, null, typeDeclaration);
    }

    public V10GeneratorContext(Api api, Resource resource, Method method, Response response, TypeDeclaration typeDeclaration) {
        this.api = api;

        this.resource = resource;
        this.method = method;
        this.response = response;
        this.typeDeclaration = typeDeclaration;
    }

    public V10GeneratorContext(Api api, Resource resource, Method method, TypeDeclaration typeDeclaration) {
        this(api, resource, method, null, typeDeclaration);
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

    public Api getApi() {
        return api;
    }

    @Override
    public String ramlTypeName() {

        if ( response != null ) {
            return Names.ramlTypeName(resource, method, response, typeDeclaration);
        }

        if ( method != null ) {

            return Names.ramlTypeName(resource, method, typeDeclaration);
        }

        return typeDeclaration.name();
    }

    @Override
    public String javaTypeName() {

        if ( response != null ) {
            return Names.javaTypeName(resource, method, response, typeDeclaration);
        }

        if ( method != null ) {

            return Names.javaTypeName(resource, method, typeDeclaration);
        }

        return Names.typeName(typeDeclaration.name());
    }

    @Override
    public V10ObjectType constructionType() {
        if (typeDeclaration instanceof ObjectTypeDeclaration) {
            return PLAIN_OBJECT_TYPE;
        }

        if (typeDeclaration instanceof XMLTypeDeclaration) {

            return XML_OBJECT_TYPE;
        }

        if (typeDeclaration instanceof JSONTypeDeclaration) {

            return JSON_OBJECT_TYPE;
        }

        throw new GenerationException("unknown type " + typeDeclaration.name());
    }

    @Override
    public String schemaContent() {

        if (typeDeclaration instanceof XMLTypeDeclaration) {

            return ((XMLTypeDeclaration) typeDeclaration).schemaContent();
        }

        if (typeDeclaration instanceof JSONTypeDeclaration) {

            return ((JSONTypeDeclaration) typeDeclaration).schemaContent();
        }

        throw new GenerationException("not a schema type " + typeDeclaration.name());
    }
}
