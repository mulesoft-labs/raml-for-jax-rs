package org.raml.jaxrs.generator;

import org.raml.jaxrs.generator.v10.V10GType;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jean-Philippe Belanger on 12/30/16.
 * Just potential zeroes and ones
 */
public class V10TypeRegistry implements TypeRegistry {

    private Map<String, V10GType> types = new HashMap<>();

    @Override
    public GType fetchType(String name) {
        return types.get(name);
    }

    public V10GType fetchType(Resource resource, Method method, TypeDeclaration typeDeclaration) {

        String key = Names.ramlTypeName(resource, method, typeDeclaration);
        if ( types.containsKey(key)) {

            return types.get(key);
        } else {

            V10GType type = V10GType.createRequestBodyType(this, resource, method, typeDeclaration);
            types.put(type.name(), type);
            return type;
        }
    }

    public V10GType fetchType(Resource resource, Method method, Response response, TypeDeclaration typeDeclaration) {
        String key = Names.ramlTypeName(resource, method, response, typeDeclaration);
        if ( types.containsKey(key)) {

            return types.get(key);
        } else {

            V10GType type = V10GType.createResponseBodyType(this, resource, method, response, typeDeclaration);
            types.put(type.name(), type);
            return type;
        }
    }

    public V10GType fetchType(String name, TypeDeclaration typeDeclaration) {
        if ( types.containsKey(name)) {

            return types.get(name);
        } else {

            V10GType type = V10GType.createExplicitlyNamedType(this, name, typeDeclaration);
            types.put(type.name(), type);
            return type;
        }
    }

    public V10GType fetchType(TypeDeclaration typeDeclaration) {

        String name = typeDeclaration.name();
        return fetchType(name, typeDeclaration);
    }

    public GType createInlineType(String internalTypeName, String javaTypeName, TypeDeclaration implementation) {
        V10TypeRegistry registry = new V10TypeRegistry();
        registry.types = new HashMap<>();
        types.putAll(registry.types);

        return V10GType.createExplicitlyNamedType(registry, internalTypeName, javaTypeName, implementation);
    }

}
