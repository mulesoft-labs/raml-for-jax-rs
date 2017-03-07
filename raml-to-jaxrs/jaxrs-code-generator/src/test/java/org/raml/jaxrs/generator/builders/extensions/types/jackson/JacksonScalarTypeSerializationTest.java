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
package org.raml.jaxrs.generator.builders.extensions.types.jackson;

import com.squareup.javapoet.TypeSpec;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.raml.jaxrs.generator.matchers.AnnotationSpecMatchers.hasMember;
import static org.raml.jaxrs.generator.matchers.AnnotationSpecMatchers.member;
import static org.raml.jaxrs.generator.matchers.CodeBlockMatchers.codeBlockContents;

/**
 * Created by Jean-Philippe Belanger on 3/2/17. Just potential zeroes and ones
 */
public class JacksonScalarTypeSerializationTest {

  @Mock
  private TypeDeclaration declaration;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void onFieldImplementation() throws Exception {

  }

  @Test
  public void onEnumConstant() throws Exception {

    TypeSpec.Builder builder = TypeSpec.classBuilder("Boo");
    JacksonScalarTypeSerialization ser = new JacksonScalarTypeSerialization();
    ser.onEnumConstant(null, builder, null, "foo");

    TypeSpec spec = builder.build();

    assertThat(spec.annotations.get(0), hasMember("value"));
    assertThat(spec.annotations.get(0), member("value", contains(codeBlockContents(equalTo("\"foo\"")))));
  }



}
