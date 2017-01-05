package org.raml.jaxrs.generator.v10;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.GeneratorType;
import org.raml.jaxrs.generator.Names;
import org.raml.jaxrs.generator.builders.CodeContainer;
import org.raml.jaxrs.generator.builders.JavaPoetTypeGenerator;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.UnionTypeDeclaration;

import javax.lang.model.element.Modifier;
import java.io.IOException;

/**
 * Created by Jean-Philippe Belanger on 1/1/17.
 * Just potential zeroes and ones
 */
public class UnionTypeGenerator implements JavaPoetTypeGenerator {


    private final V10TypeRegistry registry;
    private final V10GType v10GType;
    private final ClassName javaName;
    private final CurrentBuild currentBuild;

    UnionTypeGenerator(V10TypeRegistry registry, V10GType v10GType, ClassName javaName, CurrentBuild currentBuild) {

        this.registry = registry;
        this.v10GType = v10GType;
        this.javaName = javaName;
        this.currentBuild = currentBuild;
    }

    @Override
    public void output(CodeContainer<TypeSpec.Builder> rootDirectory) throws IOException {

        UnionTypeDeclaration union = (UnionTypeDeclaration) v10GType.implementation();

        TypeSpec.Builder builder = TypeSpec.classBuilder(javaName).addModifiers(Modifier.PUBLIC);
        for (TypeDeclaration typeDeclaration : union.of()) {

            V10GType type = registry.fetchType(typeDeclaration);

            TypeName typeName = type.defaultJavaTypeName(currentBuild.getModelPackage());
            builder
                    .addField(FieldSpec.builder(typeName, typeDeclaration.name(), Modifier.PRIVATE).build())
                    .addField(FieldSpec.builder(TypeName.BOOLEAN, Names.variableName("is" , typeDeclaration.name()), Modifier.PRIVATE).build())
                    .addMethod(
                            MethodSpec.constructorBuilder()
                                    .addParameter(ParameterSpec.builder(typeName, typeDeclaration.name()).build())
                                    .addModifiers(Modifier.PUBLIC)
                                    .addStatement("this.$L = $L", typeDeclaration.name(), typeDeclaration.name())
                                    .addStatement("this.is$L = true", typeDeclaration.name())
                                    .build())
                    .addMethod(
                            MethodSpec.methodBuilder(Names.methodName("get", typeDeclaration.name()))
                                .addModifiers(Modifier.PUBLIC)
                                .returns(typeName)
                                .addStatement("if ( is$L == false) throw new $T(\"fetching wrong type out of the union: $T\")", typeDeclaration.name(), IllegalStateException.class, typeName)
                                .addStatement("return $L", typeDeclaration.name())
                                .build()
                    )
                    .addMethod(
                            MethodSpec.methodBuilder(Names.methodName("is", typeDeclaration.name()))
                                .addStatement("return " +  Names.variableName("is" , typeDeclaration.name()))
                                .returns(TypeName.BOOLEAN).build()
                    );
        }

        currentBuild.withTypeListeners().onUnionType(currentBuild, builder, v10GType);
        rootDirectory.into(builder);
    }

    @Override
    public void output(CodeContainer<TypeSpec.Builder> rootDirectory, TYPE type) throws IOException {

        output(rootDirectory);
    }

    @Override
    public TypeName getGeneratedJavaType() {
        return javaName;
    }
}
