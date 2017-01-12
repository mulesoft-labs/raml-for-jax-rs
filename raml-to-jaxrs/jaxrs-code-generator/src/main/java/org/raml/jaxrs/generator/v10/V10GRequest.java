package org.raml.jaxrs.generator.v10;

import org.raml.jaxrs.generator.ramltypes.GRequest;
import org.raml.jaxrs.generator.ramltypes.GType;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

/**
 * Created by Jean-Philippe Belanger on 12/10/16.
 * Just potential zeroes and ones
 */
class V10GRequest implements GRequest {

    private final TypeDeclaration input;
    private final V10GType type;

    public V10GRequest(TypeDeclaration input, V10GType type) {

        this.input = input;
        this.type = type;
    }

    @Override
    public TypeDeclaration implementation() {
        return input;
    }

    @Override
    public String mediaType() {

        return input.name();
    }

    @Override
    public GType type() {

        return type;
    }
}
