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

import com.squareup.javapoet.TypeName;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.raml.v2.api.model.v10.datamodel.*;
import org.raml.v2.api.model.v10.declarations.AnnotationRef;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Created by Jean-Philippe Belanger on 12/17/16. Just potential zeroes and ones
 */
public class ScalarTypesTest {

  @Test
  public void classToTypeNameTest() throws Exception {

    assertEquals(TypeName.INT, ScalarTypes.classToTypeName(int.class));
    assertEquals(TypeName.INT.box(), ScalarTypes.classToTypeName(Integer.class));
  }


}
