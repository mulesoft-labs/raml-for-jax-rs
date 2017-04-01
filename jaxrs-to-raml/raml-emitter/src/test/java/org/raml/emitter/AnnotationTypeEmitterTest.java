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
package org.raml.emitter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.raml.api.RamlSupportedAnnotation;
import org.raml.utilities.IndentedAppendable;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.util.Collections;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Jean-Philippe Belanger on 3/29/17. Just potential zeroes and ones
 */
public class AnnotationTypeEmitterTest {

  @Retention(RUNTIME)
  @interface Simple {

    String one();
  }

  @Retention(RUNTIME)
  @interface Classed {

    Class one();
  }

  @Retention(RUNTIME)
  @interface Listed {

    int[] one();

    Class[] two();

    String[] three();
  }

  @Mock
  private IndentedAppendable writer;
  @Mock
  private RamlSupportedAnnotation ramlAnnotation;


  @Before
  public void mockito() {

    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void simple() throws IOException {

    when(ramlAnnotation.getAnnotation()).thenAnswer(new Answer<Class<? extends Annotation>>() {

      @Override
      public Class<? extends Annotation> answer(InvocationOnMock invocation) throws Throwable {
        return Simple.class;
      }
    });

    AnnotationTypeEmitter am = new AnnotationTypeEmitter(writer);
    am.emitAnnotation(Collections.singletonList(ramlAnnotation));

    verify(writer).appendLine("annotationTypes:");
    verify(writer).appendLine("Simple:");
    verify(writer).appendLine("properties:");
    verify(writer).appendLine("one: string");
  }

  @Test
  public void classed() throws IOException {

    when(ramlAnnotation.getAnnotation()).thenAnswer(new Answer<Class<? extends Annotation>>() {

      @Override
      public Class<? extends Annotation> answer(InvocationOnMock invocation) throws Throwable {
        return Classed.class;
      }
    });

    AnnotationTypeEmitter am = new AnnotationTypeEmitter(writer);
    am.emitAnnotation(Collections.singletonList(ramlAnnotation));

    verify(writer).appendLine("annotationTypes:");
    verify(writer).appendLine("Classed:");
    verify(writer).appendLine("properties:");
    verify(writer).appendLine("one: string");
  }

  @Test
  public void noprops() throws IOException {

    when(ramlAnnotation.getAnnotation()).thenAnswer(new Answer<Class<? extends Annotation>>() {

      @Override
      public Class<? extends Annotation> answer(InvocationOnMock invocation) throws Throwable {
        return Deprecated.class;
      }
    });

    AnnotationTypeEmitter am = new AnnotationTypeEmitter(writer);
    am.emitAnnotation(Collections.singletonList(ramlAnnotation));

    verify(writer).appendLine("annotationTypes:");
    verify(writer).appendLine("Deprecated: nil");
  }

  @Test
  public void list() throws IOException {

    when(ramlAnnotation.getAnnotation()).thenAnswer(new Answer<Class<? extends Annotation>>() {

      @Override
      public Class<? extends Annotation> answer(InvocationOnMock invocation) throws Throwable {
        return Listed.class;
      }
    });

    AnnotationTypeEmitter am = new AnnotationTypeEmitter(writer);
    am.emitAnnotation(Collections.singletonList(ramlAnnotation));

    verify(writer).appendLine("annotationTypes:");
    verify(writer).appendLine("Listed:");
    verify(writer).appendLine("properties:");
    verify(writer).appendLine("one: integer[]");
    verify(writer).appendLine("two: string[]");
    verify(writer).appendLine("three: string[]");

  }

}
