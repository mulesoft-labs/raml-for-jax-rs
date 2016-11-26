package org.raml.jaxrs.generator.builders.types;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.Names;
import org.raml.jaxrs.generator.builders.CodeContainer;
import org.raml.jaxrs.generator.builders.SpecFixer;

import javax.lang.model.element.Modifier;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;
import java.util.ArrayList;
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
public class RamlTypeGeneratorImplementation implements RamlTypeGenerator {
    private final CurrentBuild build;
    private final String name;
    private final String parentType;

    private Map<String, PropertyInfo> propertyInfos = new HashMap<>();
    private List<RamlTypeGeneratorImplementation> internalTypes = new ArrayList<>();

    public RamlTypeGeneratorImplementation(CurrentBuild build, String name, String parentType) {
        this.build = build;
        this.name = name;
        this.parentType = parentType;
    }

    @Override
    public RamlTypeGenerator addProperty(String type, String name, boolean internalType) {

        propertyInfos.put(name, new PropertyInfo(type, name, internalType));
        return this;
    }

    @Override
    public RamlTypeGenerator addInternalType(RamlTypeGenerator internalGenerator) {

        internalTypes.add((RamlTypeGeneratorImplementation) internalGenerator);
        return this;
    }

    @Override
    public void output(CodeContainer<TypeSpec.Builder> container) throws IOException {

        final TypeSpec.Builder typeSpec = TypeSpec
                .classBuilder(ClassName.get(build.getDefaultPackage(), Names.buildTypeName(name) + "Impl"))
                .addModifiers(Modifier.PUBLIC);
        typeSpec.addAnnotation(AnnotationSpec.builder(XmlRootElement.class).addMember("name", "$S", name).build());
        typeSpec.addAnnotation(AnnotationSpec.builder(XmlAccessorType.class).addMember("value", "$T.$L", XmlAccessType.class, "FIELD").build());

        typeSpec.addSuperinterface(ClassName.get(build.getDefaultPackage(), Names.buildTypeName(parentType)));


        for (RamlTypeGeneratorImplementation internalType : internalTypes) {

            internalType.output(new CodeContainer<TypeSpec.Builder>() {
                @Override
                public void into(TypeSpec.Builder g) throws IOException {

                    g.addModifiers(Modifier.STATIC);
                    typeSpec.addType(g.build());
                }
            });
        }

        for (PropertyInfo propertyInfo : propertyInfos.values()) {

            if ( propertyInfo.isInternalType() ) {

                typeSpec.addField(FieldSpec.builder(ClassName.get("", Names.buildTypeName(propertyInfo.getName() + "_Type")), propertyInfo.getName()).addModifiers(Modifier.PRIVATE).build());
            } else {
                build.javaTypeName(propertyInfo.getType(), forField(typeSpec, propertyInfo.getName(),
                        new SpecFixer<FieldSpec.Builder>() {
                            @Override
                            public void adjust(FieldSpec.Builder spec) {
                                spec.addAnnotation(AnnotationSpec.builder(XmlElement.class).build());
                            }
                        }));
            }

            final MethodSpec.Builder getSpec = MethodSpec
                    .methodBuilder("get" + Names.buildTypeName(propertyInfo.getName()))
                    .addModifiers(Modifier.PUBLIC)
                    .addStatement("return this." + propertyInfo.getName());

            if ( propertyInfo.isInternalType() ) {
                getSpec.returns(ClassName.get("", Names.buildTypeName(propertyInfo.getName() + "_Type")));
            } else {
                build.javaTypeName(propertyInfo.getType(), forReturnValue(getSpec));
            }

            MethodSpec.Builder setSpec = MethodSpec
                    .methodBuilder("set" + Names.buildTypeName(propertyInfo.getName()))
                    .addModifiers(Modifier.PUBLIC)
                    .addStatement("this." + propertyInfo.getName() + " = " + propertyInfo.getName());

            if ( propertyInfo.isInternalType()) {
                setSpec.addParameter(ParameterSpec
                        .builder(ClassName.get("", Names.buildTypeName(propertyInfo.getName() + "_Type")), propertyInfo.getName()).build());
            } else {
                build.javaTypeName(propertyInfo.getType(), forParameter(setSpec, propertyInfo.getName()));
            }
            typeSpec.addMethod(getSpec.build());
            typeSpec.addMethod(setSpec.build());
        }

        // JavaFile.Builder file = JavaFile.builder(build.getDefaultPackage());
        container.into(typeSpec);

        //file.build().writeTo(new File(rootDirectory));
    }

    @Override
    public boolean declaresProperty(String name) {

        return true;
    }

    @Override
    public String getGeneratedJavaType() {
        return null;
    }
}
