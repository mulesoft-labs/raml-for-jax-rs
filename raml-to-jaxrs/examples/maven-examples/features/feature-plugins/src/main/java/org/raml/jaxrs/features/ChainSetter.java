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
package org.raml.jaxrs.features;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import org.raml.jaxrs.generator.builders.BuildPhase;
import org.raml.jaxrs.generator.extension.types.MethodExtension;
import org.raml.jaxrs.generator.extension.types.MethodType;
import org.raml.jaxrs.generator.extension.types.PredefinedMethodType;
import org.raml.jaxrs.generator.extension.types.TypeContext;
import org.raml.jaxrs.generator.v10.V10GProperty;
import org.raml.jaxrs.generator.v10.V10GType;

import java.util.List;

import static org.raml.jaxrs.generator.Names.methodName;
import static org.raml.jaxrs.generator.Names.typeName;

/**
 * Created by Jean-Philippe Belanger on 3/15/17. Just potential zeroes and ones
 */
public class ChainSetter implements MethodExtension {

  @Override
  public MethodSpec.Builder onMethod(TypeContext context, TypeSpec.Builder typeSpec, MethodSpec.Builder methodSpec,
                                     List<ParameterSpec.Builder> parameters, V10GType containingType, V10GProperty property,
                                     BuildPhase buildPhase,
                                     MethodType methodType) {

    if (methodType == PredefinedMethodType.SETTER) {

      MethodSpec seen = methodSpec.build();
      MethodSpec.Builder newBuilder = MethodSpec.methodBuilder(methodName("with", property.name()))
          .addModifiers(seen.modifiers)
          .returns(ClassName.get(context.getModelPackage(), typeName(containingType.name())));

      commonStuffToCopy(seen, newBuilder);

      if (buildPhase == BuildPhase.IMPLEMENTATION) {
        newBuilder.addStatement("this.$L = $L", property.name(), property.name())
            .addStatement("return this");

        return newBuilder;
      }

      if (buildPhase == BuildPhase.INTERFACE) {

        return newBuilder;
      }

      return methodSpec;
    } else {

      return methodSpec;
    }
  }

  private void commonStuffToCopy(MethodSpec seen, MethodSpec.Builder newBuilder) {
    for (ParameterSpec parameter : seen.parameters) {
      newBuilder.addParameter(parameter);
    }

    for (AnnotationSpec annotation : seen.annotations) {
      newBuilder.addAnnotation(annotation);
    }
  }
}
