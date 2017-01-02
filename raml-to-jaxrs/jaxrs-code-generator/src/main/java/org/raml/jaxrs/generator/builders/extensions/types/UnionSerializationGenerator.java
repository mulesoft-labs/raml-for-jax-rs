package org.raml.jaxrs.generator.builders.extensions.types;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import joptsimple.internal.Strings;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.Names;
import org.raml.jaxrs.generator.builders.CodeContainer;
import org.raml.jaxrs.generator.builders.JavaPoetTypeGenerator;
import org.raml.jaxrs.generator.builders.TypeGenerator;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.UnionTypeDeclaration;

import javax.annotation.Nullable;
import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by Jean-Philippe Belanger on 1/2/17.
 * Just potential zeroes and ones
 */
public class UnionSerializationGenerator implements JavaPoetTypeGenerator {
    private final CurrentBuild currentBuild;
    private final UnionTypeDeclaration unionTypeDeclaration;
    private final ClassName name;

    public UnionSerializationGenerator(CurrentBuild currentBuild, UnionTypeDeclaration unionTypeDeclaration, ClassName name) {
        this.currentBuild = currentBuild;
        this.unionTypeDeclaration = unionTypeDeclaration;
        this.name = name;
    }

    @Override
    public void output(CodeContainer<TypeSpec.Builder> rootDirectory) throws IOException {

        ClassName unionTypeName = ClassName.get(currentBuild.getModelPackage(), Names.typeName(unionTypeDeclaration.name(), "Union"));
        TypeSpec.Builder builder = TypeSpec.classBuilder(name);
        MethodSpec.Builder deserialize = MethodSpec.methodBuilder("deserialize")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(ClassName.get(JsonParser.class), "jsonParser").build())
                .addParameter(ParameterSpec.builder(ClassName.get(DeserializationContext.class), "jsonContext").build())
                .addException(IOException.class)
                .addException(JsonProcessingException.class)
                .returns(unionTypeName)
                .addStatement("$T mapper  = new $T()", ObjectMapper.class, ObjectMapper.class)
                .addStatement("$T<String, Object> map = mapper.readValue(jsonParser, Map.class)", Map.class);

        builder
                .superclass(ParameterizedTypeName.get(ClassName.get(StdDeserializer.class), unionTypeName))
                .addMethod(
                        MethodSpec.constructorBuilder()
                                .addModifiers(Modifier.PUBLIC)
                                .addCode("super($T.class);", unionTypeName).build()

                );

        for (TypeDeclaration typeDeclaration : unionTypeDeclaration.of()) {

            ClassName unionPossibility = ClassName.get(currentBuild.getModelPackage(), Names.typeName(typeDeclaration.name()));

            String fieldName = typeDeclaration.name();
            deserialize.addStatement("if ( looksLike" + fieldName + "(map) ) return new $T(mapper.convertValue(map, $T.class))", unionTypeName, unionPossibility);
            buildLooksLike(builder, typeDeclaration);
        }

        deserialize.addStatement("throw new $T($S + map)", IOException.class, "Can't figure out type of object");
        builder.addMethod(deserialize.build());

        rootDirectory.into(builder);
    }

    private void buildLooksLike(TypeSpec.Builder builder, TypeDeclaration typeDeclaration) {

        String name = Names.methodName("looksLike", typeDeclaration.name());
        MethodSpec.Builder spec = MethodSpec.methodBuilder(name).addParameter(ParameterizedTypeName.get(ClassName.get(Map.class), ClassName.get(String.class), ClassName.get(Object.class)), "map");
        if ( typeDeclaration instanceof ObjectTypeDeclaration ) {

            ObjectTypeDeclaration otd = (ObjectTypeDeclaration) typeDeclaration;
            List<String> names =  Lists.transform(otd.properties(), new Function<TypeDeclaration, String>() {
                @Nullable
                @Override
                public String apply(@Nullable TypeDeclaration input) {
                    return "\"" + input.name() + "\"";
                }
            });

            spec.addStatement("return map.keySet().containsAll($T.asList($L))", Arrays.class, Strings.join(names, ","));
        }

        spec.addModifiers(Modifier.PRIVATE).returns(TypeName.BOOLEAN);
        builder.addMethod(spec.build());
    }

    @Override
    public void output(CodeContainer<TypeSpec.Builder> rootDirectory, TYPE type) throws IOException {

        output(rootDirectory);
    }

    @Override
    public TypeName getGeneratedJavaType() {
        return name;
    }
}
