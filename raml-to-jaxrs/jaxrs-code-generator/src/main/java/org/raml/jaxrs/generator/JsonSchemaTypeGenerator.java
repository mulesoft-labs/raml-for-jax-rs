package org.raml.jaxrs.generator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import com.sun.codemodel.JCodeModel;
import org.jsonschema2pojo.GenerationConfig;
import org.jsonschema2pojo.Jackson2Annotator;
import org.jsonschema2pojo.SchemaGenerator;
import org.jsonschema2pojo.SchemaMapper;
import org.jsonschema2pojo.SchemaStore;
import org.jsonschema2pojo.rules.RuleFactory;
import org.raml.jaxrs.generator.builders.AbstractTypeGenerator;
import org.raml.jaxrs.generator.builders.CodeContainer;
import org.raml.jaxrs.generator.builders.CodeModelTypeGenerator;
import org.raml.jaxrs.generator.builders.BuildPhase;

import java.io.IOException;

/**
 * Created by Jean-Philippe Belanger on 11/20/16.
 * Just potential zeroes and ones
 */
public class JsonSchemaTypeGenerator extends AbstractTypeGenerator<JCodeModel> implements CodeModelTypeGenerator {
    private final CurrentBuild build;
    private final String pack;
    private final ClassName name;
    private final String schema;

    public JsonSchemaTypeGenerator(CurrentBuild build, String pack, ClassName name, String schema) {
        this.build = build;
        this.pack = pack;
        this.name = name;
        this.schema = schema;
    }

    @Override
    public void output(CodeContainer<JCodeModel> container, BuildPhase buildPhase) throws IOException {

        GenerationConfig config = build.getJsonMapperConfig();
        final SchemaMapper mapper = new SchemaMapper(new RuleFactory(config, new Jackson2Annotator(), new SchemaStore()),
                new SchemaGenerator());
        final JCodeModel codeModel = new JCodeModel();

        try {
            mapper.generate(codeModel, name.simpleName() , pack, schema);
        } catch (IOException e) {
            throw new GenerationException(e);
        }

        container.into(codeModel);
    }

    @Override
    public TypeName getGeneratedJavaType() {

        // duplicated logic with json2pojo.  Should look in model.
        return ClassName.get(name.packageName(), build.getJsonMapperConfig().getClassNamePrefix() + name.simpleName() + build.getJsonMapperConfig().getClassNameSuffix());
    }
}
