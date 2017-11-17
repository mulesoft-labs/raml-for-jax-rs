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
package org.raml.jaxrs.generator.builders.resources;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import org.junit.Test;
import org.raml.jaxrs.generator.builders.CodeContainer;
import org.raml.jaxrs.generator.utils.RamlV10;

import javax.lang.model.element.Modifier;
import javax.ws.rs.*;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Jean-Philippe Belanger on 12/25/16. Just potential zeroes and ones More of a function test than a unit test.
 * Shortcut.
 */
public class ResourceBuilderTestV10 {

  @Test
  public void build_simple() throws Exception {

    RamlV10.buildResourceV10(this, "resource_entity_no_response.raml",
                             new CodeContainer<TypeSpec>() {

                               @Override
                               public void into(TypeSpec g) throws IOException {

                                 assertEquals("Foo", g.name);
                                 assertEquals(1, g.methodSpecs.size());
                                 MethodSpec methodSpec = g.methodSpecs.get(0);
                                 assertEquals("postSearch", methodSpec.name);
                                 assertEquals(2, methodSpec.annotations.size());
                                 assertEquals(ClassName.get(POST.class), methodSpec.annotations.get(0).type);
                                 AnnotationSpec mediaTypeSpec = methodSpec.annotations.get(1);
                                 assertEquals(ClassName.get(Consumes.class), mediaTypeSpec.type);
                                 assertEquals(1, mediaTypeSpec.members.get("value").size());
                                 assertEquals("\"application/json\"", mediaTypeSpec.members.get("value").get(0)
                                     .toString());
                                 assertEquals(1, methodSpec.parameters.size());
                                 assertEquals(ClassName.get(String.class), methodSpec.parameters.get(0).type);
                               }
                             }, "foo", "/fun");
  }

  @Test
  public void build_same_type_two_media() throws Exception {

    RamlV10.buildResourceV10(this, "resource_entity_same_type_two_media.raml",
                             new CodeContainer<TypeSpec>() {

                               @Override
                               public void into(TypeSpec g) throws IOException {

                                 assertEquals("Foo", g.name);
                                 assertEquals(1, g.methodSpecs.size());
                                 MethodSpec methodSpec = g.methodSpecs.get(0);
                                 assertEquals("postSearch", methodSpec.name);
                                 assertEquals(2, methodSpec.annotations.size());
                                 assertEquals(ClassName.get(POST.class), methodSpec.annotations.get(0).type);
                                 AnnotationSpec mediaTypeSpec = methodSpec.annotations.get(1);
                                 assertEquals(ClassName.get(Consumes.class), mediaTypeSpec.type);
                                 assertEquals(2, mediaTypeSpec.members.get("value").size());
                                 assertEquals("\"application/json\"", mediaTypeSpec.members.get("value").get(0)
                                     .toString());
                                 assertEquals("\"application/xml\"", mediaTypeSpec.members.get("value").get(1)
                                     .toString());
                                 assertEquals(1, methodSpec.parameters.size());
                                 assertEquals(ClassName.get(String.class), methodSpec.parameters.get(0).type);
                               }
                             }, "foo", "/fun");
  }

  @Test
  public void build_two_types_different_media() throws Exception {

    RamlV10.buildResourceV10(this, "resource_entity_two_types_different_media.raml",
                             new CodeContainer<TypeSpec>() {

                               @Override
                               public void into(TypeSpec g) throws IOException {

                                 assertEquals("Foo", g.name);
                                 assertEquals(2, g.methodSpecs.size());
                                 {
                                   MethodSpec methodSpec = g.methodSpecs.get(0);
                                   assertEquals("postSearch", methodSpec.name);
                                   assertEquals(2, methodSpec.annotations.size());
                                   assertEquals(ClassName.get(POST.class), methodSpec.annotations.get(0).type);
                                   AnnotationSpec mediaTypeSpec = methodSpec.annotations.get(1);
                                   assertEquals(ClassName.get(Consumes.class), mediaTypeSpec.type);
                                   assertEquals(1, mediaTypeSpec.members.get("value").size());
                                   assertEquals("\"application/json\"", mediaTypeSpec.members.get("value").get(0)
                                       .toString());
                                   assertEquals(1, methodSpec.parameters.size());
                                   assertEquals(ClassName.get(String.class), methodSpec.parameters.get(0).type);
                                 }
                                 {
                                   MethodSpec methodSpec = g.methodSpecs.get(1);
                                   assertEquals("postSearch", methodSpec.name);
                                   assertEquals(2, methodSpec.annotations.size());
                                   assertEquals(ClassName.get(POST.class), methodSpec.annotations.get(0).type);
                                   AnnotationSpec mediaTypeSpec = methodSpec.annotations.get(1);
                                   assertEquals(ClassName.get(Consumes.class), mediaTypeSpec.type);
                                   assertEquals(1, mediaTypeSpec.members.get("value").size());
                                   assertEquals("\"application/xml\"", mediaTypeSpec.members.get("value").get(0)
                                       .toString());
                                   assertEquals(1, methodSpec.parameters.size());
                                   assertEquals(ClassName.INT, methodSpec.parameters.get(0).type);
                                 }

                               }
                             }, "foo", "/fun");
  }

  @Test
  public void build_octet_stream_media() throws Exception {

    RamlV10.buildResourceV10(this, "resource_entity_octet_stream.raml",
                             new CodeContainer<TypeSpec>() {

                               @Override
                               public void into(TypeSpec g) throws IOException {

                                 assertEquals("Foo", g.name);
                                 assertEquals(1, g.methodSpecs.size());
                                 {
                                   MethodSpec methodSpec = g.methodSpecs.get(0);
                                   assertEquals("postSearch", methodSpec.name);
                                   assertEquals(2, methodSpec.annotations.size());
                                   assertEquals(ClassName.get(POST.class), methodSpec.annotations.get(0).type);
                                   AnnotationSpec mediaTypeSpec = methodSpec.annotations.get(1);
                                   assertEquals(ClassName.get(Consumes.class), mediaTypeSpec.type);
                                   assertEquals(1, mediaTypeSpec.members.get("value").size());
                                   assertEquals("\"application/octet-stream\"", mediaTypeSpec.members.get("value").get(0)
                                       .toString());
                                   assertEquals(1, methodSpec.parameters.size());
                                   assertEquals(ClassName.get(InputStream.class), methodSpec.parameters.get(0).type);
                                 }
                               }
                             }, "foo", "/fun");
  }

  @Test
  public void build_empty() throws Exception {

    RamlV10.buildResourceV10(this, "resource_no_entity_no_response.raml",
                             new CodeContainer<TypeSpec>() {

                               @Override
                               public void into(TypeSpec g) throws IOException {

                                 assertEquals("Foo", g.name);
                                 assertEquals(1, g.methodSpecs.size());
                                 MethodSpec methodSpec = g.methodSpecs.get(0);
                                 assertEquals("postSearch", methodSpec.name);
                                 assertEquals(ClassName.VOID, methodSpec.returnType);
                                 assertEquals(1, methodSpec.annotations.size());
                                 assertEquals(ClassName.get(POST.class), methodSpec.annotations.get(0).type);
                                 assertEquals(0, methodSpec.parameters.size());

                                 assertEquals(0, g.typeSpecs.size());
                               }
                             }, "foo", "/fun");
  }

  @Test
  public void build_with_path_param() throws Exception {

    RamlV10.buildResourceV10(this, "resource_no_entity_path_param.raml",
                             new CodeContainer<TypeSpec>() {

                               @Override
                               public void into(TypeSpec g) throws IOException {

                                 assertEquals("Foo", g.name);
                                 assertEquals(1, g.methodSpecs.size());
                                 MethodSpec methodSpec = g.methodSpecs.get(0);
                                 assertEquals("postSearchByIdAndTwo", methodSpec.name);
                                 assertEquals(1, methodSpec.annotations.size());
                                 assertEquals(ClassName.get(POST.class), methodSpec.annotations.get(0).type);
                                 assertEquals(2, methodSpec.parameters.size());

                                 ParameterSpec paramOneSpec = methodSpec.parameters.get(0);
                                 assertEquals("id", paramOneSpec.name);
                                 assertEquals(ClassName.get(String.class), paramOneSpec.type);
                                 assertEquals(1, paramOneSpec.annotations.size());
                                 assertEquals(ClassName.get(PathParam.class), paramOneSpec.annotations.get(0).type);
                                 assertEquals("\"id\"", paramOneSpec.annotations.get(0).members.get("value").get(0)
                                     .toString());

                                 ParameterSpec paramTwoSpec = methodSpec.parameters.get(1);
                                 assertEquals("two", paramTwoSpec.name);
                                 assertEquals(ClassName.INT, paramTwoSpec.type);
                                 assertEquals(1, paramTwoSpec.annotations.size());
                                 assertEquals(ClassName.get(PathParam.class), paramTwoSpec.annotations.get(0).type);
                                 assertEquals("\"two\"", paramTwoSpec.annotations.get(0).members.get("value").get(0)
                                     .toString());
                               }
                             }, "foo", "/fun");
  }

  @Test
  public void build_with_path_param_with_intermediate() throws Exception {

    // https://github.com/mulesoft-labs/raml-for-jax-rs/issues/252
    RamlV10.buildResourceV10(this, "resource_entity_no_response_intermediate.raml",
                             new CodeContainer<TypeSpec>() {

                               @Override
                               public void into(TypeSpec g) throws IOException {

                                 assertEquals("Foo", g.name);
                                 assertEquals(1, g.methodSpecs.size());
                                 MethodSpec methodSpec = g.methodSpecs.get(0);
                                 assertEquals("putViewingSessionAttachmentsByViewingSessionId", methodSpec.name);
                                 assertEquals(2, methodSpec.annotations.size());
                                 assertEquals(ClassName.get(PUT.class), methodSpec.annotations.get(0).type);
                                 assertEquals(ClassName.get(Path.class), methodSpec.annotations.get(1).type);
                                 assertEquals("\"/{viewingSessionId}/Attachments\"",
                                              methodSpec.annotations.get(1).members.get("value").get(0).toString());

                                 assertEquals(1, methodSpec.parameters.size());

                                 ParameterSpec paramOneSpec = methodSpec.parameters.get(0);
                                 assertEquals("viewingSessionId", paramOneSpec.name);
                                 assertEquals(ClassName.get(String.class), paramOneSpec.type);
                                 assertEquals(1, paramOneSpec.annotations.size());
                                 assertEquals(ClassName.get(PathParam.class), paramOneSpec.annotations.get(0).type);
                                 assertEquals("\"viewingSessionId\"", paramOneSpec.annotations.get(0).members.get("value").get(0)
                                     .toString());

                               }
                             }, "foo", "/fun");
  }

  @Test
  public void build_with_query_param_with_complex_types() throws Exception {

    // https://github.com/mulesoft-labs/raml-for-jax-rs/issues/252
    RamlV10.buildResourceV10(this, "resource_no_entity_complex_query_param.raml",
                             new CodeContainer<TypeSpec>() {

                               @Override
                               public void into(TypeSpec g) throws IOException {

                                 assertEquals("Foo", g.name);
                                 assertEquals(1, g.methodSpecs.size());
                                 MethodSpec methodSpec = g.methodSpecs.get(0);
                                 assertEquals("postSearch", methodSpec.name);
                                 assertEquals(1, methodSpec.annotations.size());
                                 assertEquals(ClassName.get(POST.class), methodSpec.annotations.get(0).type);
                                 assertEquals(1, methodSpec.parameters.size());

                                 ParameterSpec paramOneSpec = methodSpec.parameters.get(0);
                                 assertEquals("one", paramOneSpec.name);
                                 assertEquals(ClassName.bestGuess("model.Complex"), paramOneSpec.type);
                                 assertEquals(1, paramOneSpec.annotations.size());
                                 assertEquals(ClassName.get(QueryParam.class), paramOneSpec.annotations.get(0).type);
                                 assertEquals("\"one\"", paramOneSpec.annotations.get(0).members.get("value").get(0)
                                     .toString());

                               }
                             }, "foo", "/fun");
  }

  @Test
  public void build_with_path_param_with_complex_types() throws Exception {

    // https://github.com/mulesoft-labs/raml-for-jax-rs/issues/252
    RamlV10.buildResourceV10(this, "resource_no_entity_complex_path_param.raml",
                             new CodeContainer<TypeSpec>() {

                               @Override
                               public void into(TypeSpec g) throws IOException {

                                 assertEquals("Foo", g.name);
                                 assertEquals(1, g.methodSpecs.size());
                                 MethodSpec methodSpec = g.methodSpecs.get(0);
                                 assertEquals("postSearchById", methodSpec.name);
                                 assertEquals(2, methodSpec.annotations.size());
                                 assertEquals(ClassName.get(POST.class), methodSpec.annotations.get(0).type);
                                 assertEquals(ClassName.get(Path.class), methodSpec.annotations.get(1).type);
                                 assertEquals("\"/{id}\"",
                                              methodSpec.annotations.get(1).members.get("value").get(0).toString());

                                 assertEquals(1, methodSpec.parameters.size());

                                 ParameterSpec paramOneSpec = methodSpec.parameters.get(0);
                                 assertEquals("id", paramOneSpec.name);
                                 assertEquals(ClassName.bestGuess("model.Complex"), paramOneSpec.type);
                                 assertEquals(1, paramOneSpec.annotations.size());
                                 assertEquals(ClassName.get(PathParam.class), paramOneSpec.annotations.get(0).type);
                                 assertEquals("\"id\"", paramOneSpec.annotations.get(0).members.get("value").get(0)
                                     .toString());

                               }
                             }, "foo", "/fun");
  }

  @Test
  public void build_with_intermediate_path_params() throws Exception {

    // https://github.com/mulesoft-labs/raml-for-jax-rs/issues/252
    RamlV10.buildResourceV10(this, "resource_entity_intermediate_pathparam.raml",
                             new CodeContainer<TypeSpec>() {

                               @Override
                               public void into(TypeSpec g) throws IOException {

                                 assertEquals("Foo", g.name);
                                 assertEquals(1, g.methodSpecs.size());
                                 MethodSpec methodSpec = g.methodSpecs.get(0);
                                 assertEquals("putAttachmentsBySomethingAndViewingSessionId", methodSpec.name);
                                 assertEquals(2, methodSpec.annotations.size());
                                 assertEquals(ClassName.get(PUT.class), methodSpec.annotations.get(0).type);
                                 assertEquals(ClassName.get(Path.class), methodSpec.annotations.get(1).type);
                                 assertEquals("\"/{viewingSessionId}/Attachments\"",
                                              methodSpec.annotations.get(1).members.get("value").get(0).toString());

                                 assertEquals(2, methodSpec.parameters.size());

                                 ParameterSpec paramOneSpec = methodSpec.parameters.get(0);
                                 assertEquals("something", paramOneSpec.name);
                                 assertEquals(ClassName.get(String.class), paramOneSpec.type);
                                 assertEquals(1, paramOneSpec.annotations.size());
                                 assertEquals(ClassName.get(PathParam.class), paramOneSpec.annotations.get(0).type);
                                 assertEquals("\"something\"", paramOneSpec.annotations.get(0).members.get("value").get(0)
                                     .toString());

                                 ParameterSpec paramTwoSpec = methodSpec.parameters.get(1);
                                 assertEquals("viewingSessionId", paramTwoSpec.name);
                                 assertEquals(ClassName.get(String.class), paramTwoSpec.type);
                                 assertEquals(1, paramTwoSpec.annotations.size());
                                 assertEquals(ClassName.get(PathParam.class), paramTwoSpec.annotations.get(0).type);
                                 assertEquals("\"viewingSessionId\"", paramTwoSpec.annotations.get(0).members.get("value").get(0)
                                     .toString());

                               }
                             }, "foo", "/fun");
  }

  @Test
  public void build_with_query_param() throws Exception {

    RamlV10.buildResourceV10(this, "resource_no_entity_query_param.raml",
                             new CodeContainer<TypeSpec>() {

                               @Override
                               public void into(TypeSpec g) throws IOException {

                                 assertEquals("Foo", g.name);
                                 assertEquals(1, g.methodSpecs.size());
                                 MethodSpec methodSpec = g.methodSpecs.get(0);
                                 assertEquals("postSearch", methodSpec.name);
                                 assertEquals(1, methodSpec.annotations.size());
                                 assertEquals(ClassName.get(POST.class), methodSpec.annotations.get(0).type);
                                 assertEquals(2, methodSpec.parameters.size());

                                 ParameterSpec paramOneSpec = methodSpec.parameters.get(0);
                                 assertEquals("one", paramOneSpec.name);
                                 assertEquals(ClassName.get(String.class), paramOneSpec.type);
                                 assertEquals(1, paramOneSpec.annotations.size());
                                 assertEquals(ClassName.get(QueryParam.class), paramOneSpec.annotations.get(0).type);
                                 assertEquals("\"one\"", paramOneSpec.annotations.get(0).members.get("value").get(0)
                                     .toString());

                                 ParameterSpec paramTwoSpec = methodSpec.parameters.get(1);
                                 assertEquals("two", paramTwoSpec.name);
                                 assertEquals(ClassName.INT, paramTwoSpec.type);
                                 assertEquals(1, paramTwoSpec.annotations.size());
                                 assertEquals(ClassName.get(QueryParam.class), paramTwoSpec.annotations.get(0).type);
                                 assertEquals("\"two\"", paramTwoSpec.annotations.get(0).members.get("value").get(0)
                                     .toString());
                               }
                             }, "foo", "/fun");
  }

  @Test
  public void build_with_query_param_with_defaults() throws Exception {

    RamlV10.buildResourceV10(this, "resource_no_entity_query_param_with_defaults.raml",
                             new CodeContainer<TypeSpec>() {

                               @Override
                               public void into(TypeSpec g) throws IOException {

                                 assertEquals("Foo", g.name);
                                 assertEquals(1, g.methodSpecs.size());
                                 MethodSpec methodSpec = g.methodSpecs.get(0);
                                 assertEquals("postSearch", methodSpec.name);
                                 assertEquals(1, methodSpec.annotations.size());
                                 assertEquals(ClassName.get(POST.class), methodSpec.annotations.get(0).type);
                                 assertEquals(2, methodSpec.parameters.size());

                                 ParameterSpec paramOneSpec = methodSpec.parameters.get(0);
                                 assertEquals("one", paramOneSpec.name);
                                 assertEquals(ClassName.get(String.class), paramOneSpec.type);
                                 assertEquals(2, paramOneSpec.annotations.size());
                                 assertEquals(ClassName.get(QueryParam.class), paramOneSpec.annotations.get(0).type);
                                 assertEquals("\"one\"", paramOneSpec.annotations.get(0).members.get("value").get(0)
                                     .toString());
                                 assertEquals(ClassName.get(DefaultValue.class), paramOneSpec.annotations.get(1).type);
                                 assertEquals("\"all\"", paramOneSpec.annotations.get(1).members.get("value").get(0)
                                     .toString());

                                 ParameterSpec paramTwoSpec = methodSpec.parameters.get(1);
                                 assertEquals("two", paramTwoSpec.name);
                                 assertEquals(ClassName.INT, paramTwoSpec.type);
                                 assertEquals(2, paramTwoSpec.annotations.size());
                                 assertEquals(ClassName.get(QueryParam.class), paramTwoSpec.annotations.get(0).type);
                                 assertEquals("\"two\"", paramTwoSpec.annotations.get(0).members.get("value").get(0)
                                     .toString());
                                 assertEquals(ClassName.get(DefaultValue.class), paramTwoSpec.annotations.get(1).type);
                                 assertEquals("\"-1\"", paramTwoSpec.annotations.get(1).members.get("value").get(0)
                                     .toString());
                               }
                             }, "foo", "/fun");
  }

  @Test
  public void build_with_header_param() throws Exception {

    RamlV10.buildResourceV10(this, "resource_no_entity_headers.raml",
                             new CodeContainer<TypeSpec>() {

                               @Override
                               public void into(TypeSpec g) throws IOException {

                                 assertEquals("Foo", g.name);
                                 assertEquals(1, g.methodSpecs.size());
                                 MethodSpec methodSpec = g.methodSpecs.get(0);
                                 assertEquals("postSearch", methodSpec.name);
                                 assertEquals(1, methodSpec.annotations.size());
                                 assertEquals(ClassName.get(POST.class), methodSpec.annotations.get(0).type);
                                 assertEquals(2, methodSpec.parameters.size());

                                 ParameterSpec paramOneSpec = methodSpec.parameters.get(0);
                                 assertEquals("xFun", paramOneSpec.name);
                                 assertEquals(ClassName.get(String.class), paramOneSpec.type);
                                 assertEquals(1, paramOneSpec.annotations.size());
                                 assertEquals(ClassName.get(HeaderParam.class), paramOneSpec.annotations.get(0).type);
                                 assertEquals("\"X-Fun\"", paramOneSpec.annotations.get(0).members.get("value").get(0)
                                     .toString());

                                 ParameterSpec paramTwoSpec = methodSpec.parameters.get(1);
                                 assertEquals("xDigitalFun", paramTwoSpec.name);
                                 assertEquals(ClassName.INT, paramTwoSpec.type);
                                 assertEquals(1, paramTwoSpec.annotations.size());
                                 assertEquals(ClassName.get(HeaderParam.class), paramTwoSpec.annotations.get(0).type);
                                 assertEquals("\"X-DigitalFun\"", paramTwoSpec.annotations.get(0).members.get("value").get(0)
                                     .toString());
                               }
                             }, "foo", "/fun");
  }


  @Test
  public void build_simple_response() throws Exception {

    RamlV10.buildResourceV10(this, "resource_simple_response.raml", new CodeContainer<TypeSpec>() {

      @Override
      public void into(TypeSpec g) throws IOException {

        assertEquals("Foo", g.name);
        assertEquals(1, g.methodSpecs.size());
        MethodSpec methodSpec = g.methodSpecs.get(0);
        assertEquals("getSearch", methodSpec.name);
        assertEquals(ClassName.get("", "GetSearchResponse"), methodSpec.returnType);
        assertEquals(1, g.typeSpecs.size());
        AnnotationSpec mediaTypeSpec = methodSpec.annotations.get(1);
        assertEquals(ClassName.get(Produces.class), mediaTypeSpec.type);
        assertEquals(1, mediaTypeSpec.members.get("value").size());
        assertEquals("\"application/json\"", mediaTypeSpec.members.get("value").get(0).toString());

        TypeSpec response = g.typeSpecs.get(0);
        assertEquals("GetSearchResponse", response.name);
        assertEquals(3, response.methodSpecs.size());

        assertTrue(response.methodSpecs.get(0).isConstructor());
        assertTrue(response.methodSpecs.get(1).isConstructor());

        MethodSpec responseMethod = response.methodSpecs.get(2);

        assertEquals("respond200WithApplicationJson", responseMethod.name);
        assertEquals("int", responseMethod.parameters.get(0).type.toString());

        assertTrue(responseMethod.hasModifier(Modifier.PUBLIC));
        assertTrue(responseMethod.hasModifier(Modifier.STATIC));
        assertTrue(responseMethod.code.toString().contains(
                                                           ".header(\"Content-Type\", \"application/json\")"));

      }
    }, "foo", "/fun");
  }


  @Test
  public void build_object_response() throws Exception {

    RamlV10.buildResourceV10(this, "resource_object_response.raml", new CodeContainer<TypeSpec>() {

      @Override
      public void into(TypeSpec g) throws IOException {

        assertEquals("Foo", g.name);
        assertEquals(1, g.methodSpecs.size());
        MethodSpec methodSpec = g.methodSpecs.get(0);
        assertEquals("getSearch", methodSpec.name);
        assertEquals(ClassName.get("", "GetSearchResponse"), methodSpec.returnType);
        assertEquals(1, g.typeSpecs.size());
        AnnotationSpec mediaTypeSpec = methodSpec.annotations.get(1);
        assertEquals(ClassName.get(Produces.class), mediaTypeSpec.type);
        assertEquals(1, mediaTypeSpec.members.get("value").size());
        assertEquals("\"application/json\"", mediaTypeSpec.members.get("value").get(0).toString());

        TypeSpec response = g.typeSpecs.get(0);
        assertEquals("GetSearchResponse", response.name);
        assertEquals(3, response.methodSpecs.size());

        assertTrue(response.methodSpecs.get(0).isConstructor());
        assertTrue(response.methodSpecs.get(1).isConstructor());

        MethodSpec responseMethod = response.methodSpecs.get(2);
        assertEquals("respond200WithApplicationJson", responseMethod.name);
        assertEquals("model.TypeOne", responseMethod.parameters.get(0).type.toString());
        assertTrue(responseMethod.hasModifier(Modifier.PUBLIC));
        assertTrue(responseMethod.hasModifier(Modifier.STATIC));
        assertTrue(responseMethod.code.toString().contains(
                                                           ".header(\"Content-Type\", \"application/json\")"));

      }
    }, "foo", "/fun");
  }

  @Test
  public void build_stream_response() throws Exception {

    RamlV10.buildResourceV10(this, "resource_stream_response.raml", new CodeContainer<TypeSpec>() {

      @Override
      public void into(TypeSpec g) throws IOException {

        assertEquals("Foo", g.name);
        assertEquals(1, g.methodSpecs.size());
        MethodSpec methodSpec = g.methodSpecs.get(0);
        assertEquals("getSearch", methodSpec.name);
        assertEquals(ClassName.get("", "GetSearchResponse"), methodSpec.returnType);
        assertEquals(1, g.typeSpecs.size());
        AnnotationSpec mediaTypeSpec = methodSpec.annotations.get(1);
        assertEquals(ClassName.get(Produces.class), mediaTypeSpec.type);
        assertEquals(1, mediaTypeSpec.members.get("value").size());
        assertEquals("\"application/octet-stream\"", mediaTypeSpec.members.get("value").get(0).toString());

        TypeSpec response = g.typeSpecs.get(0);
        assertEquals("GetSearchResponse", response.name);
        assertEquals(3, response.methodSpecs.size());

        assertTrue(response.methodSpecs.get(0).isConstructor());
        assertTrue(response.methodSpecs.get(1).isConstructor());

        MethodSpec responseMethod = response.methodSpecs.get(2);
        assertEquals("respond200WithApplicationOctetStream", responseMethod.name);
        assertEquals("javax.ws.rs.core.StreamingOutput", responseMethod.parameters.get(0).type.toString());
        assertTrue(responseMethod.hasModifier(Modifier.PUBLIC));
        assertTrue(responseMethod.hasModifier(Modifier.STATIC));
        assertTrue(responseMethod.code.toString().contains(
                                                           ".header(\"Content-Type\", \"application/octet-stream\")"));

      }
    }, "foo", "/fun");
  }

  @Test
  public void build_two_responses() throws Exception {

    RamlV10.buildResourceV10(this, "resource_two_responses.raml", new CodeContainer<TypeSpec>() {

      @Override
      public void into(TypeSpec g) throws IOException {

        assertEquals("Foo", g.name);
        assertEquals(1, g.methodSpecs.size());
        MethodSpec methodSpec = g.methodSpecs.get(0);
        assertEquals("getSearch", methodSpec.name);
        assertEquals(ClassName.get("", "GetSearchResponse"), methodSpec.returnType);
        assertEquals(1, g.typeSpecs.size());
        AnnotationSpec mediaTypeSpec = methodSpec.annotations.get(1);
        assertEquals(ClassName.get(Produces.class), mediaTypeSpec.type);
        assertEquals(2, mediaTypeSpec.members.get("value").size());
        assertEquals("\"application/xml\"", mediaTypeSpec.members.get("value").get(0).toString());
        assertEquals("\"application/json\"", mediaTypeSpec.members.get("value").get(1).toString());

        TypeSpec response = g.typeSpecs.get(0);
        assertEquals("GetSearchResponse", response.name);
        assertEquals(4, response.methodSpecs.size());

        assertTrue(response.methodSpecs.get(0).isConstructor());
        assertTrue(response.methodSpecs.get(1).isConstructor());

        {
          MethodSpec responseMethod = response.methodSpecs.get(2);
          assertEquals("respond200WithApplicationJson", responseMethod.name);
          assertTrue(responseMethod.hasModifier(Modifier.PUBLIC));
          assertTrue(responseMethod.hasModifier(Modifier.STATIC));
          assertTrue(responseMethod.code.toString().contains(
                                                             ".header(\"Content-Type\", \"application/json\")"));
        }
        {
          MethodSpec responseMethod = response.methodSpecs.get(3);
          assertEquals("respond200WithApplicationXml", responseMethod.name);
          assertTrue(responseMethod.hasModifier(Modifier.PUBLIC));
          assertTrue(responseMethod.hasModifier(Modifier.STATIC));
          assertTrue(responseMethod.code.toString().contains(
                                                             ".header(\"Content-Type\", \"application/xml\")"));
        }

      }
    }, "foo", "/fun");
  }
}
