package org.raml.jaxrs.generator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import org.raml.jaxrs.generator.builders.AbstractTypeGenerator;
import org.raml.jaxrs.generator.builders.CodeContainer;
import org.raml.jaxrs.generator.builders.CodeModelTypeGenerator;

import java.io.IOException;

/**
 * Created by Jean-Philippe Belanger on 11/20/16.
 * Just potential zeroes and ones
 */
public class XmlSchemaTypeGenerator extends AbstractTypeGenerator<JCodeModel> implements CodeModelTypeGenerator {
    private final JCodeModel codeModel;
    private final String packageName;
    private final String className;
    private final JClass jclass;

    public XmlSchemaTypeGenerator(JCodeModel codeModel, String packageName, String className, JClass jclass) {
        this.codeModel = codeModel;
        this.packageName = packageName;
        this.className = className;
        this.jclass = jclass;
    }

    @Override
    public void output(CodeContainer<JCodeModel> container, TYPE type) throws IOException {

        container.into(codeModel);
    }

    @Override
    public TypeName getGeneratedJavaType() {

        return ClassName.get(packageName, jclass.name());
    }

    @Override
    public boolean declaresProperty(String name) {
        return false;
    }
}
