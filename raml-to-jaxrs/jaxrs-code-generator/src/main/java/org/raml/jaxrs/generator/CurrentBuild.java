package org.raml.jaxrs.generator;

import com.google.common.collect.Lists;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import com.squareup.javapoet.WildcardTypeName;
import joptsimple.internal.Strings;
import org.raml.jaxrs.generator.builders.ResourceBuilder;
import org.raml.jaxrs.generator.builders.ResourceInterface;

import javax.lang.model.element.Modifier;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.raml.jaxrs.generator.Paths.relativize;

/**
 * Created by Jean-Philippe Belanger on 10/26/16.
 * The art of building stuff is here.
 * Factory for building root stuff.
 */
public class CurrentBuild {

    private final String defaultPackage;

    private final List<ResourceBuilder> resources = new ArrayList<ResourceBuilder>();

    public CurrentBuild(String defaultPackage) {

        this.defaultPackage = defaultPackage;
    }

    public ResourceBuilder createResource(String name, String relativeURI) {

        ResourceBuilder builder = new ResourceInterface(defaultPackage, name, relativize(relativeURI));
        resources.add(builder);
        return builder;
    }

    public void generate(String rootDirectory) throws IOException {

        buildSupportClasses(rootDirectory);
        for (ResourceBuilder resource : resources) {
            resource.output(rootDirectory);
        }
    }

    private void buildSupportClasses(String rootDir) throws IOException {

        TypeSpec.Builder builder = TypeSpec.classBuilder("ResponseDelegate")
                .addModifiers(Modifier.PUBLIC)
                .superclass(Response.class)
                .addField(FieldSpec.builder(Response.class, "delegate", Modifier.PRIVATE, Modifier.FINAL).build());

        builder.addMethod(
                MethodSpec.constructorBuilder()
                        .addParameter(
                                ParameterSpec.builder(Response.class, "delegate").build()
                        )
                        .addModifiers(Modifier.PROTECTED)
                        .addCode("this.delegate = delegate;\n").build()
        );

        for (Method m : Response.class.getDeclaredMethods()) {

            if (java.lang.reflect.Modifier.isStatic(m.getModifiers())) {
                continue;
            }

            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(m.getName())
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class);

            if ( m.getGenericReturnType().toString().equals("T")) {
                //ParameterizedTypeName.get(ClassName.get(Class.class), WildcardTypeName.subtypeOf(Object.class);
                methodBuilder.returns(TypeVariableName.get("<T> T"));
            } else {
                    methodBuilder.returns(m.getGenericReturnType());
            }

            int p = 0;
            for (Type aClass : m.getGenericParameterTypes()) {
                methodBuilder.addParameter(ParameterSpec.builder(aClass, "p" + (p++)).build());
            }

            if (m.getReturnType() == void.class) {
                methodBuilder.addCode("this.delegate." + m.getName() + "(" + buildParamList(m) + ");\n");
            } else {
                methodBuilder.addCode("return this.delegate." + m.getName() + "(" + buildParamList(m) + ");\n");
            }

            builder.addMethod(methodBuilder.build());

            JavaFile.Builder file = JavaFile.builder(defaultPackage, builder.build());
            file.build().writeTo(new File(rootDir));
        }
    }

    private String buildParamList(Method m) {

        ArrayList<String> list = new ArrayList<String>();
        for(int i = 0; i < m.getParameterTypes().length; i ++) {
            list.add("p" + i);
        }

        return Strings.join(list, ",");
    }
}
