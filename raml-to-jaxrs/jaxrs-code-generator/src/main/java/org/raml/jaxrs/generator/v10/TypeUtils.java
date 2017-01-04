package org.raml.jaxrs.generator.v10;

import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.GParameter;
import org.raml.jaxrs.generator.GProperty;
import org.raml.jaxrs.generator.GType;
import org.raml.jaxrs.generator.ScalarTypes;
import org.raml.jaxrs.generator.builders.JavaPoetTypeGenerator;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.datamodel.JSONTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.StringTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.XMLTypeDeclaration;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Jean-Philippe Belanger on 11/28/16.
 * Just potential zeroes and ones
 */
public class TypeUtils {


    /**
     * Called on type extension. If there are no properties ? We create an empty class.
     * if the class is of type object, we don't extend anything.
     */
    public static boolean shouldCreateNewClass(TypeDeclaration extending, TypeDeclaration... extended) {

        if (ScalarTypes.extendsScalarRamlType(extending)) {

            return false;
        }

/*
        if ( extending.type() == null ) {

            return false;
        }
*/

        if ( extending instanceof StringTypeDeclaration && extending.type() != null && extending.type().equals("string")
                && ((StringTypeDeclaration) extending).enumValues().size() > 0
                && extended.length == 1
                && extended[0] instanceof StringTypeDeclaration
                ) {

            return true;
        }

        if ( extending.parentTypes().size() > 1 ) {

            return true;
        }

/*
        if ( extending.name().equals("object") && extending.type() == null ) {

            return false;
        }
*/


        if ( extending.type() != null && extending.type().equals("object")) {

            return true;
        }

        if (extended != null && extended.length > 0 && extended[0] instanceof JSONTypeDeclaration && extending instanceof JSONTypeDeclaration) {

            return false;
        }

        if (extended != null && extended.length > 0 && extended[0] instanceof XMLTypeDeclaration && extending instanceof XMLTypeDeclaration) {
            return false;
        }

        if (extending instanceof ObjectTypeDeclaration && extended[0] instanceof ObjectTypeDeclaration) {

            ObjectTypeDeclaration extendingObject = (ObjectTypeDeclaration) extending;
            ObjectTypeDeclaration extendedObject = (ObjectTypeDeclaration) extended[0];
            return extendedObject.properties().size() < extendingObject.properties().size();
        }

        return false;
    }


    public static boolean isComposite(GParameter declaration) {

        return declaration.isComposite();
    }

}
