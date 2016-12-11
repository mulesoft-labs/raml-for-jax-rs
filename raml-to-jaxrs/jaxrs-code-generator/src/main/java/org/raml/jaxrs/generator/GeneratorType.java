package org.raml.jaxrs.generator;

import org.raml.jaxrs.generator.v10.TypeUtils;
import org.raml.jaxrs.generator.v10.V10ObjectType;
import org.raml.v2.api.model.v10.datamodel.JSONTypeDeclaration;

import static org.raml.jaxrs.generator.v10.V10ObjectType.JSON_OBJECT_TYPE;
import static org.raml.jaxrs.generator.v10.V10ObjectType.PLAIN_OBJECT_TYPE;
import static org.raml.jaxrs.generator.v10.V10ObjectType.SCALAR;
import static org.raml.jaxrs.generator.v10.V10ObjectType.XML_OBJECT_TYPE;

/**
 * Created by Jean-Philippe Belanger on 12/7/16.
 * Just potential zeroes and ones
 */
public class GeneratorType {

    private final V10ObjectType objectType;
    private final GType declaredType;

    public static GeneratorType generatorFrom(GType typeDeclaration) {

        // Just a plain type we found.
        if (typeDeclaration.isJson()) {
            return  new GeneratorType(JSON_OBJECT_TYPE,
                    typeDeclaration);
        }

        if (typeDeclaration.isXml()) {

            return new GeneratorType(XML_OBJECT_TYPE, typeDeclaration);
        }

        if (typeDeclaration.isObject()) {

            return new GeneratorType(PLAIN_OBJECT_TYPE, typeDeclaration);
        }

        return new GeneratorType(SCALAR, typeDeclaration);
    }

    public GeneratorType(V10ObjectType objectType,  GType declaredType) {

        this.objectType = objectType;
        this.declaredType = declaredType;
    }

    public GType getDeclaredType() {
        return declaredType;
    }

    public V10ObjectType getObjectType() {
        return objectType;
    }
}
