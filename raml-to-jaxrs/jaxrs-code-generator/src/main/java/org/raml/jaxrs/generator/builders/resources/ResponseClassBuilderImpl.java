package org.raml.jaxrs.generator.builders.resources;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import org.raml.jaxrs.generator.CurrentBuild;

import javax.lang.model.element.Modifier;
import javax.ws.rs.core.Response;

import static org.raml.jaxrs.generator.builders.TypeBuilderHelpers.forParameter;

/**
 * Created by Jean-Philippe Belanger on 11/5/16.
 * Just potential zeroes and ones
 */
public class ResponseClassBuilderImpl implements ResponseClassBuilder {

    private final CurrentBuild currentBuild;
    private final TypeSpec.Builder owningInterface;
    private final TypeSpec.Builder current;

    public ResponseClassBuilderImpl(CurrentBuild currentBuild, TypeSpec.Builder owningInterface, String className) {
        this.currentBuild = currentBuild;
        this.owningInterface = owningInterface;
        this.current = createResponseClass(currentBuild.getDefaultPackage(), className);
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

        return current.build().name;
    }

    @Override
    public void withResponse(String httpCode) {
        TypeSpec currentClass = current.build();
        current.addMethod(
                MethodSpec.methodBuilder("respond" + httpCode)
                        .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                        .addStatement("Response.ResponseBuilder responseBuilder = Response.status("+ httpCode +")")
                        .addStatement("return new $N(responseBuilder.build())", currentClass)
                        .returns(TypeVariableName.get(currentClass.name))
                        .build()
        );
    }

    @Override
    public void withResponse(String httpCode, String name, String type) {

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

        current.addMethod(
                builder.build()
        );
    }

    @Override
    public void output() {
        owningInterface.addType(current.build());
    }
}
