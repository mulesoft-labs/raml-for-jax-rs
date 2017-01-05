package org.raml.jaxrs.generator.v10;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.raml.jaxrs.generator.Names;
import org.raml.jaxrs.generator.ScalarTypes;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jean-Philippe Belanger on 12/30/16.
 * Just potential zeroes and ones
 * This one just make sure that the Type Classes are created only once...
 */

public class V10TypeRegistry {

    private Map<String, V10GType> types = new HashMap<>();
    private final Multimap<String, V10GType> childMap = ArrayListMultimap.create();

    public void addChildToParent(List<V10GType> parents, V10GType child) {

        for (V10GType parent : parents) {

            if ( parent.name().equals("object") ) {
                continue;
            }
            childMap.put(parent.name(), child);
            addChildToParent(parent.parentTypes(), child);
        }
    }

    public V10GType fetchType(Resource resource, Method method, TypeDeclaration typeDeclaration) {

        String key = Names.ramlTypeName(resource, method, typeDeclaration);
        if ( types.containsKey(key)) {

            return types.get(key);
        } else {

            V10GType type = V10GTypeFactory.createRequestBodyType(this, resource, method, typeDeclaration);
            types.put(type.name(), type);
            return type;
        }
    }

    public V10GType fetchType(Resource resource, Method method, Response response, TypeDeclaration typeDeclaration) {
        String key = Names.ramlTypeName(resource, method, response, typeDeclaration);
        if ( types.containsKey(key)) {

            return types.get(key);
        } else {

            V10GType type = V10GTypeFactory.createResponseBodyType(this, resource, method, response, typeDeclaration);
            types.put(type.name(), type);
            return type;
        }
    }

    public V10GType fetchType(String name, TypeDeclaration typeDeclaration) {

        Class<?> javaType = ScalarTypes.scalarToJavaType(typeDeclaration);
        if ( javaType != null ) {

            return V10GTypeFactory.createScalar(typeDeclaration);
        }

        if ( types.containsKey(name)) {

            return types.get(name);
        } else {

            V10GType type = V10GTypeFactory.createExplicitlyNamedType(this, name, typeDeclaration);
            types.put(type.name(), type);
            return type;
        }
    }

    public V10GType fetchType(TypeDeclaration typeDeclaration) {

        String name = typeDeclaration.name();
        return fetchType(name, typeDeclaration);
    }

    public V10GType createInlineType(String internalTypeName, String javaTypeName, TypeDeclaration implementation) {
        V10TypeRegistry registry = new V10TypeRegistry();
        registry.types = new HashMap<>();
        types.putAll(registry.types);

        return V10GTypeFactory.createExplicitlyNamedType(registry, internalTypeName, javaTypeName, implementation);
    }

    public Multimap<String, V10GType> getChildClasses() {
        return childMap;
    }
}
