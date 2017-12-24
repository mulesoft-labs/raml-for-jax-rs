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
package org.raml.jaxrs.generator;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.squareup.javapoet.ClassName;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import org.raml.jaxrs.generator.builders.JAXBHelper;
import org.raml.jaxrs.generator.builders.RamlToPojoTypeGenerator;
import org.raml.jaxrs.generator.builders.TypeGenerator;
import org.raml.jaxrs.generator.ramltypes.GType;
import org.raml.jaxrs.generator.v10.V10GType;
import org.raml.ramltopojo.*;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Map;

/**
 * Created by Jean-Philippe Belanger on 12/2/16. Just potential zeroes and ones
 */
public class SchemaTypeFactory {

  public static TypeGenerator createXmlType(CurrentBuild currentBuild, GType type) {
    File schemaFile = null;
    try {
      schemaFile = JAXBHelper.saveSchema(type.schema(), currentBuild.getSchemaRepository());
      final JCodeModel codeModel = new JCodeModel();

      Map<String, JClass> generated =
          JAXBHelper.generateClassesFromXmlSchemas(currentBuild.getModelPackage(), schemaFile,
                                                   codeModel);
      XmlSchemaTypeGenerator gen =
          new XmlSchemaTypeGenerator(codeModel, currentBuild.getModelPackage(), generated.values()
              .iterator().next());
      type.setJavaType(gen.getGeneratedJavaType());
      currentBuild.newGenerator(type.name(), gen);
      return gen;
    } catch (Exception e) {

      throw new GenerationException(e);
    }
  }

  public static TypeGenerator createJsonType(CurrentBuild currentBuild, GType type) {

    JsonSchemaTypeGenerator gen =
        new JsonSchemaTypeGenerator(currentBuild, currentBuild.getModelPackage(),
                                    (ClassName) type.defaultJavaTypeName(currentBuild.getModelPackage()), type.schema());
    type.setJavaType(gen.getGeneratedJavaType());
    currentBuild.newGenerator(type.name(), gen);
    return gen;
  }

  public static TypeGenerator createRamlToPojo(CurrentBuild currentBuild, final V10GType type) {

    ResultingPojos p = RamlToPojoBuilder.builder(currentBuild.getApi())
        .inPackage(currentBuild.getModelPackage())
        .fetchTypes(TypeFetchers.fromAnywhere())
        .findTypes(new TypeFinder() {

          @Override
          public Iterable<TypeDeclaration> findTypes(Api api) {

            return FluentIterable.from(TypeFinders.everyWhere().findTypes(api)).firstMatch(new Predicate<TypeDeclaration>() {

              @Override
              public boolean apply(@Nullable TypeDeclaration input) {
                return input.name().equals(type.name());
              }
            }).asSet();
          }
        }).build().buildPojos();
    return new RamlToPojoTypeGenerator(p);
  }


}
