package org.raml.jaxrs.generator;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import org.jsonschema2pojo.SchemaMapper;
import org.raml.jaxrs.generator.builders.TypeGenerator;

import java.io.File;
import java.io.IOException;

/**
 * Created by Jean-Philippe Belanger on 11/20/16.
 * Just potential zeroes and ones
 */
class JsonSchemaTypeGenerator implements TypeGenerator {
    private final SchemaMapper mapper;
    private final String pack;
    private final String name;
    private final JCodeModel codeModel;

    public JsonSchemaTypeGenerator(SchemaMapper mapper, String pack, String name, JCodeModel codeModel) {
        this.mapper = mapper;
        this.pack = pack;
        this.name = name;
        this.codeModel = codeModel;
    }

    @Override
    public void output(String rootDirectory) throws IOException {

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
