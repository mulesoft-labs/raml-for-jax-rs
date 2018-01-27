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
package org.raml.jaxrs.emitters;

import com.google.common.base.Optional;
import org.raml.builder.ExamplesBuilder;
import org.raml.builder.PropertyValueBuilder;
import org.raml.builder.SupportsProperties;
import org.raml.builder.TypeBuilder;
import org.raml.jaxrs.common.Example;
import org.raml.jaxrs.common.ExampleCases;
import org.raml.jaxrs.common.Examples;
import org.raml.jaxrs.types.RamlProperty;
import org.raml.jaxrs.types.RamlType;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

/**
 * Created by Jean-Philippe Belanger on 4/16/17. Just potential zeroes and ones
 */
public class ExampleModelEmitter implements LocalEmitter {


  private final TypeBuilder typeBuilder;
  private boolean headerDone = false;

  private String currentCaseName;
  private Set<String> seenTypeNames = new HashSet<>();

  private Stack<SupportsProperties> propertyValues = new Stack<>();

  public ExampleModelEmitter(TypeBuilder typeBuilder) {
    this.typeBuilder = typeBuilder;
  }

  @Override
  public void emit(RamlType ramlType) throws IOException {


    if (seenTypeNames.contains(ramlType.getTypeName())) {

      return;
    } else {

      seenTypeNames.add(ramlType.getTypeName());
    }

    if (!headerDone) {

      Optional<ExampleCases> examplesAnnotation = ramlType.getAnnotation(ExampleCases.class);
      if (examplesAnnotation.isPresent()) {

        String[] examples = examplesAnnotation.get().value();
        for (String caseName : examples) {
          if (caseName.isEmpty()) {
            throw new IOException("@ExampleCases case on type " + ramlType.getTypeName() + " is empty");
          }

          currentCaseName = caseName;

          ExamplesBuilder examplesBuilder = ExamplesBuilder.example(caseName).strict(false);
          propertyValues.push(examplesBuilder);
          if (hasAnExample(ramlType)) {

            emitOneExample(ramlType);
          } else {
            examplesBuilder.withNoProperties();
          }

          typeBuilder.withExamples(examplesBuilder);
        }
      } else {

        currentCaseName = "";

        if (!hasAnExample(ramlType)) {

          return;
        }


        ExamplesBuilder examplesBuilder = ExamplesBuilder.example("onlyCase").strict(false);
        propertyValues.push(examplesBuilder);
        typeBuilder.withExamples(examplesBuilder);

        headerDone = true;

        emitOneExample(ramlType);
      }
    } else {

      // This is never the first call.
      emitOneExample(ramlType);
    }
  }

  private void emitOneExample(RamlType ramlType) throws IOException {

    for (RamlProperty ramlProperty : ramlType.getProperties()) {
      ramlProperty.emit(this);
    }
  }

  @Override
  public void emit(RamlProperty ramlProperty) throws IOException {

    RamlType ramlType = ramlProperty.getRamlType();
    if (!ramlType.isRamlScalarType()) {

      try {
        PropertyValueBuilder property = PropertyValueBuilder.property(ramlProperty.getName());
        propertyValues.peek().withPropertyValue(property);
        propertyValues.push(property);
        ramlType.emit(this);
      } finally {

        propertyValues.pop();
      }
    } else {

      Optional<Example> e = ramlProperty.getAnnotation(Example.class);
      if (e.isPresent()) {

        if (e.get().useCase().equals(currentCaseName)) {
          propertyValues.peek().withPropertyValue(PropertyValueBuilder.property(ramlProperty.getName(), e.get().value()));
          return;
        }
      }

      Optional<Examples> examplesAnnotation = ramlProperty.getAnnotation(Examples.class);
      if (examplesAnnotation.isPresent()) {
        for (Example example : examplesAnnotation.get().value()) {
          if (example.useCase().equals(currentCaseName)) {

            propertyValues.peek().withPropertyValue(PropertyValueBuilder.property(ramlProperty.getName(), example.value()));
            return;
          }
        }
      }
    }
  }

  private boolean hasAnExample(RamlType type) {

    for (RamlProperty ramlProperty : type.getProperties()) {
      Optional<Example> annotation = ramlProperty.getAnnotation(Example.class);
      if (annotation.isPresent()) {
        return annotation.get().useCase().equals(currentCaseName);
      }

      Optional<Examples> examplesAnnotation = ramlProperty.getAnnotation(Examples.class);
      if (examplesAnnotation.isPresent()) {
        for (Example example : examplesAnnotation.get().value()) {
          if (example.useCase().equals(currentCaseName)) {
            return true;
          }
        }
      }
    }

    return false;
  }

}
