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

import com.squareup.javapoet.*;
import org.raml.ramltopojo.CreationResult;
import org.raml.ramltopojo.EventType;
import org.raml.ramltopojo.extensions.ObjectPluginContext;
import org.raml.ramltopojo.extensions.ObjectTypeHandlerPlugin;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import static org.raml.ramltopojo.Names.methodName;

/**
 * Created. There, you have it.
 */
public class ChainSetter extends ObjectTypeHandlerPlugin.Helper {


  @Override
  public MethodSpec.Builder setterBuilt(ObjectPluginContext objectPluginContext, TypeDeclaration property,
                                        MethodSpec.Builder methodSpec, EventType eventType) {

    MethodSpec seen = methodSpec.build();
    MethodSpec.Builder newBuilder = MethodSpec.methodBuilder(methodName("with", property.name()))
        .addModifiers(seen.modifiers)
        .returns(objectPluginContext.creationResult().getJavaName(EventType.INTERFACE));

    commonStuffToCopy(seen, newBuilder);

    if (eventType == EventType.IMPLEMENTATION) {
      newBuilder.addStatement("this.$L = $L", property.name(), property.name())
          .addStatement("return this");

      return newBuilder;
    } else {
      return newBuilder;
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
