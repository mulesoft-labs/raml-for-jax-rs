/*
 * Copyright ${licenseYear} (c) MuleSoft, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.raml.jaxrs.generator.v10;

import org.raml.jaxrs.generator.ScalarTypes;
import org.raml.jaxrs.generator.ramltypes.GParameter;
import org.raml.v2.api.model.v10.datamodel.JSONTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.StringTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.XMLTypeDeclaration;

/**
 * Created by Jean-Philippe Belanger on 11/28/16. Just potential zeroes and ones
 */
public class TypeUtils {


  /**
   * Called on type extension. If there are no properties ? We create an empty class. if the class is of type object, we don't
   * extend anything.
   */
  public static boolean shouldCreateNewClass(TypeDeclaration extending, TypeDeclaration... extended) {

    if (ScalarTypes.extendsScalarRamlType(extending)) {

      return false;
    }

    /*
     * if ( extending.type() == null ) {
     * 
     * return false; }
     */

    if (extending instanceof StringTypeDeclaration && extending.type() != null
        && extending.type().equals("string")
        && ((StringTypeDeclaration) extending).enumValues().size() > 0 && extended.length == 1
        && extended[0] instanceof StringTypeDeclaration) {

      return true;
    }

    if (extending.parentTypes().size() > 1) {

      return true;
    }

    /*
     * if ( extending.name().equals("object") && extending.type() == null ) {
     * 
     * return false; }
     */


    if (extending.type() != null && extending.type().equals("object")) {

      return true;
    }

    if (extended != null && extended.length > 0 && extended[0] instanceof JSONTypeDeclaration
        && extending instanceof JSONTypeDeclaration) {

      return false;
    }

    if (extended != null && extended.length > 0 && extended[0] instanceof XMLTypeDeclaration
        && extending instanceof XMLTypeDeclaration) {
      return false;
    }

    if (extending instanceof ObjectTypeDeclaration && extended.length > 0
        && extended[0] instanceof ObjectTypeDeclaration) {

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
