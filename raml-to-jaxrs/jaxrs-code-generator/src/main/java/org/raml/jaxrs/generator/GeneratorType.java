package org.raml.jaxrs.generator;

/**
 * Created by Jean-Philippe Belanger on 12/7/16.
 * Just potential zeroes and ones
 */
public class GeneratorType<T> {

    private final String ramlTypeName;
    private final String javaTypeName;
    private final T context;

    public GeneratorType(GeneratorObjectType type,  String ramlTypeName, String javaTypeName,
            T context) {
        this.ramlTypeName = ramlTypeName;
        this.javaTypeName = javaTypeName;
        this.context = context;
    }

    public String getRamlTypeName() {
        return ramlTypeName;
    }

    public String getJavaTypeName() {
        return javaTypeName;
    }

    public boolean declaresProperty(String name) {
        return false;
    }

    public T getContext() {
        return context;
    }
}
