/*
 * Copyright 2013-2018 (c) MuleSoft, Inc.
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

import amf.client.model.domain.AnyShape;
import com.squareup.javapoet.TypeName;
import org.raml.ramltopojo.RamlToPojo;
import org.raml.ramltopojo.ResultingPojos;

import java.io.IOException;

/**
 * Created. There, you have it.
 */
public class RamlToPojoTypeGenerator implements TypeGenerator<ResultingPojos> {

  private final RamlToPojo pojos;
  private final String name;
  private final AnyShape anyShape;
  private final TypeName generatedType;

  public RamlToPojoTypeGenerator(RamlToPojo p, String name, AnyShape anyShape, TypeName generatedType) {
    this.pojos = p;
    this.name = name;
    this.anyShape = anyShape;
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
            .buildPojo(name, anyShape);

    rootDirectory.into(p);
  }
}
