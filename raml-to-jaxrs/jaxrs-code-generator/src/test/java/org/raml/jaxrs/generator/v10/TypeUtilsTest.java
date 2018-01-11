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
package org.raml.jaxrs.generator.v10;

import org.junit.Test;
import org.raml.jaxrs.generator.GFinderListener;
import org.raml.jaxrs.generator.ramltypes.GType;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.common.ValidationResult;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Jean-Philippe Belanger on 12/6/16. Just potential zeroes and ones
 */
public class TypeUtilsTest {

  @Test
  public void shouldExtendingString() throws Exception {

    ObjectTypeDeclaration typeDeclaration =
        (ObjectTypeDeclaration) finder("extendString.raml").get("ObjectOne").implementation();
    TypeDeclaration property = findProperty(typeDeclaration, "name");
    assertFalse(TypeUtils.shouldCreateNewClass(property, null));
  }

  @Test
  public void shouldExtendingObject() throws Exception {

    ObjectTypeDeclaration typeDeclaration =
        (ObjectTypeDeclaration) finder("extendObject.raml").get("ObjectOne").implementation();
    TypeDeclaration property = findProperty(typeDeclaration, "name");
    assertTrue(TypeUtils.shouldCreateNewClass(property, null));
  }

  @Test
  public void shouldExtendingObjectWithProperties() throws Exception {

    ObjectTypeDeclaration typeDeclaration =
        (ObjectTypeDeclaration) finder("extendObjectWithProperties.raml").get("ObjectOne")
            .implementation();
    TypeDeclaration property = findProperty(typeDeclaration, "name");
    assertTrue(TypeUtils.shouldCreateNewClass(property, null));
  }

  @Test
  public void shouldExtendingAnother() throws Exception {

    Map<String, GType> finder = finder("extendingAnother.raml");
    ObjectTypeDeclaration object = (ObjectTypeDeclaration) finder.get("ObjectOne").implementation();
    TypeDeclaration extending = findProperty(object, "name");
    ObjectTypeDeclaration extended =
        (ObjectTypeDeclaration) finder.get(extending.type()).implementation();
    assertFalse(TypeUtils.shouldCreateNewClass(extending, extended));
  }

  @Test
  public void shouldExtendingAnotherWithProperties() throws Exception {

    Map<String, GType> finder = finder("extendingAnotherWithProperties.raml");
    ObjectTypeDeclaration object = (ObjectTypeDeclaration) finder.get("ObjectOne").implementation();
    TypeDeclaration extending = findProperty(object, "name");
    ObjectTypeDeclaration extended =
        (ObjectTypeDeclaration) finder.get(extending.type()).implementation();
    assertTrue(TypeUtils.shouldCreateNewClass(extending, extended));
  }

  @Test
  public void shouldExtendingAnotherMultipleInheritance() throws Exception {

    Map<String, GType> finder = finder("extendObjectMultipleIneritance.raml");
    ObjectTypeDeclaration object = (ObjectTypeDeclaration) finder.get("ObjectOne").implementation();
    TypeDeclaration extending = findProperty(object, "name");
    ObjectTypeDeclaration extended = (ObjectTypeDeclaration) finder.get(extending.type());
    assertTrue(TypeUtils.shouldCreateNewClass(extending, extended));
  }

  @Test
  public void bigRaml() throws Exception {

    Map<String, GType> finder = finder("big.raml");
    ObjectTypeDeclaration object =
        (ObjectTypeDeclaration) finder.get("RamlDataType").implementation();
    TypeDeclaration extending = findProperty(object, "NilValue");
    ObjectTypeDeclaration extended = (ObjectTypeDeclaration) finder.get(extending.type());
    assertTrue(TypeUtils.shouldCreateNewClass(extending, extended));
  }

  private Map<String, GType> finder(String raml) {
    RamlModelResult ramlModelResult =
        new RamlModelBuilder().buildApi(
                                        new InputStreamReader(this.getClass().getResourceAsStream(raml)), ".");
    if (ramlModelResult.hasErrors()) {
      for (ValidationResult validationResult : ramlModelResult.getValidationResults()) {
        System.out.println(validationResult.getMessage());
      }
      throw new AssertionError();
    } else {
      final Map<String, GType> decls = new HashMap<>();
      new V10Finder(null, ramlModelResult.getApiV10())
          .findTypes(new GFinderListener() {

            @Override
            public void newTypeDeclaration(GType type) {
              decls.put(type.name(), type);
            }
          });

      return decls;

    }
  }

  private TypeDeclaration findProperty(ObjectTypeDeclaration typeDeclaration, String stringProp) {

    for (TypeDeclaration declaration : typeDeclaration.properties()) {
      if (declaration.name().equals(stringProp)) {
        return declaration;
      }
    }

    throw new AssertionError("no such prop " + stringProp);
  }

}
