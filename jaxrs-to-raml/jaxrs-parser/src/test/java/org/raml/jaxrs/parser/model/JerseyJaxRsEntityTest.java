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
package org.raml.jaxrs.parser.model;

import com.google.common.base.Optional;
import org.junit.Ignore;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by jpbelang on 2017-05-29.
 */
public class JerseyJaxRsEntityTest {

  @Ignore
  public static class Foo<T> {

    public Foo<String> foo() {

      return null;
    }

    public T foot() {

      return null;
    }
  }


  public static class NoAnnotation {

  }


  @Test
  public void annotationOnSimpleType() throws Exception {

    JerseyJaxRsEntity entity = new JerseyJaxRsEntity(Foo.class, null);
    Optional<Ignore> annotations = entity.getAnnotation(Ignore.class);

    assertTrue(annotations.isPresent());
  }

  @Test
  public void annotationOnParametrizedType() throws Exception {

    Method m = Foo.class.getMethod("foo");
    JerseyJaxRsEntity entity = new JerseyJaxRsEntity(m.getGenericReturnType(), null);
    Optional<Ignore> annotations = entity.getAnnotation(Ignore.class);

    assertTrue(annotations.isPresent());
  }

  @Test(expected = IllegalArgumentException.class)
  public void annotationOnParametrizedVariableType() throws Exception {

    Method m = Foo.class.getMethod("foot");
    JerseyJaxRsEntity entity = new JerseyJaxRsEntity(m.getGenericReturnType(), null);
    Optional<Ignore> annotations = entity.getAnnotation(Ignore.class);

    assertTrue(annotations.isPresent());
  }

  @Test
  public void noAnnotation() throws Exception {

    JerseyJaxRsEntity entity = new JerseyJaxRsEntity(NoAnnotation.class, null);
    Optional<Ignore> annotations = entity.getAnnotation(Ignore.class);

    assertFalse(annotations.isPresent());
  }

}
