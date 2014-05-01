package com.mulesoft.jaxrs.raml.generator.popup.actions;

import java.io.File;
import java.io.FileOutputStream;
import java.net.MalformedURLException;

import com.mulesoft.jaxrs.raml.annotation.model.ResourceVisitor;
import com.mulesoft.jaxrs.raml.annotation.model.reflection.RuntimeResourceVisitor;
import com.mulesoft.jaxrs.raml.jsonschema.JsonFormatter;
import com.mulesoft.jaxrs.raml.jsonschema.JsonUtil;
import com.mulesoft.jaxrs.raml.jsonschema.SchemaGenerator;

public class JDTResourceVisitor extends RuntimeResourceVisitor {

	
	public JDTResourceVisitor(File outputFile, ClassLoader classLoader) {
		super(outputFile, classLoader);
	}

	protected void generateExamle(File file, String content) {
		DummyXMLGenerator m=new DummyXMLGenerator();
		try {
			String generateDummyXmlFor = m.generateDummyXmlFor(file.toURL().toExternalForm());
			String convertToJSON = JsonUtil.convertToJSON(generateDummyXmlFor, true);
			convertToJSON=JsonFormatter.format(convertToJSON);
			String generateSchema2 = new SchemaGenerator().generateSchema(convertToJSON);
			generateSchema2=JsonFormatter.format(generateSchema2);
			String fName = file.getName().replace(".xsd", "-jsonshema");
			spec.getCoreRaml().addGlobalSchema(fName,generateSchema2, true,false);
			File parentFile = file.getParentFile().getParentFile();
			File examples=new File(parentFile,"examples");
			if (!examples.exists()){
				examples.mkdir();
			}
			String name = file.getName();
			name=name.substring(0,name.lastIndexOf('.'));
			File toSave=new File(examples,name+".xml");
			writeString(generateDummyXmlFor, toSave);
			toSave=new File(examples,name+".json");
			writeString(convertToJSON, toSave);
			File shemas=new File(parentFile,"schemas");
			toSave=new File(shemas,fName+".json");
			writeString(generateSchema2, toSave);
		} catch (MalformedURLException e) {
			throw new IllegalStateException(e);			
		}
	}

	private void writeString(String generateDummyXmlFor, File toSave) {
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(toSave);
			fileOutputStream.write(generateDummyXmlFor.getBytes("UTF-8"));
			fileOutputStream.close();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}
	
	@Override
	protected ResourceVisitor createResourceVisitor() {
		return new JDTResourceVisitor(outputFile, classLoader);
	}
}
