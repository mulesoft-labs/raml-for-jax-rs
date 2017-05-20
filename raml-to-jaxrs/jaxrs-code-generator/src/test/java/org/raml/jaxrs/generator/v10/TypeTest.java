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
package org.raml.jaxrs.generator.v10;

import com.squareup.javapoet.TypeSpec;
import org.junit.Test;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.builders.BuildPhase;
import org.raml.jaxrs.generator.builders.CodeContainer;
import org.raml.jaxrs.generator.builders.JavaPoetTypeGenerator;
import org.raml.jaxrs.generator.utils.RamlV10;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Created by Jean-Philippe Belanger on 12/31/16. Just potential zeroes and ones
 */
public class TypeTest {


  @Test
  public void union() throws Exception {

    V10TypeRegistry registry = new V10TypeRegistry();
    CurrentBuild cb = RamlV10.buildType(this, "simpleUnion.raml", registry, "foo", ".");
    JavaPoetTypeGenerator gen = cb.getBuiltType("UnionType");
    gen.output(new CodeContainer<TypeSpec.Builder>() {

      @Override
      public void into(TypeSpec.Builder g) throws IOException {
        System.err.println(g.build().toString());

        TypeSpec spec = g.build();
        assertEquals(1, spec.fieldSpecs.size());
        assertEquals("java.lang.Object", spec.fieldSpecs.get(0).type.toString());
        assertEquals("anyType", spec.fieldSpecs.get(0).name);
      }
    });
  }

  @Test
  public void inlineObject() throws Exception {


    V10TypeRegistry registry = new V10TypeRegistry();
    CurrentBuild cb = RamlV10.buildType(this, "inlineObject.raml", registry, "foo", ".");
    JavaPoetTypeGenerator gen = cb.getBuiltType("TypeOne");

    gen.output(new CodeContainer<TypeSpec.Builder>() {

      @Override
      public void into(TypeSpec.Builder g) throws IOException {
        TypeSpec spec = g.build();
        System.err.println(spec);
        checkMethodsOfInterface(spec);
        // assertEquals("DayType", spec.typeSpecs.get(0).name);

      }
    }, BuildPhase.INTERFACE);


  }

  private void checkMethodsOfInterface(TypeSpec spec) {
    assertEquals(2, spec.methodSpecs.size());
    assertEquals("getDay", spec.methodSpecs.get(0).name);
    assertEquals("DayType", spec.methodSpecs.get(0).returnType.toString());

    assertEquals("setDay", spec.methodSpecs.get(1).name);
    assertEquals("DayType", spec.methodSpecs.get(1).parameters.get(0).type.toString());
    // assertEquals(1, spec.typeSpecs.size());
  }


  @Test
  public void scalarTypes() throws Exception {


    V10TypeRegistry registry = new V10TypeRegistry();
    CurrentBuild cb = RamlV10.buildType(this, "scalarTypes.raml", registry, "foo", ".");
    JavaPoetTypeGenerator gen = cb.getBuiltType("TypeOne");

    gen.output(new CodeContainer<TypeSpec.Builder>() {

      int count = 0;

      @Override
      public void into(TypeSpec.Builder g) throws IOException {
        TypeSpec spec = g.build();
        System.err.println(spec);
        if (count == 0) {
          assertEquals("getDay", spec.methodSpecs.get(0).name);
          assertEquals("java.lang.String", spec.methodSpecs.get(0).returnType.toString());
        }

        count++;
      }
    });
  }

  @Test
  public void overrideScalarTypes() throws Exception {


    V10TypeRegistry registry = new V10TypeRegistry();
    CurrentBuild cb = RamlV10.buildType(this, "overrideScalarTypes.raml", registry, "foo", ".");
    JavaPoetTypeGenerator gen = cb.getBuiltType("TypeOne");

    gen.output(new CodeContainer<TypeSpec.Builder>() {

      int count = 0;

      @Override
      public void into(TypeSpec.Builder g) throws IOException {
        TypeSpec spec = g.build();
        System.err.println(spec);
        if (count == 0) {
          assertEquals("getDay", spec.methodSpecs.get(0).name);
          assertEquals("java.lang.Long", spec.methodSpecs.get(0).returnType.toString());
        }
        count++;
      }
    });
  }

  @Test
  public void objectTypes() throws Exception {


    V10TypeRegistry registry = new V10TypeRegistry();
    CurrentBuild cb = RamlV10.buildType(this, "objectTypes.raml", registry, "foo", ".");
    JavaPoetTypeGenerator gen = cb.getBuiltType("TypeOne");

    gen.output(new CodeContainer<TypeSpec.Builder>() {

      int count = 0;

      @Override
      public void into(TypeSpec.Builder g) throws IOException {
        TypeSpec spec = g.build();
        System.err.println(spec);
        if (count == 0) {
          assertEquals("getDay", spec.methodSpecs.get(0).name);
          assertEquals("model.ReturnValue", spec.methodSpecs.get(0).returnType.toString());
        }

        count++;
      }
    });


  }

  @Test
  public void arrayOfScalar() throws Exception {

    V10TypeRegistry registry = new V10TypeRegistry();
    CurrentBuild cb = RamlV10.buildType(this, "arrayOfScalar.raml", registry, "foo", ".");
    JavaPoetTypeGenerator gen = cb.getBuiltType("TypeOne");

    gen.output(new CodeContainer<TypeSpec.Builder>() {

      int count = 0;

      @Override
      public void into(TypeSpec.Builder g) throws IOException {
        TypeSpec spec = g.build();
        System.err.println(spec);
        if (count == 0) {
          assertEquals("getDay", spec.methodSpecs.get(0).name);
          assertEquals("java.util.List<java.lang.String>", spec.methodSpecs.get(0).returnType.toString());
        }

        count++;
      }
    });
  }

  @Test
  public void arrayOfInteger() throws Exception {

    V10TypeRegistry registry = new V10TypeRegistry();
    CurrentBuild cb = RamlV10.buildType(this, "arrayOfInteger.raml", registry, "foo", ".");
    JavaPoetTypeGenerator gen = cb.getBuiltType("TypeOne");

    gen.output(new CodeContainer<TypeSpec.Builder>() {

      int count = 0;

      @Override
      public void into(TypeSpec.Builder g) throws IOException {
        TypeSpec spec = g.build();

        if (count == 0) {
          assertEquals("getDay", spec.methodSpecs.get(0).name);
          assertEquals("java.util.List<java.lang.Integer>", spec.methodSpecs.get(0).returnType.toString());
        }

        count++;
      }
    });
  }

  @Test
  public void arrayOfObjects() throws Exception {

    V10TypeRegistry registry = new V10TypeRegistry();
    CurrentBuild cb = RamlV10.buildType(this, "arrayOfObjects.raml", registry, "foo", ".");
    JavaPoetTypeGenerator gen = cb.getBuiltType("TypeOne");

    gen.output(new CodeContainer<TypeSpec.Builder>() {

      int count = 0;

      @Override
      public void into(TypeSpec.Builder g) throws IOException {
        TypeSpec spec = g.build();
        System.err.println(spec);
        if (count == 0) {
          assertEquals("getDay", spec.methodSpecs.get(0).name);
          assertEquals("java.util.List<model.ReturnValue>", spec.methodSpecs.get(0).returnType.toString());
        }

        count++;
      }
    });
  }

  @Test
  public void enums() throws Exception {

    V10TypeRegistry registry = new V10TypeRegistry();
    CurrentBuild cb = RamlV10.buildType(this, "enums.raml", registry, "foo", ".");
    JavaPoetTypeGenerator gen = cb.getBuiltType("TypeOne");

    gen.output(new CodeContainer<TypeSpec.Builder>() {

      int count = 0;

      @Override
      public void into(TypeSpec.Builder g) throws IOException {
        TypeSpec spec = g.build();
        System.err.println(spec);
        if (count == 0) {
          assertEquals("getDay", spec.methodSpecs.get(0).name);
          assertEquals("DayType", spec.methodSpecs.get(0).returnType.toString());
        }

        count++;
      }
    }, BuildPhase.INTERFACE);

    JavaPoetTypeGenerator gen2 = cb.getBuiltType("TypeTwo");

    gen2.output(new CodeContainer<TypeSpec.Builder>() {

      int count = 0;

      @Override
      public void into(TypeSpec.Builder g) throws IOException {
        TypeSpec spec = g.build();
        System.err.println(spec);
        if (count == 0) {
          assertEquals(2, spec.enumConstants.size());
          assertEquals("\"either\"", spec.enumConstants.get("EITHER").anonymousTypeArguments.toString());
          assertEquals("\"or\"", spec.enumConstants.get("OR").anonymousTypeArguments.toString());
        }

        count++;
      }
    });

  }

}
