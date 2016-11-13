package org.raml.jaxrs.generator.builders.types;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.Names;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.raml.jaxrs.generator.builders.TypeBuilderHelpers.forField;
import static org.raml.jaxrs.generator.builders.TypeBuilderHelpers.forParameter;
import static org.raml.jaxrs.generator.builders.TypeBuilderHelpers.forReturnValue;

/**
 * Created by Jean-Philippe Belanger on 11/13/16.
 * Just potential zeroes and ones
 */
public class TypeBuilderImplementation implements TypeBuilder {
    private final CurrentBuild build;
    private final String name;
    private final String parentType;

    private Map<String, PropertyInfo> propertyInfos = new HashMap<>();

    public TypeBuilderImplementation(CurrentBuild build, String name, String parentType) {
        this.build = build;
        this.name = name;
        this.parentType = parentType;
    }

    @Override
    public TypeBuilder addProperty(String type, String name) {

        propertyInfos.put(name, new PropertyInfo(type, name));
        return this;
    }

    @Override
    public void ouput(String rootDirectory) throws IOException {

        TypeSpec.Builder typeSpec = TypeSpec
                .classBuilder(ClassName.get(build.getDefaultPackage(), Names.buildTypeName(name) + "Impl"))
                .addModifiers(Modifier.PUBLIC);

        typeSpec.addSuperinterface(ClassName.get(build.getDefaultPackage(), Names.buildTypeName(parentType)));


        for (PropertyInfo propertyInfo : propertyInfos.values()) {

            build.javaTypeName(propertyInfo.getType(), forField(typeSpec, propertyInfo.getName()));

            final MethodSpec.Builder getSpec = MethodSpec
                    .methodBuilder("get" + Names.buildTypeName(propertyInfo.getName()))
                    .addModifiers(Modifier.PUBLIC)
                    .addStatement("return this." + propertyInfo.getName());
            ;
            build.javaTypeName(propertyInfo.getType(), forReturnValue(getSpec));

            MethodSpec.Builder setSpec = MethodSpec
                    .methodBuilder("set" + Names.buildTypeName(propertyInfo.getName()))
                    .addModifiers(Modifier.PUBLIC)
                    .addStatement("this." + propertyInfo.getName() + " = " + propertyInfo.getName());

            build.javaTypeName(propertyInfo.getType(), forParameter(setSpec, propertyInfo.getName()));
            typeSpec.addMethod(getSpec.build());
            typeSpec.addMethod(setSpec.build());
        }

        JavaFile.Builder file = JavaFile.builder(build.getDefaultPackage(), typeSpec.build());
        file.build().writeTo(new File(rootDirectory));
    }


    private boolean noParentDeclares(List<TypeBuilder> propsFromParents, String name) {

        for (TypeBuilder propsFromParent : propsFromParents) {

            if (propsFromParent.declares(name)) {

                return false;
            }

        }

        return true;
    }

    @Override
    public boolean declares(String name) {

        return true;
    }
}
