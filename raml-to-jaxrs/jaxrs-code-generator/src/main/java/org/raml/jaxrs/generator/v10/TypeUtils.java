package org.raml.jaxrs.generator.v10;

import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.ScalarTypes;
import org.raml.jaxrs.generator.builders.JavaPoetTypeGenerator;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.datamodel.JSONTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.XMLTypeDeclaration;

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
    public static boolean shouldCreateNewClass(TypeDeclaration extending, TypeDeclaration extended, List<TypeDeclaration>... all) {

        if (ScalarTypes.extendsScalarRamlType(extending)) {

            return false;
        }

        if ( all.length > 0 && ModelFixer.parentTypes(all[0], extending).size() > 1 ) {

            return true;
        }

        if ( extending.type().equals("object")) {

            return true;
        }

        if (extended instanceof JSONTypeDeclaration && extending instanceof JSONTypeDeclaration) {

            return false;
        }

        if (extended instanceof XMLTypeDeclaration && extending instanceof XMLTypeDeclaration) {
            return true;
        }

        if (extending instanceof ObjectTypeDeclaration && extended instanceof ObjectTypeDeclaration) {

            ObjectTypeDeclaration extendingObject = (ObjectTypeDeclaration) extending;
            ObjectTypeDeclaration extendedObject = (ObjectTypeDeclaration) extended;
            return extendedObject.properties().size() < extendingObject.properties().size();
        }

        return false;
    }


    public static boolean isNewTypeDeclaration(Api api, TypeDeclaration typeDeclaration) {

        if (typeDeclaration instanceof JSONTypeDeclaration) {

            return true;
        }

        if (typeDeclaration instanceof XMLTypeDeclaration) {

            return true;
        }


        if (!(typeDeclaration instanceof ObjectTypeDeclaration)) {
            return false;
        }

        if (typeDeclaration.type().equals("null_AnonymousType")) {

            return true;
        }

        ObjectTypeDeclaration object = (ObjectTypeDeclaration) typeDeclaration;
        if (typeDeclaration.type().equals("object")) {
            return true;
        }
        List<TypeDeclaration> parents = ModelFixer.parentTypes(api.types(), typeDeclaration);
        if (parents.size() == 0) {
            return false;
        }
        if (parents.size() != 1) {
            return true;
        }


        return ((ObjectTypeDeclaration) parents.get(0)).properties().size() < object.properties().size();
    }

    public static boolean isInlineTypeDeclarationFromResource(Api api, CurrentBuild current,
            TypeDeclaration typeDeclaration) {


        if (isComposite(typeDeclaration) && current.getDeclaredType(typeDeclaration.type()) != null) {

            return false;
        }

        if (typeDeclaration.type().equals("null_AnonymousType")) {

            return true;
        }

        if (!isComposite(typeDeclaration)) {
            return false;
        }

        if (current.checkJavaType(typeDeclaration, new HashMap<String, JavaPoetTypeGenerator>()) == null) {
            return true;
        }


        ObjectTypeDeclaration object = (ObjectTypeDeclaration) typeDeclaration;
        if (typeDeclaration.type().equals("object")) {
            return true;
        }
        List<TypeDeclaration> parents = ModelFixer.parentTypes(api.types(), typeDeclaration);
        if (parents.size() == 0) {
            return false;
        }
        if (parents.size() != 1) {
            return true;
        }


        return ((ObjectTypeDeclaration) parents.get(0)).properties().size() < object.properties().size();
    }

    public static boolean isComposite(TypeDeclaration typeDeclaration) {

        return typeDeclaration instanceof ObjectTypeDeclaration || typeDeclaration instanceof XMLTypeDeclaration
                || typeDeclaration instanceof JSONTypeDeclaration;
    }
}
