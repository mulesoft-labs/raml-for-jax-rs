package org.raml.jaxrs.generator;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import org.raml.jaxrs.generator.builders.JAXBHelper;
import org.raml.jaxrs.generator.builders.TypeGenerator;

import java.io.File;
import java.util.Map;

/**
 * Created by Jean-Philippe Belanger on 12/2/16.
 * Just potential zeroes and ones
 */
public class SchemaTypeFactory {

    public static TypeGenerator createXmlType(CurrentBuild currentBuild, GType type) {
        try {
            File schemaFile = JAXBHelper.saveSchema(type.schema());
            final JCodeModel codeModel = new JCodeModel();

            Map<String, JClass> generated = JAXBHelper.generateClassesFromXmlSchemas(currentBuild.getModelPackage(), schemaFile, codeModel);
            XmlSchemaTypeGenerator gen = new XmlSchemaTypeGenerator(codeModel, currentBuild.getModelPackage(), type.defaultJavaTypeName(currentBuild.getModelPackage()), generated.values().iterator().next());
            currentBuild.newGenerator(type.name(), gen);
            return gen;
        } catch (Exception e) {

            throw new GenerationException(e);
        }
    }

    public static TypeGenerator createJsonType(CurrentBuild currentBuild, GType type) {

        JsonSchemaTypeGenerator gen = new JsonSchemaTypeGenerator(currentBuild.getModelPackage(), type.defaultJavaTypeName(currentBuild.getModelPackage()), type.schema());
        currentBuild.newGenerator(type.name(), gen);
        return gen;
    }


}
