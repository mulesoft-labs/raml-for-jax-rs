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
package org.raml.jaxrs.emitters;

import com.google.common.base.Optional;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.raml.api.Annotable;
import org.raml.api.RamlSupportedAnnotation;
import org.raml.jaxrs.types.RamlType;
import org.raml.utilities.IndentedAppendable;

import java.lang.annotation.Annotation;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Jean-Philippe Belanger on 4/1/17. Just potential zeroes and ones
 */
public class AnnotationInstanceEmitterTest {

  @Mock
  private IndentedAppendable writer;
  @Mock
  private RamlSupportedAnnotation ramlAnnotation;


  @Before
  public void mockito() {

    MockitoAnnotations.initMocks(this);
  }

  @Test
  @Simple(one = "hello")
  public void simple() throws Exception {

    final Annotation annotation = AnnotationInstanceEmitterTest.class.getDeclaredMethod("simple")
        .getAnnotation(Simple.class);
    ramlAnnotationOfType(Simple.class, annotation);

    AnnotationInstanceEmitter emitter = new AnnotationInstanceEmitter(writer, Collections.singletonList(ramlAnnotation));
    emitter.emit(wrap(annotation));

    verify(writer).appendLine("(Simple):");
    verify(writer).appendLine("one: hello");
  }

  @Test
  @Deprecated
  public void noargs() throws Exception {

    Annotation annotation = AnnotationInstanceEmitterTest.class.getDeclaredMethod("noargs")
        .getAnnotation(Deprecated.class);
    ramlAnnotationOfType(Deprecated.class, annotation);

    AnnotationInstanceEmitter emitter = new AnnotationInstanceEmitter(writer, Collections.singletonList(ramlAnnotation));
    emitter.emit(wrap(annotation));

    verify(writer).appendLine("(Deprecated):");
  }

  @Test
  @Classed(one = AnnotationInstanceEmitterTest.class)
  public void classed() throws Exception {

    Annotation annotation = AnnotationInstanceEmitterTest.class.getDeclaredMethod("classed")
        .getAnnotation(Classed.class);
    ramlAnnotationOfType(Classed.class, annotation);

    AnnotationInstanceEmitter emitter = new AnnotationInstanceEmitter(writer, Collections.singletonList(ramlAnnotation));
    emitter.emit(wrap(annotation));

    verify(writer).appendLine("(Classed):");
    verify(writer).appendLine("one: AnnotationInstanceEmitterTest");
  }

  @Test
  @Listed(one = {1, 2, 3}, two = {AnnotationInstanceEmitter.class, AnnotationInstanceEmitterTest.class}, three = {"a", "b",
      "c"})
  public void listed() throws Exception {

    Annotation annotation = AnnotationInstanceEmitterTest.class.getDeclaredMethod("listed").getAnnotation(Listed.class);
    ramlAnnotationOfType(Listed.class, annotation);

    AnnotationInstanceEmitter emitter = new AnnotationInstanceEmitter(writer, Collections.singletonList(ramlAnnotation));
    emitter.emit(wrap(annotation));

    verify(writer).appendLine("(Listed):");
    verify(writer).appendLine("one: [1, 2, 3]");
    verify(writer).appendLine("two: [AnnotationInstanceEmitter, AnnotationInstanceEmitterTest]");
    verify(writer).appendLine("three: [a, b, c]");
  }

  private void ramlAnnotationOfType(final Class<? extends Annotation> annotationClass,
                                    final Annotation annotation) {
    when(ramlAnnotation.getAnnotation()).thenAnswer(new Answer<Class<? extends Annotation>>() {

      @Override
      public Class<? extends Annotation> answer(InvocationOnMock invocation) throws Throwable {
        return annotationClass;
      }
    });

    when(ramlAnnotation.getAnnotationInstance(any(Annotable.class))).thenAnswer(new Answer<Optional<Annotation>>() {

      @Override
      public Optional<Annotation> answer(InvocationOnMock invocation) throws Throwable {

        return Optional.of(annotation);
      }
    });
  }

  public RamlType wrap(Annotation annotation) {

    return new RamlType(null);
  }

  private static class SimpleAnnotable implements Annotable {

    private final Annotation annotation;

    public SimpleAnnotable(Annotation annotation) {
      this.annotation = annotation;
    }

    @Override
    public <T extends Annotation> Optional<T> getAnnotation(Class<T> annotationType) {
      {
        return (Optional<T>) Optional.of(annotation);
      }
    }
  }
}
