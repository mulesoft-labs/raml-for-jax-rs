package org.raml.jaxrs.generator.v10;

import com.squareup.javapoet.ClassName;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import org.jsonschema2pojo.DefaultGenerationConfig;
import org.jsonschema2pojo.GenerationConfig;
import org.jsonschema2pojo.Jackson2Annotator;
import org.jsonschema2pojo.SchemaGenerator;
import org.jsonschema2pojo.SchemaMapper;
import org.jsonschema2pojo.SchemaStore;
import org.jsonschema2pojo.rules.RuleFactory;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.GenerationException;
import org.raml.jaxrs.generator.GeneratorContext;
import org.raml.jaxrs.generator.JsonSchemaTypeGenerator;
import org.raml.jaxrs.generator.Names;
import org.raml.jaxrs.generator.XmlSchemaTypeGenerator;
import org.raml.jaxrs.generator.builders.JAXBHelper;
import org.raml.jaxrs.generator.builders.JavaPoetTypeGenerator;
import org.raml.jaxrs.generator.builders.TypeGenerator;
import org.raml.jaxrs.generator.builders.types.CompositeRamlTypeGenerator;
import org.raml.jaxrs.generator.builders.types.PropertyInfo;
import org.raml.jaxrs.generator.builders.types.RamlTypeGeneratorImplementation;
import org.raml.jaxrs.generator.builders.types.RamlTypeGeneratorInterface;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.datamodel.JSONTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.XMLTypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

import java.io.File;
import java.io.IOException;
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

    public TypeFactory(CurrentBuild currentBuild) {
        this.currentBuild = currentBuild;
    }

    public void createType(GeneratorContext context) {

        build(context, context.ramlTypeName(), context.javaTypeName(), true);
    }

    public void createType(Api api, String name, TypeDeclaration typeDeclaration) {

      //  build(api, name, Names.typeName(name), typeDeclaration, true);
    }

    private ClassName buildClassName(String pack, String name, boolean publicType) {

        if ( publicType ) {
            return ClassName.get(pack, name);
        } else {

            return ClassName.get("", name);
        }
    }

    private TypeGenerator build(GeneratorContext context,  String ramlType, String javaType, boolean publicType) {

        switch (context.constructionType()) {

            case PLAIN_OBJECT_TYPE:
                return createObjectType(context, ramlType, javaType,  publicType);

            case JSON_OBJECT_TYPE:
                return createJsonType(context, ramlType, javaType);

            case XML_OBJECT_TYPE:
                return createXmlType(context, ramlType, javaType);
        }

        throw new GenerationException("don't know what to do with type " + ramlType + " of type " + javaType);
    }

    private TypeGenerator createXmlType(GeneratorContext context, String ramlTypeName, String javaTypeName) {
        try {
            File schemaFile = JAXBHelper.saveSchema(context.schemaContent());
            final JCodeModel codeModel = new JCodeModel();

            Map<String, JClass> generated = JAXBHelper.generateClassesFromXmlSchemas(currentBuild.getModelPackage(), schemaFile, codeModel);
            XmlSchemaTypeGenerator gen = new XmlSchemaTypeGenerator(codeModel, currentBuild.getModelPackage(), javaTypeName, generated.values().iterator().next());
            currentBuild.newGenerator(ramlTypeName, gen);
            return gen;
        } catch (Exception e) {

            throw new GenerationException(e);
        }
    }

    private TypeGenerator createJsonType(GeneratorContext context, String ramlTypeName, String javaType) {
        //JSONTypeDeclaration decl = typeDeclaration;
        GenerationConfig config = new DefaultGenerationConfig() {
            @Override
            public boolean isGenerateBuilders() { // set config option by overriding method
                return true;
            }
        };

        final SchemaMapper mapper = new SchemaMapper(new RuleFactory(config, new Jackson2Annotator(), new SchemaStore()),
                new SchemaGenerator());
        final JCodeModel codeModel = new JCodeModel();

        try {
            mapper.generate(codeModel, javaType , currentBuild.getModelPackage(), context.schemaContent());
        } catch (IOException e) {
            throw new GenerationException(e);
        }

        JsonSchemaTypeGenerator gen = new JsonSchemaTypeGenerator(mapper, currentBuild.getModelPackage(), ramlTypeName, codeModel);
        currentBuild.newGenerator(ramlTypeName, gen);
        return gen;
    }

    private TypeGenerator createObjectType(GeneratorContext context, String ramlTypeName, String javaTypeName, boolean publicType) {
        V10GeneratorContext v10Context = (V10GeneratorContext) context;
        ObjectTypeDeclaration object = (ObjectTypeDeclaration) v10Context.getTypeDeclaration();
        Api api = v10Context.getApi();
        List<TypeDeclaration> parentTypes = object.parentTypes();

        Map<String, JavaPoetTypeGenerator> internalTypes = new HashMap<>();
        int internalTypeCounter = 0;
        List<PropertyInfo> properties = new ArrayList<>();
        for (TypeDeclaration declaration : object.properties()) {

            if (TypeUtils.isNewTypeDeclaration(api, declaration)) {
                String internalTypeName = Integer.toString(internalTypeCounter);
                V10GeneratorContext internal = new V10GeneratorContext(api, declaration);
                TypeGenerator internalGenerator = build(internal, internalTypeName, Names.typeName(declaration.name(), "Type"),  false);
                if ( internalGenerator instanceof JavaPoetTypeGenerator ) {
                    internalTypes.put(internalTypeName, (JavaPoetTypeGenerator) internalGenerator);
                    properties.add(new PropertyInfo(declaration.name(), internalTypeName, declaration));
                    internalTypeCounter ++;
                } else {
                    throw new GenerationException("internal type bad");
                }
            } else {
                properties.add(new PropertyInfo(declaration.name(), declaration));
            }

        }

        ClassName interf = buildClassName(currentBuild.getModelPackage(), javaTypeName, publicType);
        ClassName impl = buildClassName(currentBuild.getModelPackage(), javaTypeName + "Impl", publicType);

        RamlTypeGeneratorImplementation implg = new RamlTypeGeneratorImplementation(currentBuild, impl, interf, parentTypes, properties, internalTypes, object);
        RamlTypeGeneratorInterface intg = new RamlTypeGeneratorInterface(currentBuild, interf, parentTypes, properties, internalTypes, object);
        CompositeRamlTypeGenerator gen = new CompositeRamlTypeGenerator(intg, implg);

        if ( publicType ) {
            currentBuild.newGenerator(ramlTypeName, gen);
        }

        return gen;
    }

    /*
        Name of type is a mime type
     */
    public void createPrivateTypeForRequest(GeneratorContext context) {

        String ramlType = context.ramlTypeName();
        String javaType = context.javaTypeName();
        build(context, ramlType, javaType,  true);
    }

    /*
        Name of type is a mime type
     */
    public void createPrivateTypeForResponse(GeneratorContext context ) {

        String ramlType = context.ramlTypeName();
        String javaType = context.javaTypeName();
        build(context, ramlType, javaType, true);
    }

}
