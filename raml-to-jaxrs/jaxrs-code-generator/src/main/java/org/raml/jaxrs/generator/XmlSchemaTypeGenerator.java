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
package org.raml.jaxrs.generator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import org.raml.jaxrs.generator.builders.AbstractTypeGenerator;
import org.raml.jaxrs.generator.builders.CodeContainer;
import org.raml.jaxrs.generator.builders.CodeModelTypeGenerator;
import org.raml.jaxrs.generator.builders.BuildPhase;
import org.raml.jaxrs.generator.ramltypes.GType;

import java.io.IOException;

/**
 * Created by Jean-Philippe Belanger on 11/20/16. Just potential zeroes and ones
 */
public class XmlSchemaTypeGenerator extends AbstractTypeGenerator<JCodeModel> implements CodeModelTypeGenerator {

  private final GType type;
  private final JCodeModel codeModel;
  private final String packageName;
  private final JClass jclass;

  public XmlSchemaTypeGenerator(GType type, JCodeModel codeModel, String packageName, JClass jclass) {
    this.type = type;
    this.codeModel = codeModel;
    this.packageName = packageName;
    this.jclass = jclass;
  }

  @Override
  public void output(CodeContainer<JCodeModel> container, BuildPhase buildPhase) throws IOException {

    container.into(codeModel);
  }

  @Override
  public TypeName getGeneratedJavaType() {

    return ClassName.get(packageName, jclass.name());
  }

  public GType getType() {
    return type;
  }
}
