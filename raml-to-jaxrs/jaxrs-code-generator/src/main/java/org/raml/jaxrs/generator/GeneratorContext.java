package org.raml.jaxrs.generator;

import org.raml.jaxrs.generator.v10.V10ObjectType;

/**
 * Created by Jean-Philippe Belanger on 12/8/16.
 * Just potential zeroes and ones
 */
public interface GeneratorContext {
    String ramlTypeName();
    String javaTypeName();
    V10ObjectType constructionType();
    String schemaContent();
}
