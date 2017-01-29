package org.raml.jaxrs.generator.v10.types;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.GenerationException;
import org.raml.jaxrs.generator.GeneratorType;
import org.raml.jaxrs.generator.Names;
import org.raml.jaxrs.generator.SchemaTypeFactory;
import org.raml.jaxrs.generator.builders.JavaPoetTypeGenerator;
import org.raml.jaxrs.generator.builders.BuildPhase;
import org.raml.jaxrs.generator.builders.TypeGenerator;
import org.raml.jaxrs.generator.extension.types.TypeContext;
import org.raml.jaxrs.generator.extension.types.TypeExtension;
import org.raml.jaxrs.generator.ramltypes.GProperty;
import org.raml.jaxrs.generator.ramltypes.GType;
import org.raml.jaxrs.generator.v10.Annotations;
import org.raml.jaxrs.generator.v10.PropertyInfo;
import org.raml.jaxrs.generator.v10.V10GType;
import org.raml.jaxrs.generator.v10.V10TypeRegistry;
import org.raml.v2.api.model.v10.common.Annotable;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jean-Philippe Belanger on 1/29/17.
 * Just potential zeroes and ones
 */
class SimpleInheritanceExtension implements TypeExtension {
    private final V10GType originalType;
    private final V10TypeRegistry registry;
    private final CurrentBuild currentBuild;

    public SimpleInheritanceExtension(V10GType originalType, V10TypeRegistry registry, CurrentBuild currentBuild) {
        this.originalType = originalType;
        this.registry = registry;
        this.currentBuild = currentBuild;
    }

    @Override
    public TypeSpec.Builder onType(TypeContext context, TypeSpec.Builder builder, V10GType objectType,
            BuildPhase buildPhase) {

        if ( buildPhase == BuildPhase.INTERFACE) {
            return buildType(context, objectType);
        } else {
            return buildTypeImplementation(context, objectType);
        }
    }

    private TypeSpec.Builder buildTypeImplementation(TypeContext context, V10GType objectType) {

        ObjectTypeDeclaration object = (ObjectTypeDeclaration) objectType.implementation();

        ClassName className = originalType.javaImplementationName(currentBuild.getModelPackage());

        final TypeSpec.Builder typeSpec = TypeSpec
                .classBuilder(className)
                .addModifiers(Modifier.PUBLIC);

        currentBuild.withTypeListeners().onTypeImplementation(currentBuild, typeSpec, object);
        ClassName parentClassName = (ClassName) originalType.defaultJavaTypeName(currentBuild.getModelPackage());

        if ( parentClassName != null ) {
            typeSpec.addSuperinterface(parentClassName);
        }


/*
        for (TypeGenerator internalType : internalTypes.values()) {

            internalType.output(new CodeContainer<TypeSpec.Builder>() {
                @Override
                public void into(TypeSpec.Builder g) throws IOException {

                    g.addModifiers(Modifier.STATIC);
                    typeSpec.addType(g.build());
                }
            }, buildPhase);
        }

*/

        V10TypeRegistry localRegistry = registry.createRegistry();
        List<PropertyInfo> properties = new ArrayList<>();
        int internalTypeCounter = 0;
        for (GProperty declaration : originalType.properties()) {

            if (declaration.isInline()) {
                String internalTypeName = Integer.toString(internalTypeCounter);

                V10GType type = localRegistry.createInlineType(internalTypeName, Annotations.CLASS_NAME.get(
                        Names.typeName(declaration.name(), "Type"), (Annotable) declaration.implementation()),
                        (TypeDeclaration) declaration.implementation()
                );
                TypeGenerator internalGenerator = inlineTypeBuild(localRegistry, currentBuild,
                        GeneratorType.generatorFrom(type));
                if (internalGenerator instanceof JavaPoetTypeGenerator) {
                    properties.add(new PropertyInfo(localRegistry, declaration.overrideType(type)));
                    internalTypeCounter++;
                } else {
                    throw new GenerationException("internal type bad");
                }
            } else {
                properties.add(new PropertyInfo(localRegistry, declaration));
            }
        }

        for (PropertyInfo propertyInfo : properties) {

            FieldSpec.Builder fieldSpec = FieldSpec.builder(propertyInfo.resolve(currentBuild), Names.variableName(propertyInfo.getName())).addModifiers(Modifier.PRIVATE);
            currentBuild.withTypeListeners().onFieldImplementation(currentBuild,
                    fieldSpec, (TypeDeclaration) propertyInfo.getType().implementation());
            if ( propertyInfo.getName().equals(object.discriminator()) ) {
                fieldSpec.initializer("$S", object.discriminatorValue());
            }
            typeSpec.addField(fieldSpec.build());

            final MethodSpec.Builder getSpec = MethodSpec
                    .methodBuilder("get" + Names.typeName(propertyInfo.getName()))
                    .addModifiers(Modifier.PUBLIC)
                    .addStatement("return this." + Names.variableName(propertyInfo.getName()));

            getSpec.returns(propertyInfo.resolve(currentBuild));
            currentBuild.withTypeListeners().onGetterMethodImplementation(currentBuild,
                    getSpec, (TypeDeclaration) propertyInfo.getType().implementation());
            typeSpec.addMethod(getSpec.build());

            if ( ! propertyInfo.getName().equals(object.discriminator()) ) {

                MethodSpec.Builder setSpec = MethodSpec
                        .methodBuilder("set" + Names.typeName(propertyInfo.getName()))
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement("this." + Names.variableName(propertyInfo.getName()) + " = " + Names
                                .variableName(propertyInfo.getName()));

                ParameterSpec.Builder parameterSpec = ParameterSpec
                        .builder(propertyInfo.resolve(currentBuild), Names.variableName(propertyInfo.getName()));
                currentBuild.withTypeListeners().onSetterMethodImplementation(currentBuild, setSpec,
                        parameterSpec, (TypeDeclaration) propertyInfo.getType().implementation());

                setSpec.addParameter(parameterSpec.build());
                typeSpec.addMethod(setSpec.build());
            }
        }

        return typeSpec;
    }

    private TypeSpec.Builder buildType(TypeContext context, V10GType objectType) {
        ObjectTypeDeclaration object = (ObjectTypeDeclaration) objectType.implementation();

        List<V10GType> parentTypes = originalType.parentTypes();
        Map<String, JavaPoetTypeGenerator> internalTypes = new HashMap<>();
        int internalTypeCounter = 0;

        // this should be in the generator;
        List<PropertyInfo> properties = new ArrayList<>();
        V10TypeRegistry localRegistry = registry.createRegistry();
        for (GProperty declaration : originalType.properties()) {

            if (declaration.isInline()) {
                String internalTypeName = Integer.toString(internalTypeCounter);

                V10GType type = localRegistry.createInlineType(internalTypeName, Annotations.CLASS_NAME.get(
                        Names.typeName(declaration.name(), "Type"), (Annotable) declaration.implementation()),
                        (TypeDeclaration) declaration.implementation()
                );

                TypeGenerator internalGenerator = inlineTypeBuild(localRegistry, currentBuild,
                        GeneratorType.generatorFrom(type));
                if (internalGenerator instanceof JavaPoetTypeGenerator) {
                    properties.add(new PropertyInfo(localRegistry, declaration.overrideType(type)));
                    context.createInternalClass((JavaPoetTypeGenerator) internalGenerator);
                    internalTypeCounter++;
                } else {
                    throw new GenerationException("internal type bad");
                }
            } else {
                properties.add(new PropertyInfo(localRegistry, declaration));
            }

        }

        ClassName interf = (ClassName) originalType.defaultJavaTypeName(currentBuild.getModelPackage());

        final TypeSpec.Builder typeSpec = TypeSpec
                .interfaceBuilder(interf)
                .addModifiers(Modifier.PUBLIC);

        for (GType parentType : parentTypes) {

            if (parentType.name().equals("object")) {

                continue;
            }

            typeSpec.addSuperinterface(parentType.defaultJavaTypeName(currentBuild.getModelPackage()));
        }

        for (PropertyInfo propertyInfo : properties) {

            final MethodSpec.Builder getSpec = MethodSpec
                    .methodBuilder(Names.methodName("get", propertyInfo.getName()))
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
            getSpec.returns(propertyInfo.resolve(currentBuild));
            currentBuild.withTypeListeners().onGetterMethodDeclaration(currentBuild,
                    getSpec, (TypeDeclaration) propertyInfo.getType().implementation());
            typeSpec.addMethod(getSpec.build());

            if (!propertyInfo.getName().equals(object.discriminator())) {
                MethodSpec.Builder setSpec = MethodSpec
                        .methodBuilder(Names.methodName("set", propertyInfo.getName()))
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
                ParameterSpec.Builder parameterSpec = ParameterSpec
                        .builder(propertyInfo.resolve(currentBuild), Names.variableName(propertyInfo.getName()));
                currentBuild.withTypeListeners().onSetterMethodDeclaration(currentBuild, setSpec,
                        parameterSpec, (TypeDeclaration) propertyInfo.getType().implementation());
                setSpec.addParameter(
                        parameterSpec.build());
                typeSpec.addMethod(setSpec.build());
            }
        }

        context.addImplementation();
        return typeSpec;
    }

    private static TypeGenerator inlineTypeBuild(V10TypeRegistry registry, CurrentBuild currentBuild, GeneratorType type) {

        switch (type.getObjectType()) {

            case ENUMERATION_TYPE:
                return V10TypeFactory.createEnumerationType(currentBuild, type.getDeclaredType());

            case PLAIN_OBJECT_TYPE:
                return V10TypeFactory.createObjectType(registry, currentBuild, (V10GType) type.getDeclaredType(),  false);

            case JSON_OBJECT_TYPE:
                return SchemaTypeFactory.createJsonType(currentBuild, type.getDeclaredType());

            case XML_OBJECT_TYPE:
                return SchemaTypeFactory.createXmlType(currentBuild, type.getDeclaredType());
        }

        throw new GenerationException("don't know what to do with type " + type.getDeclaredType());
    }


}
