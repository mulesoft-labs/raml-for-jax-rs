package org.raml.jaxrs.generator.builders.extensions.types;

/**
 * Created by Jean-Philippe Belanger on 1/1/17.
 * Just potential zeroes and ones
 */
public class JacksonExtensions extends TypeExtensionList {

    public JacksonExtensions() {

        addExtension(new JacksonBasicExtension());
        addExtension(new JacksonScalarTypeSerialization());
        addExtension(new JacksonDiscriminatorInheritanceTypeExtension());
        addExtension(new JacksonUnionExtension());
    }
}
