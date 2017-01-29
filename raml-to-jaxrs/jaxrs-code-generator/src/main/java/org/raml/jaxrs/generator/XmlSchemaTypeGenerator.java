package org.raml.jaxrs.generator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import org.raml.jaxrs.generator.builders.AbstractTypeGenerator;
import org.raml.jaxrs.generator.builders.CodeContainer;
import org.raml.jaxrs.generator.builders.CodeModelTypeGenerator;
import org.raml.jaxrs.generator.builders.BuildPhase;

import java.io.IOException;

/**
 * Created by Jean-Philippe Belanger on 11/20/16.
 * Just potential zeroes and ones
 */
public class XmlSchemaTypeGenerator extends AbstractTypeGenerator<JCodeModel> implements CodeModelTypeGenerator {
    private final JCodeModel codeModel;
    private final String packageName;
    private final JClass jclass;

    public XmlSchemaTypeGenerator(JCodeModel codeModel, String packageName, JClass jclass) {
        this.codeModel = codeModel;
        this.packageName = packageName;
        this.jclass = jclass;
    }

    @Override
    public void output(CodeContainer<JCodeModel> container, BuildPhase buildPhase) throws IOException {

        container.into(codeModel);
    }

    @Override
    public TypeName getGeneratedJavaType() {

        return ClassName.get(packageName, jclass.name());
    }
}
