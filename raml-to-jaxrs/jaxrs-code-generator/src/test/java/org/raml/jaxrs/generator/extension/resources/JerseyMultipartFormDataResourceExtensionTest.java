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
package org.raml.jaxrs.generator.extension.resources;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.raml.builder.*;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.extension.resources.api.ResourceContext;
import org.raml.jaxrs.generator.v10.types.V10RamlToPojoGType;
import org.raml.ramltopojo.RamlToPojo;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Created. There, you have it.
 */
@RunWith(MockitoJUnitRunner.class)
public class JerseyMultipartFormDataResourceExtensionTest {

  @Mock
  CurrentBuild build;

  @Mock
  ResourceContext context;

  @Mock
  RamlToPojo ramlToPojo;


  @Test
  public void simpleHappyTest() {

    Api api =
        RamlDocumentBuilder
            .document()
            .title("any")
            .withResources(
                           ResourceBuilder.resource("/foo")
                               .withMethods(
                                            MethodBuilder.method("post")
                                                .withBodies(
                                                            BodyBuilder.body("multipart/form-data")
                                                                .ofType(TypeBuilder.type("object")
                                                                    .withProperty(TypePropertyBuilder.property("one", "file"))))))
            .buildModel();


    setupFetchTypes(api);


    JerseyMultipartFormDataResourceExtension extension = new JerseyMultipartFormDataResourceExtension();
    V10GMethod method = setupResources(api);

    MethodSpec.Builder sp = extension.onMethod(null, method, method.body().get(0), MethodSpec.methodBuilder("foo"));
    MethodSpec m = sp.build();

    assertEquals(2, m.parameters.size());

    assertEquals("oneStream", m.parameters.get(0).name);
    assertEquals(ClassName.get(InputStream.class), m.parameters.get(0).type);
    assertEquals(ClassName.bestGuess("org.glassfish.jersey.media.multipart.FormDataParam"),
                 m.parameters.get(0).annotations.get(0).type);
    assertEquals("\"one\"", m.parameters.get(0).annotations.get(0).members.get("value").get(0).toString());

    assertEquals("oneDisposition", m.parameters.get(1).name);
    assertEquals(ClassName.bestGuess("org.glassfish.jersey.media.multipart.FormDataContentDisposition"), m.parameters.get(1).type);
    assertEquals(ClassName.bestGuess("org.glassfish.jersey.media.multipart.FormDataParam"),
                 m.parameters.get(1).annotations.get(0).type);
    assertEquals("\"one\"", m.parameters.get(1).annotations.get(0).members.get("value").get(0).toString());

  }

  @Test
  public void ramlType() {

    Api api =
        RamlDocumentBuilder
            .document()
            .title("any")
            .withTypes(
                       TypeDeclarationBuilder.typeDeclaration("User")
                           .ofType(TypeBuilder.type("object").withProperty(TypePropertyBuilder.property("name", "string"))
                           )
            )
            .withResources(
                           ResourceBuilder.resource("/foo")
                               .withMethods(
                                            MethodBuilder.method("post")
                                                .withBodies(
                                                            BodyBuilder.body("multipart/form-data")
                                                                .ofType(TypeBuilder.type("object")
                                                                    .withProperty(TypePropertyBuilder.property("one", "User"))))))
            .buildModel();


    setupFetchTypes(api);


    JerseyMultipartFormDataResourceExtension extension = new JerseyMultipartFormDataResourceExtension();
    V10GMethod method = setupResources(api);

    MethodSpec.Builder sp = extension.onMethod(context, method, method.body().get(0), MethodSpec.methodBuilder("foo"));
    MethodSpec m = sp.build();

    assertEquals(1, m.parameters.size());

    assertEquals("one", m.parameters.get(0).name);
    assertEquals(ClassName.bestGuess("foo.MyTypeName"), m.parameters.get(0).type);
    assertEquals(ClassName.bestGuess("org.glassfish.jersey.media.multipart.FormDataParam"),
                 m.parameters.get(0).annotations.get(0).type);
    assertEquals("\"one\"", m.parameters.get(0).annotations.get(0).members.get("value").get(0).toString());

  }

  @Test
  public void passThrough() {

    Api api =
        RamlDocumentBuilder
            .document()
            .title("any")
            .withTypes(
                       TypeDeclarationBuilder.typeDeclaration("User")
                           .ofType(TypeBuilder.type("object").withProperty(TypePropertyBuilder.property("name", "string"))
                           )
            )
            .withResources(
                           ResourceBuilder.resource("/foo")
                               .withMethods(
                                            MethodBuilder.method("post")
                                                .withBodies(
                                                            BodyBuilder.body("not-multipart/form-data")
                                                                .ofType(TypeBuilder.type("object")
                                                                    .withProperty(TypePropertyBuilder.property("one", "User"))))))
            .buildModel();


    setupFetchTypes(api);


    JerseyMultipartFormDataResourceExtension extension = new JerseyMultipartFormDataResourceExtension();
    V10GMethod method = setupResources(api);

    MethodSpec.Builder foo = MethodSpec.methodBuilder("foo");
    MethodSpec.Builder sp = extension.onMethod(context, method, method.body().get(0), foo);
    MethodSpec m = sp.build();

    assertSame(sp, foo);
  }

  private V10GMethod setupResources(Api api) {
    V10GResource v10GResource = new V10GResource(build, null, api.resources().get(0));
    return new V10GMethod(build, v10GResource, api.resources().get(0).methods().get(0));
  }

  private void setupFetchTypes(Api api) {
    when(build.fetchType(any(Resource.class), any(Method.class), any(TypeDeclaration.class)))
        .thenReturn(new V10RamlToPojoGType(api.resources().get(0).methods().get(0).body().get(0)));

    when(context.fetchRamlToPojoBuilder()).thenReturn(ramlToPojo);
    when(ramlToPojo.fetchType(any(String.class), any(TypeDeclaration.class))).thenReturn(ClassName.bestGuess("foo.MyTypeName"));
  }
}
