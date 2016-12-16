package org.raml.jaxrs.generator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import org.jsonschema2pojo.DefaultGenerationConfig;
import org.jsonschema2pojo.GenerationConfig;
import org.jsonschema2pojo.Jackson2Annotator;
import org.jsonschema2pojo.SchemaGenerator;
import org.jsonschema2pojo.SchemaMapper;
import org.jsonschema2pojo.SchemaStore;
import org.jsonschema2pojo.rules.RuleFactory;
import org.raml.jaxrs.generator.builders.AbstractTypeGenerator;
import org.raml.jaxrs.generator.builders.CodeContainer;
import org.raml.jaxrs.generator.builders.CodeModelTypeGenerator;
import org.raml.jaxrs.generator.builders.Generator;
import org.raml.jaxrs.generator.builders.TypeGenerator;

import java.io.File;
import java.io.IOException;

/**
 * Created by Jean-Philippe Belanger on 11/20/16.
 * Just potential zeroes and ones
 */
public class JsonSchemaTypeGenerator extends AbstractTypeGenerator<JCodeModel> implements CodeModelTypeGenerator {
    private final String pack;
    private final String name;
    private final String schema;

    public JsonSchemaTypeGenerator(String pack, String name, String schema) {
        this.pack = pack;
        this.name = name;
        this.schema = schema;
    }

    @Override
    public void output(CodeContainer<JCodeModel> container, TYPE type) throws IOException {

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
            mapper.generate(codeModel, name , pack, schema);
        } catch (IOException e) {
            throw new GenerationException(e);
        }

        container.into(codeModel);
    }

    @Override
    public TypeName getGeneratedJavaType() {

        return ClassName.get(pack, name);
    }
}
