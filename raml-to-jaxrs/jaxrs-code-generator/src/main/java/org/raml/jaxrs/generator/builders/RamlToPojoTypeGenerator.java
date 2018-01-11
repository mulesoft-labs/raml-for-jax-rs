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
package org.raml.jaxrs.generator.builders;

import com.squareup.javapoet.TypeName;
import org.raml.ramltopojo.CreationResult;
import org.raml.ramltopojo.EventType;
import org.raml.ramltopojo.RamlToPojo;
import org.raml.ramltopojo.ResultingPojos;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import java.io.IOException;

/**
 * Created. There, you have it.
 */
public class RamlToPojoTypeGenerator implements TypeGenerator<ResultingPojos> {

  private final RamlToPojo pojos;
  private final String name;
  private final TypeDeclaration typeDeclaration;
  private final TypeName generatedType;

  public RamlToPojoTypeGenerator(RamlToPojo p, String name, TypeDeclaration typeDeclaration, TypeName generatedType) {
    this.pojos = p;
    this.name = name;
    this.typeDeclaration = typeDeclaration;
    this.generatedType = generatedType;
  }

  @Override
  public void output(CodeContainer<ResultingPojos> rootDirectory, BuildPhase buildPhase) throws IOException {

    output(rootDirectory);
  }

  @Override
  public TypeName getGeneratedJavaType() {

    return generatedType;
  }

  @Override
  public void output(CodeContainer<ResultingPojos> rootDirectory) throws IOException {

    ResultingPojos p =
        pojos
            .buildPojo(name, typeDeclaration);

    rootDirectory.into(p);
  }
}
