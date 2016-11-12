package org.raml.jaxrs.generator.builders;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import org.raml.jaxrs.generator.ScalarTypes;

import javax.lang.model.element.Modifier;
import javax.ws.rs.core.Response;

/**
 * Created by Jean-Philippe Belanger on 11/5/16.
 * Just potential zeroes and ones
 */
public class ResponseClassBuilderImpl implements ResponseClassBuilder {

    private final TypeSpec.Builder owningInterface;
    private final String packageName;
    private final TypeSpec.Builder current;

    public ResponseClassBuilderImpl(TypeSpec.Builder owningInterface, String packageName, String className) {
        this.owningInterface = owningInterface;
        this.packageName = packageName;
        this.current = createResponseClass(packageName, className);
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
        current.addMethod(
                MethodSpec.methodBuilder("respond" + httpCode)
                        .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                        .addParameter(ParameterSpec.builder(ScalarTypes.scalarToJavaType(type), "entity").build())
                        .addStatement("Response.ResponseBuilder responseBuilder = Response.status("+ httpCode +")")
                        .addStatement("responseBuilder.entity(entity)")
                        .addStatement("return new $N(responseBuilder.build())", currentClass)
                        .returns(TypeVariableName.get(currentClass.name))
                        .build()
        );
    }

    @Override
    public void output() {
        owningInterface.addType(current.build());
    }
}
