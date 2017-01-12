package org.raml.jaxrs.generator.v10;

import org.raml.jaxrs.generator.ramltypes.GResponseType;
import org.raml.jaxrs.generator.ramltypes.GType;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

/**
 * Created by Jean-Philippe Belanger on 12/11/16.
 * Just potential zeroes and ones
 */
public class V10GResponseType implements GResponseType {

    private final TypeDeclaration input;
    private final V10GType v10GType;

    public V10GResponseType(TypeDeclaration input, V10GType v10GType) {

        this.input = input;
        this.v10GType = v10GType;
    }

    @Override
    public String mediaType() {
        return input.name();
    }

    @Override
    public GType type() {
        return v10GType;
    }
}
