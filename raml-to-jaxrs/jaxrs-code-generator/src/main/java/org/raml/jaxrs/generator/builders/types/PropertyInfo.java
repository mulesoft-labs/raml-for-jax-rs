package org.raml.jaxrs.generator.builders.types;

/**
 * Created by Jean-Philippe Belanger on 11/13/16.
 * Just potential zeroes and ones
 */
class PropertyInfo {

    private final String name;
    private final String type;

    public PropertyInfo(String type, String name) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}
