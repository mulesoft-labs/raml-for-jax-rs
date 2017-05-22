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
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.raml.api.RamlEntity;
import org.raml.api.RamlParameter;
import org.raml.jaxrs.plugins.TypeHandler;
import org.raml.jaxrs.types.TypeRegistry;
import org.raml.utilities.IndentedAppendable;

import java.lang.annotation.Annotation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Jean-Philippe Belanger on 5/22/17. Just potential zeroes and ones
 */

public class ParameterEmitterTest {

  @Mock
  private IndentedAppendable writer;

  @Mock
  private RamlParameter parameter;

  @Mock
  private TypeRegistry typeRegistry;

  @Mock
  private TypeHandler typeHandler;

  @Mock
  private RamlEntity ramlEntity;

  @Before
  public void mockito() {
    MockitoAnnotations.initMocks(this);
  }

  public enum Enumeration {
    FOO, GOO
  }

  @Test
  public void simple() throws Exception {

    when(parameter.getName()).thenReturn("qp");
    when(ramlEntity.getType()).thenReturn(int.class);
    when(parameter.getEntity()).thenReturn(ramlEntity);
    when(parameter.getDefaultValue()).thenReturn(Optional.<String>absent());
    when(parameter.getAnnotation(any(Class.class))).thenReturn(Optional.<Annotation>absent());

    ParameterEmitter pe = new ParameterEmitter(writer, typeRegistry, typeHandler);
    pe.emit(parameter);

    verify(writer).appendLine("qp:");
    verify(typeHandler).writeType(typeRegistry, writer, ramlEntity);
  }

  @Test
  public void enumeration() throws Exception {

    when(parameter.getName()).thenReturn("qp");
    when(parameter.getEntity()).thenReturn(ramlEntity);
    when(ramlEntity.getType()).thenReturn(Enumeration.class);
    when(parameter.getDefaultValue()).thenReturn(Optional.<String>absent());
    when(parameter.getAnnotation(any(Class.class))).thenReturn(Optional.<Annotation>absent());

    ParameterEmitter pe = new ParameterEmitter(writer, typeRegistry, typeHandler);
    pe.emit(parameter);

    verify(writer).appendLine("qp:");
    verify(typeHandler).writeType(typeRegistry, writer, ramlEntity);
  }

}
