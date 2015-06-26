package com.mulesoft.jaxrs.raml.generator.popup.actions;

import java.io.File;
import java.net.MalformedURLException;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.mulesoft.jaxrs.raml.annotation.model.ITypeModel;
import com.mulesoft.jaxrs.raml.annotation.model.ResourceVisitor;
import com.mulesoft.jaxrs.raml.annotation.model.reflection.RuntimeResourceVisitor;
import com.mulesoft.jaxrs.raml.annotation.model.StructureType;
import com.mulesoft.jaxrs.raml.jsonschema.JsonFormatter;
import com.mulesoft.jaxrs.raml.jsonschema.JsonUtil;
import com.mulesoft.jaxrs.raml.jsonschema.JsonUtils;

public class JDTResourceVisitor extends RuntimeResourceVisitor {

	
	public JDTResourceVisitor(File outputFile, ClassLoader classLoader) {
		super(outputFile, classLoader);
	}

	protected void generateExamle(File schemaFile, String content) {
		/*DummyXMLGenerator generator=new DummyXMLGenerator();
		try {
			File parentDir = schemaFile.getParentFile().getParentFile();
			File examplesDir=new File(parentDir,"examples"); //$NON-NLS-1$
			if (!examplesDir.exists()){
				examplesDir.mkdir();
			}
			String dummyXml = generator.generateDummyXmlFor(schemaFile.toURL().toExternalForm());
			doGenerateAndSave(schemaFile, parentDir, examplesDir, dummyXml);
		} catch (MalformedURLException e) {
			throw new IllegalStateException(e);			
		}
		*/
	}
	

	@Override
	protected boolean generateXMLSchema(ITypeModel t, StructureType st) {
		boolean result = super.generateXMLSchema(t,st);
//		String generateXMLExampleJAXB = generateXMLExampleJAXB(t);
//		if (generateXMLExampleJAXB!=null){
//			
//				File file =outputFile;
//				File parentDir = file.getParentFile();
//				File examplesDir=new File(parentDir,"examples"); //$NON-NLS-1$
//				if (!examplesDir.exists()){
//					examplesDir.mkdir();
//				}
//				writeString(generateXMLExampleJAXB, new File(examplesDir,t.getName()+".xml"));
//				String jsonText = getProperJSONExampleFromXML(generateXMLExampleJAXB,t);
//				writeString(jsonText, new File(examplesDir,t.getName()+".json"));
//				
//		}
		return result;
	}
	
	protected ResourceVisitor createResourceVisitor() {
		return new JDTResourceVisitor(outputFile, classLoader);
	}
}
