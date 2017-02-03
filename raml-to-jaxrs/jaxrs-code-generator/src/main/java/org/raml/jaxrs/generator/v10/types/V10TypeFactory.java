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
package org.raml.jaxrs.generator.v10.types;

import com.squareup.javapoet.ClassName;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.GenerationException;
import org.raml.jaxrs.generator.GeneratorType;
import org.raml.jaxrs.generator.Names;
import org.raml.jaxrs.generator.SchemaTypeFactory;
import org.raml.jaxrs.generator.builders.JavaPoetTypeGenerator;
import org.raml.jaxrs.generator.builders.TypeGenerator;
import org.raml.jaxrs.generator.ramltypes.GProperty;
import org.raml.jaxrs.generator.ramltypes.GType;
import org.raml.jaxrs.generator.v10.Annotations;
import org.raml.jaxrs.generator.v10.CompositeRamlTypeGenerator;
import org.raml.jaxrs.generator.v10.EnumerationGenerator;
import org.raml.jaxrs.generator.v10.PropertyInfo;
import org.raml.jaxrs.generator.v10.RamlTypeGeneratorImplementation;
import org.raml.jaxrs.generator.v10.RamlTypeGeneratorInterface;
import org.raml.jaxrs.generator.v10.UnionTypeGenerator;
import org.raml.jaxrs.generator.v10.V10GType;
import org.raml.jaxrs.generator.v10.V10TypeRegistry;
import org.raml.v2.api.model.v10.common.Annotable;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jean-Philippe Belanger on 12/30/16. Just potential zeroes and ones
 */
class V10TypeFactory {

  static TypeGenerator createObjectType(V10TypeRegistry registry, CurrentBuild currentBuild,
                                        V10GType originalType, boolean publicType) {

    List<V10GType> parentTypes = originalType.parentTypes();
    Map<String, JavaPoetTypeGenerator> internalTypes = new HashMap<>();
    int internalTypeCounter = 0;

    // this should be in the generator;
    List<PropertyInfo> properties = new ArrayList<>();
    V10TypeRegistry localRegistry = registry.createRegistry();
    for (GProperty declaration : originalType.properties()) {

      if (declaration.isInline()) {
        String internalTypeName = Integer.toString(internalTypeCounter);

        V10GType type =
            localRegistry.createInlineType(internalTypeName,
                                           Annotations.CLASS_NAME.get(
                                                                      Names.typeName(declaration.name(), "Type"),
                                                                      (Annotable) declaration.implementation()),
                                           (TypeDeclaration) declaration
                                               .implementation());
        TypeGenerator internalGenerator =
            inlineTypeBuild(localRegistry, currentBuild, GeneratorType.generatorFrom(type));
        if (internalGenerator instanceof JavaPoetTypeGenerator) {
          internalTypes.put(internalTypeName, (JavaPoetTypeGenerator) internalGenerator);
          properties.add(new PropertyInfo(localRegistry, declaration.overrideType(type)));
          internalTypeCounter++;
        } else {
          throw new GenerationException("internal type bad");
        }
      } else {
        properties.add(new PropertyInfo(localRegistry, declaration));
      }

    }


    JavaPoetTypeGenerator gen;
    if (Annotations.ABSTRACT.get(originalType)) {

      ClassName interf =
          (ClassName) originalType.defaultJavaTypeName(currentBuild.getModelPackage());
      gen =
          new RamlTypeGeneratorInterface(currentBuild, interf, parentTypes, properties,
                                         internalTypes, originalType);
    } else {

      ClassName interf =
          (ClassName) originalType.defaultJavaTypeName(currentBuild.getModelPackage());
      ClassName impl = originalType.javaImplementationName(currentBuild.getModelPackage());

      RamlTypeGeneratorImplementation implg =
          new RamlTypeGeneratorImplementation(currentBuild, impl, interf, properties,
                                              internalTypes, originalType);
      RamlTypeGeneratorInterface intg =
          new RamlTypeGeneratorInterface(currentBuild, interf, parentTypes, properties,
                                         internalTypes, originalType);
      gen = new CompositeRamlTypeGenerator(intg, implg);
    }

    if (publicType) {
      currentBuild.newGenerator(originalType.name(), gen);
    }
    return gen;
  }

  static TypeGenerator createEnumerationType(CurrentBuild currentBuild, GType type) {
    JavaPoetTypeGenerator generator =
        new EnumerationGenerator(currentBuild, ((V10GType) type).implementation(),
                                 (ClassName) type.defaultJavaTypeName(currentBuild.getModelPackage()), type.enumValues());

    currentBuild.newGenerator(type.name(), generator);
    return generator;
  }

  private static TypeGenerator inlineTypeBuild(V10TypeRegistry registry, CurrentBuild currentBuild,
                                               GeneratorType type) {

    switch (type.getObjectType()) {

      case ENUMERATION_TYPE:
        return createEnumerationType(currentBuild, type.getDeclaredType());

      case PLAIN_OBJECT_TYPE:
        return createObjectType(registry, currentBuild, (V10GType) type.getDeclaredType(), false);

      case JSON_OBJECT_TYPE:
        return SchemaTypeFactory.createJsonType(currentBuild, type.getDeclaredType());

      case XML_OBJECT_TYPE:
        return SchemaTypeFactory.createXmlType(currentBuild, type.getDeclaredType());
    }

    throw new GenerationException("don't know what to do with type " + type.getDeclaredType());
  }

  public static void createUnion(CurrentBuild currentBuild, V10TypeRegistry v10TypeRegistry,
                                 V10GType v10GType) {

    ClassName unionJavaName =
        (ClassName) v10GType.defaultJavaTypeName(currentBuild.getModelPackage());
    currentBuild.newGenerator(v10GType.name(), new UnionTypeGenerator(v10TypeRegistry, v10GType,
                                                                      unionJavaName, currentBuild));
  }
}
