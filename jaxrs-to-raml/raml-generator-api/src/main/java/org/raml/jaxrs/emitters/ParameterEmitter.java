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
import org.raml.api.RamlParameter;
import org.raml.api.ScalarType;
import org.raml.builder.FacetBuilder;
import org.raml.builder.ParameterBuilder;
import org.raml.jaxrs.plugins.TypeHandler;
import org.raml.jaxrs.types.TypeRegistry;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.IOException;

/**
 * Created by barnabef on 4/18/17.
 */
public class ParameterEmitter {

  private final TypeRegistry typeRegistry;
  private final TypeHandler typeHandler;

  public ParameterEmitter(TypeRegistry typeRegistry,
                          TypeHandler typeHandler) {
    this.typeRegistry = typeRegistry;
    this.typeHandler = typeHandler;
  }

  public ParameterBuilder emit(RamlParameter parameter) throws IOException {

    ParameterBuilder parameterBuilder =
        ParameterBuilder.parameter(parameter.getName()).ofType(typeHandler.writeType(typeRegistry, parameter.getEntity()));

    if (parameter.getDefaultValue().isPresent()) {
      parameterBuilder.withFacets(
                                  FacetBuilder.facet("default").value(parameter.getDefaultValue().get()),
                                  FacetBuilder.facet("required").value("false")
          );
    } else if (parameter.getAnnotation(NotNull.class).isPresent()) {
      parameterBuilder.withFacets(
          FacetBuilder.facet("required").value("true")
          );
    }

    Optional<ScalarType> ramlType = ScalarType.fromType(parameter.getEntity().getType());
    if (ramlType.isPresent()) {
      if (ramlType.get() == ScalarType.INTEGER || ramlType.get() == ScalarType.NUMBER) {
        if (parameter.getAnnotation(Min.class).isPresent()) {

          parameterBuilder.withFacets(
              FacetBuilder.facet("minimum").value(String.valueOf(parameter.getAnnotation(Min.class).get().value()))
              );
        }
        if (parameter.getAnnotation(Max.class).isPresent()) {
          parameterBuilder.withFacets(
              FacetBuilder.facet("maximum").value(String.valueOf(parameter.getAnnotation(Max.class).get().value()))
              );
        }
      }
      if (parameter.getAnnotation(Size.class).isPresent()) {
        if (ramlType.get() == ScalarType.STRING) {
          parameterBuilder.withFacets(
                                      FacetBuilder.facet("minLength").value(String.valueOf(parameter.getAnnotation(Size.class)
                                          .get().min())),
                                      FacetBuilder.facet("maxLength").value(String.valueOf(parameter.getAnnotation(Size.class)
                                          .get().max()))

              );
        }
      }
    }

    return parameterBuilder;
  }
}
