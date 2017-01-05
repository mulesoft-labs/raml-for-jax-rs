package org.raml.jaxrs.generator.v10;

import com.squareup.javapoet.TypeSpec;
import org.junit.Test;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.GeneratorType;
import org.raml.jaxrs.generator.builders.CodeContainer;
import org.raml.jaxrs.generator.builders.JavaPoetTypeGenerator;
import org.raml.jaxrs.generator.builders.TypeGenerator;
import org.raml.jaxrs.generator.utils.Raml;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Created by Jean-Philippe Belanger on 12/31/16.
 * Just potential zeroes and ones
 */
public class TypeTest {


    @Test
    public void union() throws Exception {

        V10TypeRegistry registry = new V10TypeRegistry();
        CurrentBuild cb = Raml.buildType(this, "simpleUnion.raml", registry, "foo", ".");
        JavaPoetTypeGenerator gen = cb.getBuiltType("UnionType");
        gen.output(new CodeContainer<TypeSpec.Builder>() {
            @Override
            public void into(TypeSpec.Builder g) throws IOException {
                System.err.println(g.build().toString());

                TypeSpec spec = g.build();
                assertEquals(4, spec.fieldSpecs.size());
                assertEquals("TypeOne", spec.fieldSpecs.get(0).type.toString());
                assertEquals("TypeOne", spec.fieldSpecs.get(0).name);
                assertEquals("boolean", spec.fieldSpecs.get(1).type.toString());
                assertEquals("isTypeOne", spec.fieldSpecs.get(1).name);
                assertEquals("TypeTwo", spec.fieldSpecs.get(2).type.toString());
                assertEquals("TypeTwo", spec.fieldSpecs.get(2).name);
                assertEquals("boolean", spec.fieldSpecs.get(3).type.toString());
                assertEquals("isTypeTwo", spec.fieldSpecs.get(3).name);
            }
        });
    }

    @Test
    public void inlineObject() throws Exception {


        V10TypeRegistry registry = new V10TypeRegistry();
        CurrentBuild cb = Raml.buildType(this, "inlineObject.raml", registry, "foo", ".");
        JavaPoetTypeGenerator gen = cb.getBuiltType("TypeOne");

        gen.output(new CodeContainer<TypeSpec.Builder>() {

            int count = 0;
            @Override
            public void into(TypeSpec.Builder g) throws IOException {
                TypeSpec spec = g.build();
                System.err.println(spec);
                if ( count == 0 ) {
                    checkMethodsOfInterface(spec);
                    assertEquals("DayType", spec.typeSpecs.get(0).name);
                } else {

                    checkMethodsOfInterface(spec);
                    assertEquals("DayTypeImpl", spec.typeSpecs.get(0).name);
                }

                count ++;
            }
        });


    }

    private void checkMethodsOfInterface(TypeSpec spec) {
        assertEquals(2, spec.methodSpecs.size());
        assertEquals("getDay", spec.methodSpecs.get(0).name);
        assertEquals("DayType", spec.methodSpecs.get(0).returnType.toString());

        assertEquals("setDay", spec.methodSpecs.get(1).name);
        assertEquals("DayType", spec.methodSpecs.get(1).parameters.get(0).type.toString());
        assertEquals(1, spec.typeSpecs.size());
    }


    @Test
    public void scalarTypes() throws Exception {


        V10TypeRegistry registry = new V10TypeRegistry();
        CurrentBuild cb = Raml.buildType(this, "scalarTypes.raml", registry, "foo", ".");
        JavaPoetTypeGenerator gen = cb.getBuiltType("TypeOne");

        gen.output(new CodeContainer<TypeSpec.Builder>() {

            int count = 0;
            @Override
            public void into(TypeSpec.Builder g) throws IOException {
                TypeSpec spec = g.build();
                System.err.println(spec);
                if ( count == 0 ) {
                    assertEquals("getDay", spec.methodSpecs.get(0).name);
                    assertEquals("java.lang.String", spec.methodSpecs.get(0).returnType.toString());
                }

                count ++;
            }
        });


    }

    @Test
    public void objectTypes() throws Exception {


        V10TypeRegistry registry = new V10TypeRegistry();
        CurrentBuild cb = Raml.buildType(this, "objectTypes.raml", registry, "foo", ".");
        JavaPoetTypeGenerator gen = cb.getBuiltType("TypeOne");

        gen.output(new CodeContainer<TypeSpec.Builder>() {

            int count = 0;
            @Override
            public void into(TypeSpec.Builder g) throws IOException {
                TypeSpec spec = g.build();
                System.err.println(spec);
                if ( count == 0 ) {
                    assertEquals("getDay", spec.methodSpecs.get(0).name);
                    assertEquals("ReturnValue", spec.methodSpecs.get(0).returnType.toString());
                }

                count ++;
            }
        });


    }

    @Test
    public void arrayOfScalar() throws Exception {


        V10TypeRegistry registry = new V10TypeRegistry();
        CurrentBuild cb = Raml.buildType(this, "arrayOfScalar.raml", registry, "foo", ".");
        JavaPoetTypeGenerator gen = cb.getBuiltType("TypeOne");

        gen.output(new CodeContainer<TypeSpec.Builder>() {

            int count = 0;
            @Override
            public void into(TypeSpec.Builder g) throws IOException {
                TypeSpec spec = g.build();
                System.err.println(spec);
                if ( count == 0 ) {
                    assertEquals("getDay", spec.methodSpecs.get(0).name);
                    assertEquals("java.util.List<java.lang.String>", spec.methodSpecs.get(0).returnType.toString());
                }

                count ++;
            }
        });
    }

    @Test
    public void arrayOfObjects() throws Exception {


        V10TypeRegistry registry = new V10TypeRegistry();
        CurrentBuild cb = Raml.buildType(this, "arrayOfObjects.raml", registry, "foo", ".");
        JavaPoetTypeGenerator gen = cb.getBuiltType("TypeOne");

        gen.output(new CodeContainer<TypeSpec.Builder>() {

            int count = 0;
            @Override
            public void into(TypeSpec.Builder g) throws IOException {
                TypeSpec spec = g.build();
                System.err.println(spec);
                if ( count == 0 ) {
                    assertEquals("getDay", spec.methodSpecs.get(0).name);
                    assertEquals("java.util.List<ReturnValue>", spec.methodSpecs.get(0).returnType.toString());
                }

                count ++;
            }
        });
    }

}
