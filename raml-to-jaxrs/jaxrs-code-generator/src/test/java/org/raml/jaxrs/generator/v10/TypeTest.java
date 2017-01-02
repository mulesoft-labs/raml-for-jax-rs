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

/**
 * Created by Jean-Philippe Belanger on 12/31/16.
 * Just potential zeroes and ones
 */
public class TypeTest {


    @Test
    public void wha() throws Exception {

        V10TypeRegistry registry = new V10TypeRegistry();
        CurrentBuild cb = Raml.buildType(this, "extendObject.raml", registry, "foo", ".");
        JavaPoetTypeGenerator gen = cb.getBuiltType("ObjectOne");
        gen.output(new CodeContainer<TypeSpec.Builder>() {
            @Override
            public void into(TypeSpec.Builder g) throws IOException {
                System.err.println(g);
            }
        });
    }
}
