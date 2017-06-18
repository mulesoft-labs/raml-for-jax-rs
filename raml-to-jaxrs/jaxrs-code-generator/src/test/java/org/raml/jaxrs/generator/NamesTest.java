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

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.raml.jaxrs.generator.ramltypes.GMethod;
import org.raml.jaxrs.generator.ramltypes.GParameter;
import org.raml.jaxrs.generator.ramltypes.GResource;
import org.raml.v2.api.model.v10.system.types.RelativeUriString;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Created by Jean-Philippe Belanger on 10/29/16. Just potential zeroes and ones
 */
public class NamesTest {

  @Mock
  GResource resource;

  @Mock
  GMethod method;

  @Mock
  RelativeUriString url;

  @Mock
  GParameter uriParameter;

  @Before
  public void mocks() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void buildTypeName() throws Exception {


    assertEquals("Fun", Names.typeName("/fun"));
    assertEquals("Fun", Names.typeName("/fun"));
    assertEquals("CodeBytes", Names.typeName("//code//bytes"));
    assertEquals("Root", Names.typeName(""));
    assertEquals("FunAllo", Names.typeName("fun_allo"));
    assertEquals("FunAllo", Names.typeName("fun allo"));
    assertEquals("FunAllo", Names.typeName("funAllo"));
    assertEquals("FunAllo", Names.typeName("FunAllo"));

    assertEquals("FunAllo", Names.typeName("/FunAllo"));

    assertEquals("FunAllo", Names.typeName("Fun", "allo"));
    assertEquals("FunAllo", Names.typeName("fun", "_allo"));
    assertEquals("FunAllo", Names.typeName("fun", "allo"));

  }


  @Test
  public void buildMethod() {

    assertEquals("getSomething", Names.methodName("get", "something"));
    assertEquals("getClazz", Names.methodName("get", "class"));
  }

  @Test
  public void buildVariableName() throws Exception {

    assertEquals("funAllo", Names.variableName("funAllo"));
    assertEquals("funAllo", Names.variableName("FunAllo"));
    assertEquals("funAllo", Names.variableName("Fun", "allo"));
    assertEquals("funAllo", Names.variableName("fun", "_allo"));
    assertEquals("funAllo", Names.variableName("fun", "allo"));
    assertEquals("root", Names.variableName(""));

    assertEquals("fun", Names.variableName("/fun"));
    assertEquals("fun", Names.variableName("/fun"));
    assertEquals("funAllo", Names.variableName("//fun//allo"));
    assertEquals("funAllo", Names.variableName("fun allo"));
    assertEquals("funAllo", Names.variableName("fun_allo"));

  }

  @Test
  public void buildVariableReservedWord() throws Exception {

    assertEquals("ifVariable", Names.variableName("if"));
    assertEquals("classVariable", Names.variableName("class"));
  }

  @Test
  public void buildVariableWithUnderscore() throws Exception {

    assertEquals("_funAllo", Names.variableName("_funAllo"));
    assertEquals("_funAllo", Names.variableName("_FunAllo"));
    assertEquals("_funAllo", Names.variableName("_Fun", "allo"));
    assertEquals("_funAllo", Names.variableName("_fun", "_allo"));
  }

  @Test
  public void buildResponseClassname() throws Exception {

    when(method.resource()).thenReturn(resource);
    when(resource.resourcePath()).thenReturn("/songs");
    when(resource.uriParameters()).thenReturn(new ArrayList<GParameter>());
    when(method.method()).thenReturn("get");

    assertEquals("GetSongsResponse", Names.responseClassName(resource, method));
  }

  @Test
  public void buildResponseClassnameWithURIParam() throws Exception {

    when(method.resource()).thenReturn(resource);
    when(resource.resourcePath()).thenReturn("/songs/{songId}");
    when(uriParameter.name()).thenReturn("songId");
    when(resource.uriParameters()).thenReturn(Arrays.asList(uriParameter));
    when(method.method()).thenReturn("get");

    assertEquals("GetSongsBySongIdResponse", Names.responseClassName(resource, method));
  }

  @Test
  public void buildResponseClassnameWithTwoURIParam() throws Exception {

    when(method.resource()).thenReturn(resource);
    when(resource.resourcePath()).thenReturn("/songs/{songId}/{songId}");
    when(uriParameter.name()).thenReturn("songId");
    when(resource.uriParameters()).thenReturn(Arrays.asList(uriParameter, uriParameter));
    when(method.method()).thenReturn("get");

    assertEquals("GetSongsBySongIdAndSongIdResponse", Names.responseClassName(resource, method));
  }

  @Test
  public void buildResourceMethodClassname() throws Exception {

    when(method.resource()).thenReturn(resource);
    when(resource.resourcePath()).thenReturn("/songs");
    when(resource.uriParameters()).thenReturn(new ArrayList<GParameter>());
    when(method.method()).thenReturn("get");

    assertEquals("getSongs", Names.resourceMethodName(resource, method));
  }

  @Test
  public void buildResourceMethodNameWithURIParam() throws Exception {

    when(method.resource()).thenReturn(resource);
    when(resource.resourcePath()).thenReturn("/songs/{songId}");
    when(uriParameter.name()).thenReturn("songId");
    when(resource.uriParameters()).thenReturn(Arrays.asList(uriParameter));
    when(method.method()).thenReturn("get");

    assertEquals("getSongsBySongId", Names.resourceMethodName(resource, method));
  }

  @Test
  public void buildResourceMethodNameWithCurlyBracesAndWithoutParameter() throws Exception {

    when(method.resource()).thenReturn(resource);
    when(resource.resourcePath()).thenReturn("/songs/foo/{songId}");
    when(uriParameter.name()).thenReturn("songId");
    when(method.method()).thenReturn("get");

    assertEquals("getSongsFoo", Names.resourceMethodName(resource, method));
  }

  @Test
  public void buildResourceMethodNameWithTwoURIParam() throws Exception {

    when(method.resource()).thenReturn(resource);
    when(resource.resourcePath()).thenReturn("/songs/{songId}/{songId}");
    when(uriParameter.name()).thenReturn("songId");
    when(resource.uriParameters()).thenReturn(Arrays.asList(uriParameter, uriParameter));
    when(method.method()).thenReturn("get");

    assertEquals("getSongsBySongIdAndSongId", Names.resourceMethodName(resource, method));
  }

}
