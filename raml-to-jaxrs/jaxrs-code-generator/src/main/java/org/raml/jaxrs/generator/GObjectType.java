package org.raml.jaxrs.generator;

/**
 * Created by Jean-Philippe Belanger on 12/7/16.
 * Just potential zeroes and ones
 */
public enum GObjectType implements GeneratorObjectType {

    PLAIN_OBJECT_TYPE, XML_OBJECT_TYPE, JSON_OBJECT_TYPE, SCALAR, ENUMERATION_TYPE;
}
