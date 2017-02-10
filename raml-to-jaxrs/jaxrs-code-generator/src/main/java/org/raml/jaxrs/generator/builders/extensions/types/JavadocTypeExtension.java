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
package org.raml.jaxrs.generator.builders.extensions.types;

import com.squareup.javapoet.TypeSpec;
import org.raml.jaxrs.generator.builders.BuildPhase;
import org.raml.jaxrs.generator.extension.types.TypeContext;
import org.raml.jaxrs.generator.extension.types.TypeExtension;
import org.raml.jaxrs.generator.v10.V10GType;
import org.raml.v2.api.model.v10.datamodel.ExampleSpec;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

/**
 * Created by Jean-Philippe Belanger on 1/29/17. Just potential zeroes and ones
 */
public class JavadocTypeExtension implements TypeExtension {

  private interface JavadocAdder {

    void addJavadoc(String format, Object... args);
  }

  public void javadocExamples(JavadocAdder adder, TypeDeclaration typeDeclaration) {
    ExampleSpec example = typeDeclaration.example();
    if (example != null) {

      javadoc(adder, example);
    }

    for (ExampleSpec exampleSpec : typeDeclaration.examples()) {
      javadoc(adder, exampleSpec);
    }
  }

  @Override
  public TypeSpec.Builder onType(TypeContext context, final TypeSpec.Builder typeSpec, V10GType type, BuildPhase btype) {


    if (type.implementation().description() != null) {
      typeSpec.addJavadoc("$L\n", type.implementation().description().value());
    }

    javadocExamples(new JavadocAdder() {

      @Override
      public void addJavadoc(String format, Object... args) {

        typeSpec.addJavadoc(format, args);
      }
    }, type.implementation());

    return null;
  }


  public void javadoc(JavadocAdder adder, ExampleSpec exampleSpec) {
    adder.addJavadoc("Example:\n");

    if (exampleSpec.name() != null) {
      adder.addJavadoc(" $L\n", exampleSpec.name());
    }

    adder.addJavadoc(" $L\n", "<pre>\n{@code\n" + exampleSpec.value() + "\n}</pre>");
  }
}
