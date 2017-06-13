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

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.raml.jaxrs.generator.Names;
import org.raml.jaxrs.generator.v10.Annotations;
import org.raml.jaxrs.generator.v10.V10GProperty;
import org.raml.jaxrs.generator.v10.V10GType;
import org.raml.jaxrs.generator.v10.V10TypeRegistry;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.JSONTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.StringTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.UnionTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.XMLTypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * Created by Jean-Philippe Belanger on 1/3/17. Just potential zeroes and ones
 */
public class V10GTypeFactory {

  public static V10GType createRequestBodyType(V10TypeRegistry registry, Resource resource,
                                               Method method, TypeDeclaration typeDeclaration) {

    return new V10GTypeObject(registry, typeDeclaration, Names.ramlTypeName(resource, method,
                                                                            typeDeclaration),
                              Annotations.CLASS_NAME.get(
                                                         Names.javaTypeName(resource, method, typeDeclaration), typeDeclaration),
                              true,
                              getProperties(typeDeclaration, registry), getParents(typeDeclaration, registry), null);
  }

  public static V10GType createResponseBodyType(V10TypeRegistry registry, Resource resource,
                                                Method method, Response response, TypeDeclaration typeDeclaration) {
    return new V10GTypeObject(registry, typeDeclaration, Names.ramlTypeName(resource, method,
                                                                            response, typeDeclaration),
                              Annotations.CLASS_NAME.get(
                                                         Names.javaTypeName(resource, method, response, typeDeclaration),
                                                         typeDeclaration), true,
                              getProperties(typeDeclaration, registry), getParents(typeDeclaration, registry), null);
  }

  public static V10GType createExplicitlyNamedType(V10TypeRegistry registry, String s,
                                                   TypeDeclaration typeDeclaration) {
    return new V10GTypeObject(registry, typeDeclaration, s, Annotations.CLASS_NAME.get(
                                                                                       Names.typeName(typeDeclaration.name()),
                                                                                       typeDeclaration), false,
                              getProperties(
                                            typeDeclaration, registry), getParents(typeDeclaration, registry), null);
  }

  public static V10GType createInlineType(V10TypeRegistry registry, String ramlName,
                                          String javaClassName, TypeDeclaration typeDeclaration, V10GType containingType) {
    return new V10GTypeObject(registry, typeDeclaration, ramlName, Annotations.CLASS_NAME.get(
                                                                                              javaClassName, typeDeclaration),
                              true, getProperties(typeDeclaration, registry),
                              getParents(typeDeclaration, registry), containingType);
  }

  public static V10GType createScalar(String name, TypeDeclaration typeDeclaration) {

    return new V10GTypeScalar(name, typeDeclaration);
  }

  public static V10GType createArray(V10TypeRegistry registry, String name,
                                     ArrayTypeDeclaration typeDeclaration) {

    return new V10GTypeArray(registry, name, typeDeclaration);
  }

  public static V10GType createEnum(V10TypeRegistry v10TypeRegistry, String name,
                                    StringTypeDeclaration typeDeclaration) {
    return new V10GTypeEnum(v10TypeRegistry, name, Annotations.CLASS_NAME.get(
                                                                              Names.typeName(typeDeclaration.name()),
                                                                              typeDeclaration), typeDeclaration);
  }

  public static V10GType createEnum(V10TypeRegistry v10TypeRegistry, String name,
                                    String javaTypeName, StringTypeDeclaration typeDeclaration) {
    return new V10GTypeEnum(v10TypeRegistry, name, javaTypeName, typeDeclaration);
  }

  public static V10GType createJson(JSONTypeDeclaration jsonTypeDeclaration, String ramlName) {

    return new V10GTypeJson(jsonTypeDeclaration, ramlName, Annotations.CLASS_NAME.get(
                                                                                      Names.typeName(ramlName),
                                                                                      jsonTypeDeclaration));
  }

  public static V10GType createJson(JSONTypeDeclaration jsonTypeDeclaration, String ramlName,
                                    String javaTypeName) {

    return new V10GTypeJson(jsonTypeDeclaration, ramlName, javaTypeName);
  }

  public static V10GType createXml(XMLTypeDeclaration typeDeclaration, String ramlName) {
    return new V10GTypeXml(typeDeclaration, ramlName, Annotations.CLASS_NAME.get(
                                                                                 Names.typeName(ramlName), typeDeclaration));
  }

  public static V10GType createXml(XMLTypeDeclaration typeDeclaration, String ramlName,
                                   String javaName) {
    return new V10GTypeXml(typeDeclaration, ramlName, javaName);
  }

  public static V10GType createUnion(V10TypeRegistry registry,
                                     UnionTypeDeclaration typeDeclaration, String ramlName) {
    return new V10GTypeUnion(registry, typeDeclaration, ramlName, Annotations.CLASS_NAME.get(
                                                                                             Names.typeName(ramlName),
                                                                                             typeDeclaration));
  }

  public static V10GType createUnion(V10TypeRegistry registry,
                                     UnionTypeDeclaration typeDeclaration, String ramlName, String javaName) {
    return new V10GTypeUnion(registry, typeDeclaration, ramlName, javaName);
  }

  private static List<V10GType> getParents(TypeDeclaration typeDeclaration,
                                           final V10TypeRegistry registry) {
    return Lists.transform(typeDeclaration.parentTypes(),
                           new Function<TypeDeclaration, V10GType>() {

                             @Nullable
                             @Override
                             public V10GType apply(@Nullable TypeDeclaration input) {
                               return registry.fetchType(input);
                             }
                           });
  }

  private static List<V10GProperty> getProperties(final TypeDeclaration input,
                                                  final V10TypeRegistry registry) {

    if (input instanceof ObjectTypeDeclaration) {

      ObjectTypeDeclaration otd = (ObjectTypeDeclaration) input;
      return Lists.transform(otd.properties(), new Function<TypeDeclaration, V10GProperty>() {

        @Nullable
        @Override
        public V10GProperty apply(@Nullable TypeDeclaration declaration) {

          return new V10GProperty(declaration, registry.fetchType(declaration.type(), declaration));
        }
      });
    } else {

      return Collections.emptyList();
    }
  }

}
