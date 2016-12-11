package org.raml.jaxrs.generator.v10;

import org.raml.jaxrs.generator.GRequest;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

/**
 * Created by Jean-Philippe Belanger on 12/10/16.
 * Just potential zeroes and ones
 */
class V10GRequest implements GRequest {

    private final TypeDeclaration input;

    public V10GRequest(TypeDeclaration input) {

        this.input = input;
    }

    @Override
    public TypeDeclaration implementation() {
        return input;
    }

    @Override
    public String mediaType() {

        return input.name();
    }
}
