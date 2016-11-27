package org.raml.jaxrs.generator.builders.resources;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.builders.CodeContainer;
import org.raml.jaxrs.generator.builders.Generator;
import org.raml.jaxrs.generator.builders.OutputBuilder;

import javax.lang.model.element.Modifier;
import javax.ws.rs.core.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.raml.jaxrs.generator.builders.TypeBuilderHelpers.forParameter;

/**
 * Created by Jean-Philippe Belanger on 11/5/16.
 * Just potential zeroes and ones
 */
public class ResponseClassBuilderImpl implements ResponseClassBuilder {

    private final CurrentBuild currentBuild;
    private final String className;
    private List<OutputBuilder<TypeSpec.Builder>> builders = new ArrayList<>();


    public ResponseClassBuilderImpl(CurrentBuild currentBuild, String className) {
        this.currentBuild = currentBuild;
        this.className = className;
    }

    private TypeSpec.Builder createResponseClass(String packageName, String className) {
        return TypeSpec.classBuilder(className + "Response")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .superclass(ClassName.get(packageName, "ResponseDelegate")) // todo:  package name
                .addMethod(
                        MethodSpec.constructorBuilder()
                                .addParameter(Response.class, "response")
                                .addModifiers(Modifier.PRIVATE)
                                .addCode("super(response);\n").build()
                );
    }

    @Override
    public String name() {

        // idiotic.  Must fix
        return createResponseClass(currentBuild.getDefaultPackage(), className).build().name;
    }

    @Override
    public void withResponse(final String httpCode) {

        builders.add(new OutputBuilder<TypeSpec.Builder>() {
            @Override
            public void build(TypeSpec.Builder response) {

                TypeSpec currentClass = response.build();
                response.addMethod(
                        MethodSpec.methodBuilder("respond" + httpCode)
                                .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                                .addStatement("Response.ResponseBuilder responseBuilder = Response.status("+ httpCode +")")
                                .addStatement("return new $N(responseBuilder.build())", currentClass)
                                .returns(TypeVariableName.get(currentClass.name))
                                .build()
                );
            }
        });
    }

    @Override
    public void withResponse(final String httpCode, String name, final String type) {

        builders.add(new OutputBuilder<TypeSpec.Builder>() {
            @Override
            public void build(TypeSpec.Builder current) {

                TypeSpec currentClass = current.build();
                MethodSpec.Builder builder = MethodSpec.methodBuilder("respond" + httpCode);
                builder
                        .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                        .addStatement("Response.ResponseBuilder responseBuilder = Response.status("+ httpCode +")")
                        .addStatement("responseBuilder.entity(entity)")
                        .addStatement("return new $N(responseBuilder.build())", currentClass)
                        .returns(TypeVariableName.get(currentClass.name))
                        .build();

                currentBuild.javaTypeName(type, forParameter(builder, "entity" ));
                current.addMethod(builder.build());
            }
        });
    }

    @Override
    public void output(CodeContainer<TypeSpec.Builder> container) throws IOException {

        TypeSpec.Builder current = createResponseClass(currentBuild.getDefaultPackage(), className);

        for (OutputBuilder<TypeSpec.Builder> builder : builders) {
            builder.build(current);
        }

        container.into(current);
    }
}
