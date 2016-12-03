package org.raml.jaxrs.generator.v10;

import org.raml.jaxrs.generator.JsonSchemaTypeGenerator;
import org.raml.jaxrs.generator.XmlSchemaTypeGenerator;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.datamodel.JSONTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.XMLTypeDeclaration;

import java.util.List;

/**
 * Created by Jean-Philippe Belanger on 11/28/16.
 * Just potential zeroes and ones
 */
public class TypeUtils {

    public static boolean isNewTypeDeclaration(Api api, TypeDeclaration typeDeclaration) {

        if ( typeDeclaration instanceof JSONTypeDeclaration) {

            return true;
        }

        if ( typeDeclaration instanceof XMLTypeDeclaration) {

            return true;
        }


        if ( ! (typeDeclaration instanceof ObjectTypeDeclaration) ) {
            return false;
        }

        if ( typeDeclaration.type().equals("null_AnonymousType")) {

            return true;
        }

        ObjectTypeDeclaration object = (ObjectTypeDeclaration) typeDeclaration;
        if ( typeDeclaration.type().equals("object") ) {
            return true;
        }
        List<TypeDeclaration> parents = ModelFixer.parentTypes(api.types(), typeDeclaration);
        if ( parents.size() == 0 ) {
            return false;
        }
        if ( parents.size() != 1) {
            return true;
        }


        return ((ObjectTypeDeclaration) parents.get(0)).properties().size() < object.properties().size();
    }
}
