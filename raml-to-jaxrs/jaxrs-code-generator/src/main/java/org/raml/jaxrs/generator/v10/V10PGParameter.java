package org.raml.jaxrs.generator.v10;

import org.raml.jaxrs.generator.GParameter;
import org.raml.v2.api.model.v10.datamodel.JSONTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.XMLTypeDeclaration;

/**
 * Created by Jean-Philippe Belanger on 12/10/16.
 * Just potential zeroes and ones
 */
class V10PGParameter implements GParameter {
    private final TypeDeclaration input;

    public V10PGParameter(TypeDeclaration input) {

        this.input = input;
    }

    @Override
    public String name() {
        return input.name();
    }

    @Override
    public boolean isComposite() {
        return input instanceof ObjectTypeDeclaration || input instanceof XMLTypeDeclaration
                || input instanceof JSONTypeDeclaration;
    }

    @Override
    public TypeDeclaration implementation() {

        return input;
    }
}
