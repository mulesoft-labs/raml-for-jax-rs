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
package org.raml.utilities.types;

import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

/**
 * Created by jpbelang on 2017-06-01.
 */
public class CastTest {


  public static class Foo<T> {

    public Foo<String> foo() {

      return null;
    }

    public T foot() {

      return null;
    }
  }

  @Test
  public void fromClassToClass() throws Exception {

    Class c = Cast.toClass(CastTest.class);
    assertSame(CastTest.class, c);
  }

  @Test
  public void fromParamClassToClass() throws Exception {

    Method m = Foo.class.getMethod("foo");
    Class c = Cast.toClass(m.getReturnType());
    assertSame(Foo.class, c);
  }

  @Test(expected = IllegalArgumentException.class)
  public void fromParamVarToClass() throws Exception {

    Method m = Foo.class.getMethod("foot");
    Class c = Cast.toClass(m.getGenericReturnType());
    assertSame(Foo.class, c);
  }

}
