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
package org.raml.jaxrs.parser.source;

import org.junit.Test;

import java.lang.reflect.Type;
import java.nio.file.Path;

import static org.junit.Assert.*;

/**
 * Created. There, you have it.
 */
public class UtilitiesTest {

  @Test
  public void testSimpleClass() {

    Path p = Utilities.getSourceFileRelativePath(LocalClass.class);

    assertEquals(p.toString(), "org/raml/jaxrs/parser/source/LocalClass.java");
  }


  @Test
  public void testGenericType() throws NoSuchMethodException {

    Type type = LocalClass.class.getMethod("testType").getGenericReturnType();

    Path p = Utilities.getSourceFileRelativePath(type);

    assertEquals(p.toString(), "java/util/List.java");
  }

}
