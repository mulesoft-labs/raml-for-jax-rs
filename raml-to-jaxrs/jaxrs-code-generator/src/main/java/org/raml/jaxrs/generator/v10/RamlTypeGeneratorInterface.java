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

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.Names;
import org.raml.jaxrs.generator.builders.AbstractTypeGenerator;
import org.raml.jaxrs.generator.builders.CodeContainer;
import org.raml.jaxrs.generator.builders.JavaPoetTypeGenerator;
import org.raml.jaxrs.generator.ramltypes.GType;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jean-Philippe Belanger on 11/13/16. Just potential zeroes and ones
 */
public class RamlTypeGeneratorInterface extends AbstractTypeGenerator<TypeSpec.Builder> implements
    JavaPoetTypeGenerator {

  private final CurrentBuild build;
  private final List<V10GType> parentTypes;
  private final ClassName interf;

  private Map<String, PropertyInfo> propertyInfos = new HashMap<>();
  private Map<String, JavaPoetTypeGenerator> internalTypes = new HashMap<>();
  private final V10GType typeDeclaration;


  public RamlTypeGeneratorInterface(CurrentBuild currentBuild, ClassName interf,
                                    List<V10GType> parentTypes, List<PropertyInfo> properties,
                                    Map<String, JavaPoetTypeGenerator> internalTypes, V10GType typeDeclaration) {

    this.build = currentBuild;
    this.interf = interf;
    this.parentTypes = parentTypes;
    this.internalTypes = internalTypes;
    this.typeDeclaration = typeDeclaration;
    for (PropertyInfo property : properties) {
      propertyInfos.put(property.getName(), property);
    }
  }

  @Override
  public void output(CodeContainer<TypeSpec.Builder> into, TYPE type) throws IOException {

    ObjectTypeDeclaration object = (ObjectTypeDeclaration) typeDeclaration.implementation();

    final TypeSpec.Builder typeSpec =
        TypeSpec.interfaceBuilder(interf).addModifiers(Modifier.PUBLIC);

    build.withTypeListeners().onTypeDeclaration(build, typeSpec, typeDeclaration);

    for (JavaPoetTypeGenerator internalType : internalTypes.values()) {

      internalType.output(new CodeContainer<TypeSpec.Builder>() {

        @Override
        public void into(TypeSpec.Builder g) throws IOException {

          g.addModifiers(Modifier.STATIC);
          typeSpec.addType(g.build());
        }
      }, type);
    }


    for (GType parentType : parentTypes) {

      if (parentType.name().equals("object")) {

        continue;
      }

      typeSpec.addSuperinterface(parentType.defaultJavaTypeName(build.getModelPackage()));
    }

    for (PropertyInfo propertyInfo : propertyInfos.values()) {

      final MethodSpec.Builder getSpec =
          MethodSpec.methodBuilder(Names.methodName("get", propertyInfo.getName())).addModifiers(
                                                                                                 Modifier.PUBLIC,
                                                                                                 Modifier.ABSTRACT);
      getSpec.returns(propertyInfo.resolve(build));
      build.withTypeListeners().onGetterMethodDeclaration(build, getSpec,
                                                          (TypeDeclaration) propertyInfo.getType().implementation());
      typeSpec.addMethod(getSpec.build());

      if (!propertyInfo.getName().equals(object.discriminator())) {
        MethodSpec.Builder setSpec =
            MethodSpec.methodBuilder(Names.methodName("set", propertyInfo.getName())).addModifiers(
                                                                                                   Modifier.PUBLIC,
                                                                                                   Modifier.ABSTRACT);
        ParameterSpec.Builder parameterSpec =
            ParameterSpec.builder(propertyInfo.resolve(build),
                                  Names.variableName(propertyInfo.getName()));
        build.withTypeListeners().onSetterMethodDeclaration(build, setSpec, parameterSpec,
                                                            (TypeDeclaration) propertyInfo.getType().implementation());
        setSpec.addParameter(parameterSpec.build());
        typeSpec.addMethod(setSpec.build());
      }
    }

    into.into(typeSpec);
  }

  @Override
  public TypeName getGeneratedJavaType() {

    return interf;
  }
}
