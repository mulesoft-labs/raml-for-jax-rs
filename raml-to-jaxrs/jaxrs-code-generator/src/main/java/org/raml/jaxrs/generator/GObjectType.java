package org.raml.jaxrs.generator;

import org.raml.jaxrs.generator.GeneratorObjectType;

/**
 * Created by Jean-Philippe Belanger on 12/7/16.
 * Just potential zeroes and ones
 */
public enum GObjectType implements GeneratorObjectType {

    PLAIN_OBJECT_TYPE, XML_OBJECT_TYPE, JSON_OBJECT_TYPE, SCALAR;
}
