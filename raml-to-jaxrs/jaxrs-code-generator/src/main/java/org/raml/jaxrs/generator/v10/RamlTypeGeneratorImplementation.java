package org.raml.jaxrs.generator.v10;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.Names;
import org.raml.jaxrs.generator.builders.AbstractTypeGenerator;
import org.raml.jaxrs.generator.builders.CodeContainer;
import org.raml.jaxrs.generator.builders.JavaPoetTypeGenerator;
import org.raml.jaxrs.generator.builders.TypeGenerator;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jean-Philippe Belanger on 11/13/16.
 * Just potential zeroes and ones
 */
public class RamlTypeGeneratorImplementation extends AbstractTypeGenerator<TypeSpec.Builder> implements JavaPoetTypeGenerator {
    private final CurrentBuild build;
    private final ClassName className;
    private final ClassName parentClassName;
    private Map<String, JavaPoetTypeGenerator> internalTypes = new HashMap<>();

    private Map<String, PropertyInfo> propertyInfos = new HashMap<>();
    private final V10GType typeDeclaration;


    public RamlTypeGeneratorImplementation(CurrentBuild build, ClassName className, ClassName parentClassName,
            List<PropertyInfo> properties, Map<String, JavaPoetTypeGenerator> internalTypes, V10GType typeDeclaration) {

        this.build = build;
        this.className = className;
        this.parentClassName = parentClassName;
        this.internalTypes = internalTypes;
        this.typeDeclaration = typeDeclaration;
        for (PropertyInfo property : properties) {
            propertyInfos.put(property.getName(), property);
        }

    }


    @Override
    public void output(CodeContainer<TypeSpec.Builder> container, TYPE type) throws IOException {

        ObjectTypeDeclaration object = (ObjectTypeDeclaration) typeDeclaration.implementation();

        final TypeSpec.Builder typeSpec = TypeSpec
                .classBuilder(className)
                .addModifiers(Modifier.PUBLIC);

        build.withTypeListeners().onTypeImplementation(build, typeSpec, typeDeclaration.implementation());

        if ( parentClassName != null ) {
            typeSpec.addSuperinterface(parentClassName);
        }


        for (TypeGenerator internalType : internalTypes.values()) {

            internalType.output(new CodeContainer<TypeSpec.Builder>() {
                @Override
                public void into(TypeSpec.Builder g) throws IOException {

                    g.addModifiers(Modifier.STATIC);
                    typeSpec.addType(g.build());
                }
            }, type);
        }

        for (PropertyInfo propertyInfo : propertyInfos.values()) {

            FieldSpec.Builder fieldSpec = FieldSpec.builder(propertyInfo.resolve(build), Names.variableName(propertyInfo.getName())).addModifiers(Modifier.PRIVATE);
            build.withTypeListeners().onFieldImplementation(build,
                    fieldSpec, (TypeDeclaration) propertyInfo.getType().implementation());
            if ( propertyInfo.getName().equals(object.discriminator()) ) {
                fieldSpec.initializer("$S", object.discriminatorValue());
            }
            typeSpec.addField(fieldSpec.build());

            final MethodSpec.Builder getSpec = MethodSpec
                    .methodBuilder("get" + Names.typeName(propertyInfo.getName()))
                    .addModifiers(Modifier.PUBLIC)
                    .addStatement("return this." + Names.variableName(propertyInfo.getName()));

            getSpec.returns(propertyInfo.resolve(build));
            build.withTypeListeners().onGetterMethodImplementation(build,
                    getSpec, (TypeDeclaration) propertyInfo.getType().implementation());
            typeSpec.addMethod(getSpec.build());

            if ( ! propertyInfo.getName().equals(object.discriminator()) ) {

                MethodSpec.Builder setSpec = MethodSpec
                        .methodBuilder("set" + Names.typeName(propertyInfo.getName()))
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement("this." + Names.variableName(propertyInfo.getName()) + " = " + Names
                                .variableName(propertyInfo.getName()));

                ParameterSpec.Builder parameterSpec = ParameterSpec
                        .builder(propertyInfo.resolve(build), Names.variableName(propertyInfo.getName()));
                build.withTypeListeners().onSetterMethodImplementation(build, setSpec,
                        parameterSpec, (TypeDeclaration) propertyInfo.getType().implementation());

                setSpec.addParameter(parameterSpec.build());
                typeSpec.addMethod(setSpec.build());
            }
        }

        container.into(typeSpec);
    }


    @Override
    public TypeName getGeneratedJavaType() {
        return className;
    }
}
