package org.raml.jaxrs.generator.v10;

import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import java.util.List;

/**
 * Created by Jean-Philippe Belanger on 11/28/16.
 * Just potential zeroes and ones
 */
public class TypeUtils {

    public static boolean isNewTypeDeclaration(Api api, TypeDeclaration declaration) {

        if ( ! (declaration instanceof ObjectTypeDeclaration) ) {
            return false;
        }

        List<TypeDeclaration> parents = ModelFixer.parentTypes(api.types(), declaration);
        if ( parents.size() != 1) {
            return true;
        }

        ObjectTypeDeclaration object = (ObjectTypeDeclaration) declaration;
        return ((ObjectTypeDeclaration) parents.get(0)).properties().size() < object.properties().size();
    }
}
