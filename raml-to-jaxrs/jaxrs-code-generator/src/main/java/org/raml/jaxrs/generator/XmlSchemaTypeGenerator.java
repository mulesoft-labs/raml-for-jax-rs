package org.raml.jaxrs.generator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import org.raml.jaxrs.generator.builders.CodeContainer;
import org.raml.jaxrs.generator.builders.CodeModelTypeGenerator;
import org.raml.jaxrs.generator.builders.TypeGenerator;

import java.io.File;
import java.io.IOException;

/**
 * Created by Jean-Philippe Belanger on 11/20/16.
 * Just potential zeroes and ones
 */
public class XmlSchemaTypeGenerator implements CodeModelTypeGenerator {
    private final JCodeModel codeModel;
    private final String packageName;
    private final String className;

    public XmlSchemaTypeGenerator(JCodeModel codeModel, String packageName, String className) {
        this.codeModel = codeModel;
        this.packageName = packageName;
        this.className = className;
    }

    @Override
    public void output(CodeContainer<JCodeModel> container) throws IOException {

        container.into(codeModel);
    }

    @Override
    public TypeName getGeneratedJavaType() {

        JDefinedClass cls = codeModel._getClass(packageName + "." + className);
        return ClassName.get(packageName, className);
    }

    @Override
    public boolean declaresProperty(String name) {
        return false;
    }
}
