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

import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.raml.jaxrs.generator.builders.AbstractTypeGenerator;
import org.raml.jaxrs.generator.builders.CodeContainer;
import org.raml.jaxrs.generator.builders.JavaPoetTypeGenerator;

import java.io.IOException;

/**
 * Created by Jean-Philippe Belanger on 11/13/16. Just potential zeroes and ones
 */
public class CompositeRamlTypeGenerator extends AbstractTypeGenerator<TypeSpec.Builder> implements
    JavaPoetTypeGenerator {


  private final RamlTypeGeneratorInterface intf;
  private final RamlTypeGeneratorImplementation impl;


  public CompositeRamlTypeGenerator(RamlTypeGeneratorInterface intf,
                                    RamlTypeGeneratorImplementation impl) {
    this.intf = intf;
    this.impl = impl;
  }

  @Override
  public void output(CodeContainer<TypeSpec.Builder> rootDirectory, TYPE type) throws IOException {
    if (type == TYPE.IMPLEMENTATION) {
      impl.output(rootDirectory);
    } else {
      intf.output(rootDirectory);
    }
  }

  @Override
  public void output(CodeContainer<TypeSpec.Builder> rootDirectory) throws IOException {

    intf.output(rootDirectory, TYPE.INTERFACE);
    impl.output(rootDirectory, TYPE.IMPLEMENTATION);
  }

  @Override
  public TypeName getGeneratedJavaType() {

    return intf.getGeneratedJavaType();
  }
}
