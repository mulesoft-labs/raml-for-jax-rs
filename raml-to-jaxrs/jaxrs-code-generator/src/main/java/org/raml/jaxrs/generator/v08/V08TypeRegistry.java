package org.raml.jaxrs.generator.v08;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jean-Philippe Belanger on 1/5/17.
 * Just potential zeroes and ones
 */
public class V08TypeRegistry {

    private final Map<String, V08GType> knownTypes = new HashMap<>();

    public V08GType fetchType(String name) {
        return knownTypes.get(name);
    }

    public void addType(V08GType type) {
        knownTypes.put(type.name(), type);
    }
}
