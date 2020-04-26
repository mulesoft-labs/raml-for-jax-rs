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
package org.raml.jaxrs.generator;

import amf.client.model.domain.EndPoint;
import amf.client.model.domain.Operation;
import amf.client.model.domain.Parameter;
import amf.client.model.domain.ScalarShape;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.raml.jaxrs.generator.ramltypes.GMethod;
import org.raml.jaxrs.generator.ramltypes.GParameter;
import org.raml.jaxrs.generator.ramltypes.GResource;
import webapi.WebApiParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Created by Jean-Philippe Belanger on 10/29/16. Just potential zeroes and ones
 */
public class NamesTest {

  EndPoint resource = new EndPoint();

  Operation method = new Operation();


  @Before
  public void mocks() throws ExecutionException, InterruptedException {
    WebApiParser.init().get();
  }

  @Test
  public void buildTypeName() throws Exception {

    assertEquals("_200", Names.typeName("_200"));
    assertEquals("Fun", Names.typeName("/fun"));
    assertEquals("Fun", Names.typeName("/fun"));
    assertEquals("CodeBytes", Names.typeName("//code//bytes"));
    assertEquals("Root", Names.typeName(""));
    assertEquals("FunAllo", Names.typeName("fun_allo"));
    assertEquals("FunAllo", Names.typeName("fun allo"));
    assertEquals("FunAllo", Names.typeName("funAllo"));
    assertEquals("FunAllo", Names.typeName("FunAllo"));
    assertEquals("Fun200Allo", Names.typeName("fun_200Allo"));

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

    assertEquals("GetSongsResponse", Names.responseClassName(resource.withPath("/songs"), method.withMethod("get")));
  }

  @Test
  public void buildResponseClassnameWithURIParam() throws Exception {

    assertEquals("GetSongsBySongIdResponse",
                 Names.responseClassName(
                                         resource
                                             .withPath("/songs/{songId}")
                                             .withParameters(Collections.singletonList(new Parameter().withName("songId"))),
                                         method.withMethod("get")));
  }


  @Test
  public void buildResponseClassnameWithTwoURIParam() throws Exception {

    assertEquals("GetSongsBySongIdAndSongIdResponse",
                 Names.responseClassName(
                                         resource
                                             .withPath("/songs/{songId}/{songId}")
                                             .withParameters(Arrays.asList(new Parameter().withName("songId"),
                                                                           new Parameter().withName("songId"))),
                                         method.withMethod("get")));
  }

  @Test
  public void buildResourceMethodClassname() throws Exception {

    assertEquals("getSongs", Names.resourceMethodName(
                                                      resource
                                                          .withPath("/songs"),
                                                      method.withMethod("get")));
  }

  @Test
  public void buildResourceMethodNameWithURIParam() throws Exception {

    assertEquals("getSongsBySongId",
                 Names.resourceMethodName(
                                          resource
                                              .withPath("/songs/{songId}")
                                              .withParameters(Arrays.asList(new Parameter().withName("songId"))),
                                          method.withMethod("get")));
  }

  @Test
  public void buildResourceMethodNameWithCurlyBracesAndWithoutParameter() throws Exception {

    assertEquals("getSongsFoo", Names.resourceMethodName(
                                                         resource
                                                             .withPath("/songs/foo/{songId}"),
                                                         method.withMethod("get")));
  }

  @Test
  public void buildResourceMethodNameWithTwoURIParam() throws Exception {

    // when(method.resource()).thenReturn(resource);
    // when(resource.resourcePath()).thenReturn("/songs/{songId}/{songId}");
    // when(uriParameter.name()).thenReturn("songId");
    // when(resource.uriParameters()).thenReturn(Arrays.asList(uriParameter, uriParameter));
    // when(resource.relativePath()).thenReturn("path");
    // when(method.method()).thenReturn("get");

    assertEquals("getSongsBySongIdAndSongId",
                 Names.resourceMethodName(
                                          resource
                                              .withPath("/songs/{songId}/{songId}")
                                              .withParameters(Arrays.asList(new Parameter().withName("songId"),
                                                                            new Parameter().withName("songId"))),
                                          method.withMethod("get")));
  }

}
