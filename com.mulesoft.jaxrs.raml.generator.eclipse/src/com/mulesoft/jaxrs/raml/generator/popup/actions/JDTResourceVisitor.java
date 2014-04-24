package com.mulesoft.jaxrs.raml.generator.popup.actions;

import java.io.File;
import java.io.FileOutputStream;
import java.net.MalformedURLException;

import com.mulesoft.jaxrs.raml.annotation.model.IResourceVisitorFactory;
import com.mulesoft.jaxrs.raml.annotation.model.reflection.RuntimeResourceVisitor;
import com.mulesoft.jaxrs.raml.jsonschema.SchemaGenerator;

public class JDTResourceVisitor extends RuntimeResourceVisitor {

	public JDTResourceVisitor(IResourceVisitorFactory factory, File outputFile) {
		super(factory, outputFile);
	}

	protected void generateExamle(File file, String content) {
		DummyXMLGenerator m=new DummyXMLGenerator();
		try {
			String generateDummyXmlFor = m.generateDummyXmlFor(file.toURL().toExternalForm());
			String generateSchema = new SchemaGenerator().generateSchema(generateDummyXmlFor);
			System.out.println(generateSchema);
			File parentFile = file.getParentFile().getParentFile();
			File examples=new File(parentFile,"examples");
			if (!examples.exists()){
				examples.mkdir();
			}
			String name = file.getName();
			name=name.substring(0,name.lastIndexOf('.'));
			File toSave=new File(examples,name+".xml");
			try {
				FileOutputStream fileOutputStream = new FileOutputStream(toSave);
				fileOutputStream.write(generateDummyXmlFor.getBytes("UTF-8"));
				fileOutputStream.close();
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		} catch (MalformedURLException e) {
			throw new IllegalStateException(e);			
		}
	}
}
