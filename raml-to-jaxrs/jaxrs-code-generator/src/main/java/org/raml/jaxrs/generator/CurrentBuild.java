package org.raml.jaxrs.generator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import org.jsonschema2pojo.DefaultGenerationConfig;
import org.jsonschema2pojo.GenerationConfig;
import org.jsonschema2pojo.Jackson2Annotator;
import org.jsonschema2pojo.SchemaGenerator;
import org.jsonschema2pojo.SchemaMapper;
import org.jsonschema2pojo.SchemaStore;
import org.jsonschema2pojo.rules.RuleFactory;
import org.raml.jaxrs.generator.builders.CodeContainer;
import org.raml.jaxrs.generator.builders.CodeModelTypeGenerator;
import org.raml.jaxrs.generator.builders.JAXBHelper;
import org.raml.jaxrs.generator.builders.JavaPoetTypeGenerator;
import org.raml.jaxrs.generator.builders.TypeGenerator;
import org.raml.jaxrs.generator.builders.extensions.JaxbTypeExtensionImpl;
import org.raml.jaxrs.generator.builders.extensions.TypeExtension;
import org.raml.jaxrs.generator.builders.extensions.TypeExtensionList;
import org.raml.jaxrs.generator.builders.resources.ResourceGenerator;
import org.raml.jaxrs.generator.builders.resources.ResourceInterface;
import org.raml.jaxrs.generator.builders.TypeDescriber;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.raml.jaxrs.generator.Paths.relativize;

/**
 * Created by Jean-Philippe Belanger on 10/26/16.
 * The art of building stuff is here.
 * Factory for building root stuff.
 */
public class CurrentBuild {

    private final String defaultPackage;

    private final List<ResourceGenerator> resources = new ArrayList<>();
    private final Map<String, TypeGenerator> types = new HashMap<>();
    private final Map<String, CodeModelTypeGenerator> codeModelTypes = new HashMap<>();
    private final Map<String, JavaPoetTypeGenerator> javaPoetTypes = new HashMap<>();
    private TypeExtensionList typeExtensionList = new TypeExtensionList();
    private String modelPackage;

    public CurrentBuild(String defaultPackage) {

        this.defaultPackage = defaultPackage;
        typeExtensionList.addExtension(new JaxbTypeExtensionImpl());
    }

    public String getDefaultPackage() {
        return defaultPackage;
    }

    public String getModelPackage() {

        return defaultPackage;
    }

    public ResourceGenerator createResource(String name, String relativeURI) {

        ResourceGenerator builder = new ResourceInterface(this, name, relativize(relativeURI));
        resources.add(builder);
        return builder;
    }

    public void generate(final String rootDirectory) throws IOException {


        if ( resources.size() > 0 ) {
            ResponseSupport.buildSupportClasses(rootDirectory, this.defaultPackage);
        }

        for (TypeGenerator typeGenerator : types.values()) {

            if ( typeGenerator instanceof JavaPoetTypeGenerator ) {


                JavaPoetTypeGenerator b = (JavaPoetTypeGenerator) typeGenerator;
                b.output(new CodeContainer<TypeSpec.Builder>() {
                    @Override
                    public void into(TypeSpec.Builder g) throws IOException {

                        JavaFile.Builder file = JavaFile.builder(getDefaultPackage(), g.build());
                        file.build().writeTo(new File(rootDirectory));
                    }
                }
                );

                continue;
            }

            if ( typeGenerator instanceof  CodeModelTypeGenerator ) {
                CodeModelTypeGenerator b = (CodeModelTypeGenerator) typeGenerator;
                b.output(new CodeContainer<JCodeModel>() {
                    @Override
                    public void into(JCodeModel g) throws IOException {

                        g.build(new File(rootDirectory));
                    }
                });
            }
        }

        for (ResourceGenerator resource : resources) {
            resource.output(new CodeContainer<TypeSpec>() {
                @Override
                public void into(TypeSpec g) throws IOException {
                    JavaFile.Builder file = JavaFile.builder(getDefaultPackage(), g);
                    file.build().writeTo(new File(rootDirectory));
                }
            });
        }

    }

/*
    public RamlTypeGenerator createPrivateType(ObjectTypeDeclaration objectTypeDeclaration, String name, List<String> parentTypes) {

        RamlTypeGeneratorInterface intf = new RamlTypeGeneratorInterface(objectTypeDeclaration, this, name, parentTypes, false);
        RamlTypeGeneratorImplementation impl = new RamlTypeGeneratorImplementation(objectTypeDeclaration, this, name, name, false);

        CompositeRamlTypeGenerator compositeTypeBuilder = new CompositeRamlTypeGenerator(intf, impl);
        types.put(name, compositeTypeBuilder);
        javaPoetTypes.put(name, compositeTypeBuilder);
        return compositeTypeBuilder;
    }

    public RamlTypeGenerator createType(ObjectTypeDeclaration objectTypeDeclaration, String name, List<String> parentTypes, boolean isInternal) {

        RamlTypeGeneratorInterface intf = new RamlTypeGeneratorInterface(objectTypeDeclaration, this, name, parentTypes, isInternal);
        RamlTypeGeneratorImplementation impl = new RamlTypeGeneratorImplementation(objectTypeDeclaration, this, name, name, isInternal);

        CompositeRamlTypeGenerator compositeTypeBuilder = new CompositeRamlTypeGenerator(intf, impl);
        if ( ! isInternal) {
            types.put(name, compositeTypeBuilder);
            javaPoetTypes.put(name, compositeTypeBuilder);
        }
        return compositeTypeBuilder;
    }
*/


    public void javaTypeName(String type, TypeDescriber describer) {
        Class<?> scalar = ScalarTypes.scalarToJavaType(type);
        if ( scalar != null ){

            describer.asJavaType(this, scalar);
        } else {

            TypeGenerator builder = types.get(type);
            if ( builder != null ) {

                describer.asBuiltType(this, builder.getGeneratedJavaType());
            } else {

                throw new IllegalArgumentException("unknown type " + type);
            }
        }


    }

    public void createTypeFromJsonSchema(final String name, final String jsonSchema) {


        try {
            GenerationConfig config = new DefaultGenerationConfig() {
                @Override
                public boolean isGenerateBuilders() { // set config option by overriding method
                    return true;
                }
            };

            final SchemaMapper mapper = new SchemaMapper(new RuleFactory(config, new Jackson2Annotator(), new SchemaStore()),
                    new SchemaGenerator());
            final JCodeModel codeModel = new JCodeModel();

            mapper.generate(codeModel, name, defaultPackage, jsonSchema);

            types.put(name, new JsonSchemaTypeGenerator(mapper, defaultPackage, name, codeModel));
            codeModelTypes.put(name, new JsonSchemaTypeGenerator(mapper, defaultPackage, name, codeModel));
        } catch (IOException e) {
            throw new GenerationException(e);
        }
    }

    public void createTypeFromXmlSchema(final String name, String schema) {

        try {
            File schemaFile = JAXBHelper.saveSchema(schema);
            final JCodeModel codeModel = new JCodeModel();

            Map<String, JClass> generated = JAXBHelper.generateClassesFromXmlSchemas(defaultPackage, schemaFile, codeModel);

            types.put(name, new XmlSchemaTypeGenerator(codeModel, defaultPackage, name, generated.values().iterator().next()));
            codeModelTypes.put(name, new XmlSchemaTypeGenerator(codeModel, defaultPackage, name,
                    generated.values().iterator().next()));
        } catch (Exception e) {

            throw new GenerationException(e);
        }
    }

    public TypeExtension withTypeListeners() {

        return typeExtensionList;
    }


    public void newKnownType(String ramlTypeName, TypeGenerator generator) {

        types.put(ramlTypeName, generator);
    }

    public TypeGenerator getDeclaredType(String ramlType) {

        return types.get(ramlType);
    }

    public TypeName getJavaType(String type, Map<String, JavaPoetTypeGenerator> internalTypes) {

        Class<?> scalar = ScalarTypes.scalarToJavaType(type);
        if ( scalar != null ){

            if ( scalar.isPrimitive()) {
                switch(scalar.getSimpleName()) {
                    case "int":
                        return TypeName.INT;

                    case "boolean":
                        return TypeName.BOOLEAN;

                    default:
                        throw new GenerationException("JP, finish the list " + scalar);
                }
            } else {
                return ClassName.get(scalar);
            }
        } else {

            TypeGenerator builder = internalTypes.get(type);
            if ( builder == null ) {
                builder = types.get(type);
            }

            if ( builder != null ) {

                return builder.getGeneratedJavaType();
            } else {

                throw new IllegalArgumentException("unknown type " + type);
            }
        }
    }
}

