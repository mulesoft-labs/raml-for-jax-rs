/*
 * Copyright 2013-2017 (c) MuleSoft, Inc.
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
package org.raml.jaxrs.generator.v10.types;

import org.raml.jaxrs.generator.Names;
import org.raml.jaxrs.generator.v10.*;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.JSONTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.StringTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.UnionTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.XMLTypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

/**
 * Created by Jean-Philippe Belanger on 1/3/17. Just potential zeroes and ones
 */
public class V10GTypeFactory {


  public static V10GType createResponseBodyType(Resource resource,
                                                Method method, Response response, TypeDeclaration typeDeclaration) {

    return new V10RamlToPojoGType(Names.ramlTypeName(resource, method,
                                                     response, typeDeclaration), typeDeclaration);
  }

  public static V10GType createExplicitlyNamedType(String s,
                                                   TypeDeclaration typeDeclaration) {
    return new V10RamlToPojoGType(s, typeDeclaration);
  }

  public static V10GType createInlineType(String ramlName,
                                          TypeDeclaration typeDeclaration) {
    return new V10RamlToPojoGType(ramlName, typeDeclaration);
  }

  public static V10GType createScalar(String name, TypeDeclaration typeDeclaration) {

    return new V10RamlToPojoGType(name, typeDeclaration);
  }

  public static V10GType createArray(String name,
                                     ArrayTypeDeclaration typeDeclaration) {

    return new V10RamlToPojoGType(name, typeDeclaration);
  }

  public static V10GType createEnum(String name,
                                    StringTypeDeclaration typeDeclaration) {
    return new V10RamlToPojoGType(name, typeDeclaration);
  }

  public static V10GType createJson(JSONTypeDeclaration jsonTypeDeclaration, String ramlName, CreationModel model) {

    return new V10GTypeJson(jsonTypeDeclaration, ramlName, Annotations.CLASS_NAME.get(
                                                                                      Names.typeName(ramlName),
                                                                                      jsonTypeDeclaration), model);
  }

  public static V10GType createJson(JSONTypeDeclaration jsonTypeDeclaration, String ramlName,
                                    String javaTypeName, CreationModel model) {

    return new V10GTypeJson(jsonTypeDeclaration, ramlName, javaTypeName, model);
  }

  public static V10GType createXml(XMLTypeDeclaration typeDeclaration, String ramlName, CreationModel model) {
    return new V10GTypeXml(typeDeclaration, ramlName, Annotations.CLASS_NAME.get(
                                                                                 Names.typeName(ramlName), typeDeclaration),
                           model);
  }

  public static V10GType createXml(XMLTypeDeclaration typeDeclaration, String ramlName,
                                   String javaName, CreationModel model) {
    return new V10GTypeXml(typeDeclaration, ramlName, javaName, model);
  }

  public static V10GType createUnion(String ramlName, UnionTypeDeclaration typeDeclaration) {
    return new V10RamlToPojoGType(ramlName, typeDeclaration);
  }

}
