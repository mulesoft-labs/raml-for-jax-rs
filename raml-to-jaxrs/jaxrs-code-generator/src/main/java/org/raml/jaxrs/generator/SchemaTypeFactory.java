package org.raml.jaxrs.generator;

import com.squareup.javapoet.ClassName;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import org.raml.jaxrs.generator.builders.JAXBHelper;
import org.raml.jaxrs.generator.builders.JavaPoetTypeGenerator;
import org.raml.jaxrs.generator.builders.TypeGenerator;
import org.raml.jaxrs.generator.builders.types.CompositeRamlTypeGenerator;
import org.raml.jaxrs.generator.builders.types.PropertyInfo;
import org.raml.jaxrs.generator.builders.types.RamlTypeGeneratorImplementation;
import org.raml.jaxrs.generator.builders.types.RamlTypeGeneratorInterface;

import org.raml.jaxrs.generator.v10.V10GType;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
            XmlSchemaTypeGenerator gen = new XmlSchemaTypeGenerator(codeModel, currentBuild.getModelPackage(), type.defaultJavaTypeName(), generated.values().iterator().next());
            currentBuild.newGenerator(type.name(), gen);
            return gen;
        } catch (Exception e) {

            throw new GenerationException(e);
        }
    }

    public static TypeGenerator createJsonType(CurrentBuild currentBuild, GType type) {

        JsonSchemaTypeGenerator gen = new JsonSchemaTypeGenerator(currentBuild.getModelPackage(), type.defaultJavaTypeName(), type.schema());
        currentBuild.newGenerator(type.name(), gen);
        return gen;
    }


}
