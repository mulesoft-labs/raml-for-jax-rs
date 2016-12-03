package org.raml.jaxrs.generator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import org.jsonschema2pojo.SchemaMapper;
import org.raml.jaxrs.generator.builders.CodeContainer;
import org.raml.jaxrs.generator.builders.CodeModelTypeGenerator;
import org.raml.jaxrs.generator.builders.TypeGenerator;

import java.io.File;
import java.io.IOException;

/**
 * Created by Jean-Philippe Belanger on 11/20/16.
 * Just potential zeroes and ones
 */
public class JsonSchemaTypeGenerator implements CodeModelTypeGenerator {
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
    public void output(CodeContainer<JCodeModel> container) throws IOException {

        container.into(codeModel);
    }

    @Override
    public TypeName getGeneratedJavaType() {
        JDefinedClass cls = codeModel._getClass(pack + "." + name);
        return ClassName.get(pack, cls.name());
    }

    @Override
    public boolean declaresProperty(String name) {
        return false;
    }
}
