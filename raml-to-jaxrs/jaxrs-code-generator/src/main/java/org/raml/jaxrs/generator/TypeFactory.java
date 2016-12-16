package org.raml.jaxrs.generator;

import com.squareup.javapoet.ClassName;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import org.raml.jaxrs.generator.builders.JAXBHelper;
import org.raml.jaxrs.generator.builders.JavaPoetTypeGenerator;
import org.raml.jaxrs.generator.builders.TypeGenerator;
import org.raml.jaxrs.generator.builders.types.CompositeRamlTypeGenerator;
import org.raml.jaxrs.generator.builders.types.PropertyInfo;
import org.raml.jaxrs.generator.builders.types.RamlTypeGeneratorImplementation;
import org.raml.jaxrs.generator.builders.types.RamlTypeGeneratorInterface;

import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jean-Philippe Belanger on 12/2/16.
 * Just potential zeroes and ones
 */
public class TypeFactory {

    private CurrentBuild currentBuild;
    private final GAbstractionFactory factory;

    public TypeFactory(CurrentBuild currentBuild, GAbstractionFactory factory) {
        this.currentBuild = currentBuild;
        this.factory = factory;
    }

    public void createType(GeneratorType type) {

        build(type, true);
    }

    private ClassName buildClassName(String pack, String name, boolean publicType) {

        if ( publicType ) {
            return ClassName.get(pack, name);
        } else {

            return ClassName.get("", name);
        }
    }

    private TypeGenerator build(GeneratorType type, boolean publicType) {

        switch (type.getObjectType()) {

            case PLAIN_OBJECT_TYPE:
                return createObjectType(type.getDeclaredType(),  publicType);

            case JSON_OBJECT_TYPE:
                return createJsonType(type.getDeclaredType());

            case XML_OBJECT_TYPE:
                return createXmlType(type.getDeclaredType());
        }

        throw new GenerationException("don't know what to do with type " + type.getDeclaredType());
    }

    private TypeGenerator createXmlType(GType type) {
        try {
            File schemaFile = JAXBHelper.saveSchema(type.schema());
            final JCodeModel codeModel = new JCodeModel();

            Map<String, JClass> generated = JAXBHelper.generateClassesFromXmlSchemas(currentBuild.getModelPackage(), schemaFile, codeModel);
            XmlSchemaTypeGenerator gen = new XmlSchemaTypeGenerator(codeModel, currentBuild.getModelPackage(), type.defaultJavaTypeName(), generated.values().iterator().next());
            currentBuild.newGenerator(type.name(), gen);
            return gen;
        } catch (Exception e) {

            throw new GenerationException(e);
        }
    }

    private TypeGenerator createJsonType(GType type) {

        JsonSchemaTypeGenerator gen = new JsonSchemaTypeGenerator(currentBuild.getModelPackage(), type.defaultJavaTypeName(), type.schema());
        currentBuild.newGenerator(type.name(), gen);
        return gen;
    }

    private TypeGenerator createObjectType(GType originalType, boolean publicType) {

        ObjectTypeDeclaration object = (ObjectTypeDeclaration) originalType.implementation();
        List<TypeDeclaration> parentTypes = object.parentTypes();

        Map<String, JavaPoetTypeGenerator> internalTypes = new HashMap<>();
        int internalTypeCounter = 0;
        List<PropertyInfo> properties = new ArrayList<>();
        for (GProperty declaration : originalType.properties()) {

            if (declaration.isInternal()) {
                String internalTypeName = Integer.toString(internalTypeCounter);

                GType type = factory.newType(internalTypeName, Names.typeName(declaration.name(), "Type"),
                        (TypeDeclaration) declaration.implementation());
                TypeGenerator internalGenerator = build(GeneratorType.generatorFrom(type),  false);
                if ( internalGenerator instanceof JavaPoetTypeGenerator ) {
                    internalTypes.put(internalTypeName, (JavaPoetTypeGenerator) internalGenerator);
                    properties.add(new PropertyInfo(declaration.overrideType(type)));
                    internalTypeCounter ++;
                } else {
                    throw new GenerationException("internal type bad");
                }
            } else {
                properties.add(new PropertyInfo(declaration));
            }

        }

        ClassName interf = buildClassName(currentBuild.getModelPackage(), originalType.defaultJavaTypeName(), publicType);
        ClassName impl = buildClassName(currentBuild.getModelPackage(), originalType.defaultJavaTypeName() + "Impl", publicType);

        RamlTypeGeneratorImplementation implg = new RamlTypeGeneratorImplementation(currentBuild, impl, interf, parentTypes, properties, internalTypes, object);
        RamlTypeGeneratorInterface intg = new RamlTypeGeneratorInterface(currentBuild, interf, parentTypes, properties, internalTypes, object);
        CompositeRamlTypeGenerator gen = new CompositeRamlTypeGenerator(intg, implg);

        if ( publicType ) {
            currentBuild.newGenerator(originalType.name(), gen);
        }

        return gen;
    }

}
