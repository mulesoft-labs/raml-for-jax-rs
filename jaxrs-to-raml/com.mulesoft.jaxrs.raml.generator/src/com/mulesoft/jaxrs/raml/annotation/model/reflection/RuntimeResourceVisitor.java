
package com.mulesoft.jaxrs.raml.annotation.model.reflection;

import java.io.File;

import com.mulesoft.jaxrs.raml.annotation.model.IRamlConfig;
import com.mulesoft.jaxrs.raml.annotation.model.ITypeModel;
import com.mulesoft.jaxrs.raml.annotation.model.ResourceVisitor;
import com.mulesoft.jaxrs.raml.jsonschema.JsonFormatter;
import com.mulesoft.jaxrs.raml.jsonschema.JsonUtil;
import com.mulesoft.jaxrs.raml.jsonschema.SchemaGenerator;

public class RuntimeResourceVisitor extends ResourceVisitor {


	public RuntimeResourceVisitor(File outputFile, ClassLoader classLoader) {
		super(outputFile, classLoader);
	}

	public RuntimeResourceVisitor(File outputFile, ClassLoader classLoader, IRamlConfig preferencesConfig) {
		super(outputFile, classLoader);
		setPreferences(preferencesConfig);
	}
	protected void afterSchemaGen(ITypeModel t) {
		String generateXMLExampleJAXB = generateXMLExampleJAXB(t);
		if (generateXMLExampleJAXB!=null){
				File file =outputFile;
				File parentDir = file.getParentFile();
				File examplesDir=new File(parentDir,"examples"); //$NON-NLS-1$
				File schemaFile=new File(parentDir,"schemas"); //$NON-NLS-1$
				if (!examplesDir.exists()){
					examplesDir.mkdir();
				}
				//String dummyXml = generator.generateDummyXmlFor(schemaFile.toURL().toExternalForm());
				writeString(generateXMLExampleJAXB, new File(examplesDir,t.getName()+".xml"));
				String jsonText = JsonUtil.convertToJSON(generateXMLExampleJAXB, true);
				jsonText = JsonFormatter.format(jsonText);
				writeString(jsonText, new File(examplesDir,t.getName()+".json"));
				String generatedSchema = jsonText != null ? new SchemaGenerator().generateSchema(jsonText) : null;
				generatedSchema = generatedSchema != null ? JsonFormatter.format(generatedSchema) : null;
				if(generatedSchema != null){
					String schemaName = t.getName().toLowerCase()+"-jsonschema";
					spec.getCoreRaml().addGlobalSchema(schemaName, generatedSchema, true, false);
					writeString(generatedSchema, new File(schemaFile,schemaName+".json"));
				}
		}
	}
	
	protected void generateXMLSchema(ITypeModel t) {
		if (t instanceof ReflectionType) {
			Class<?> element = ((ReflectionType) t).getElement();
			generateXSDForClass(element);
		}
		else if (t.getFullyQualifiedName() != null && classLoader != null) {
			try {
				Class<?> element = classLoader.loadClass(t.getFullyQualifiedName());
				generateXSDForClass(element);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		afterSchemaGen(t);
	}


	
	protected ResourceVisitor createResourceVisitor() {
		return new RuntimeResourceVisitor(outputFile, classLoader);
	}

}
