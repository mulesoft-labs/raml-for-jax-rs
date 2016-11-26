package org.raml.jaxrs.generator.builders.types;

/**
 * Created by Jean-Philippe Belanger on 11/13/16.
 * Just potential zeroes and ones
 */
class PropertyInfo {

    private final String name;
    private final String type;
    private final boolean internalType;

    public PropertyInfo(String type, String name, boolean internalType) {
        this.name = name;
        this.type = type;
        this.internalType = internalType;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public boolean isInternalType() {
        return internalType;
    }
}
