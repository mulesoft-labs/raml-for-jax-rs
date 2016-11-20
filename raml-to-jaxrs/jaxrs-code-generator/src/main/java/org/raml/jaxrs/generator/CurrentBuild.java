package org.raml.jaxrs.generator;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import org.jsonschema2pojo.DefaultGenerationConfig;
import org.jsonschema2pojo.GenerationConfig;
import org.jsonschema2pojo.Jackson2Annotator;
import org.jsonschema2pojo.SchemaGenerator;
import org.jsonschema2pojo.SchemaMapper;
import org.jsonschema2pojo.SchemaStore;
import org.jsonschema2pojo.rules.RuleFactory;
import org.raml.jaxrs.generator.builders.Generator;
import org.raml.jaxrs.generator.builders.JAXBHelper;
import org.raml.jaxrs.generator.builders.TypeGenerator;
import org.raml.jaxrs.generator.builders.resources.ResourceGenerator;
import org.raml.jaxrs.generator.builders.resources.ResourceInterface;
import org.raml.jaxrs.generator.builders.types.CompositeRamlTypeGenerator;
import org.raml.jaxrs.generator.builders.types.RamlTypeGenerator;
import org.raml.jaxrs.generator.builders.types.RamlTypeGeneratorImplementation;
import org.raml.jaxrs.generator.builders.types.RamlTypeGeneratorInterface;
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

    private final List<ResourceGenerator> resources = new ArrayList<ResourceGenerator>();
    private final Map<String, TypeGenerator> types = new HashMap<>();

    public CurrentBuild(String defaultPackage) {

        this.defaultPackage = defaultPackage;
    }

    public String getDefaultPackage() {
        return defaultPackage;
    }

    public ResourceGenerator createResource(String name, String relativeURI) {

        ResourceGenerator builder = new ResourceInterface(this, name, relativize(relativeURI));
        resources.add(builder);
        return builder;
    }

    public void generate(String rootDirectory) throws IOException {

        ResponseSupport.buildSupportClasses(rootDirectory, this.defaultPackage);
        for (ResourceGenerator resource : resources) {
            resource.output(rootDirectory);
        }

        for (TypeGenerator b: types.values()) {
            b.output(rootDirectory);
        }
    }

    public RamlTypeGenerator createType(String name, List<String> parentTypes) {

        RamlTypeGeneratorInterface intf = new RamlTypeGeneratorInterface(this, name, parentTypes);
        RamlTypeGeneratorImplementation impl = new RamlTypeGeneratorImplementation(this, name, name);

        CompositeRamlTypeGenerator compositeTypeBuilder = new CompositeRamlTypeGenerator(intf, impl);
        types.put(name, compositeTypeBuilder);
        return compositeTypeBuilder;
    }

    public TypeGenerator getDeclaredType(String parentType) {

        return types.get(parentType);
    }

    public void javaTypeName(String type, TypeDescriber describer) {
        Class<?> scalar = ScalarTypes.scalarToJavaType(type);
        if ( scalar != null ){

            describer.asJavaType(this, scalar);
        } else {

            TypeGenerator builder = types.get(type);
            if ( builder != null ) {

                describer.asBuiltType(this, type);
            } else {

                throw new IllegalArgumentException("unknown type " + type);
            }
        }


    }

    public void createTypeFromJsonSchema(final String name, final String jsonSchema) {


        GenerationConfig config = new DefaultGenerationConfig() {
            @Override
            public boolean isGenerateBuilders() { // set config option by overriding method
                return true;
            }
        };

        final SchemaMapper mapper = new SchemaMapper(new RuleFactory(config, new Jackson2Annotator(), new SchemaStore()), new SchemaGenerator());
        types.put(name, new JsonSchemaTypeGenerator(mapper, defaultPackage, name, jsonSchema));
    }

    public void createTypeFromXmlSchema(final String name, String schema) {

        try {
            File schemaFile = JAXBHelper.saveSchema(schema);
            final JCodeModel codeModel = new JCodeModel();
            Map<String, JClass> generated = JAXBHelper.generateClassesFromXmlSchemas(defaultPackage, schemaFile, codeModel);

            types.put(name, new TypeGenerator() {
                @Override
                public void output(String rootDirectory) throws IOException {

                    codeModel.build(new File(rootDirectory));
                }

                @Override
                public String getGeneratedJavaType() {
                    return null;
                }

                @Override
                public boolean declaresProperty(String name) {
                    return false;
                }
            });

        } catch (Exception e) {

        }
    }

    private static class JsonSchemaTypeGenerator implements TypeGenerator {
        private final SchemaMapper mapper;
        private final String pack;
        private final String name;
        private final String jsonSchema;
        private final JCodeModel codeModel;

        public JsonSchemaTypeGenerator(SchemaMapper mapper, String pack, String name, String jsonSchema) {
            this.mapper = mapper;
            this.pack = pack;
            this.name = name;
            this.jsonSchema = jsonSchema;
            codeModel = new JCodeModel();

        }

        @Override
        public void output(String rootDirectory) throws IOException {

            mapper.generate(codeModel, name, pack, jsonSchema);
            codeModel.build(new File(rootDirectory));
        }

        @Override
        public String getGeneratedJavaType() {
            JDefinedClass cls = codeModel._getClass(pack + "." + name);
            return cls.fullName();
        }

        @Override
        public boolean declaresProperty(String name) {
            return false;
        }
    }
}
