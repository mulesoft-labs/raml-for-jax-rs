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

import org.raml.api.RamlParameter;
import org.raml.api.RamlType;
import org.raml.api.RamlTypes;
import org.raml.api.ScalarType;
import org.raml.utilities.IndentedAppendable;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.IOException;

/**
 * Created by barnabef on 4/18/17.
 */
public class ParameterEmitter {

  private IndentedAppendable writer;

  public ParameterEmitter(IndentedAppendable writer) {
    this.writer = writer;
  }

  public void emit(RamlParameter parameter) throws IOException {
    writer.appendLine(String.format("%s:", parameter.getName()));
    writer.indent();
    RamlType ramlType = RamlTypes.fromType(parameter.getType());
    writer.appendLine("type", ramlType.getRamlSyntax());

    if (parameter.getDefaultValue().isPresent()) {
      writer.appendEscapedLine("default", parameter.getDefaultValue().get());
      writer.appendLine("required", "false");
    } else if (parameter.getAnnotation(NotNull.class).isPresent()) {
      writer.appendLine("required", "true");
    }

    if (ramlType == ScalarType.INTEGER || ramlType == ScalarType.NUMBER) {
      if (parameter.getAnnotation(Min.class).isPresent()) {
        writer.appendLine("minimum", String.valueOf(parameter.getAnnotation(Min.class).get().value()));
      }
      if (parameter.getAnnotation(Max.class).isPresent()) {
        writer.appendLine("maximum", String.valueOf(parameter.getAnnotation(Max.class).get().value()));
      }
    }
    if (parameter.getAnnotation(Size.class).isPresent()) {
      if (ramlType == ScalarType.STRING) {
        writer.appendLine("minLength", String.valueOf(parameter.getAnnotation(Size.class).get().min()));
        writer.appendLine("maxLength", String.valueOf(parameter.getAnnotation(Size.class).get().max()));
      }
    }



    writer.outdent();
  }

}
