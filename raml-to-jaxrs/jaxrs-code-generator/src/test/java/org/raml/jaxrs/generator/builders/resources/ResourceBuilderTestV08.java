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
package org.raml.jaxrs.generator.builders.resources;

import com.squareup.javapoet.*;
import org.junit.Test;
import org.raml.jaxrs.generator.builders.CodeContainer;
import org.raml.jaxrs.generator.utils.RamlV08;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Created by Jean-Philippe Belanger on 12/25/16. Just potential zeroes and ones More of a function test than a unit test.
 * Shortcut.
 */
public class ResourceBuilderTestV08 {

  @Test
  public void check_query_params() throws Exception {

    RamlV08.buildResourceV08(this, "v08_query_parameters.raml", new CodeContainer<TypeSpec>() {

      @Override
      public void into(TypeSpec g) throws IOException {

        assertEquals("Foo", g.name);
        assertEquals(1, g.methodSpecs.size());
        MethodSpec methodSpec = g.methodSpecs.get(0);
        assertEquals("putAnimalsByUserId", methodSpec.name);
        assertEquals(2, methodSpec.annotations.size());
        assertEquals(ClassName.get(PUT.class), methodSpec.annotations.get(0).type);
        AnnotationSpec mediaTypeSpec = methodSpec.annotations.get(1);
        assertEquals(ClassName.get(Consumes.class), mediaTypeSpec.type);
        assertEquals(1, mediaTypeSpec.members.get("value").size());
        assertEquals("\"application/xml\"", mediaTypeSpec.members.get("value").get(0).toString());
        assertEquals(3, methodSpec.parameters.size());
        assertEquals(TypeName.INT, methodSpec.parameters.get(0).type);
        assertEquals("java.lang.String", methodSpec.parameters.get(1).type.toString());

      }
    }, "foo", "/fun");
  }
}
