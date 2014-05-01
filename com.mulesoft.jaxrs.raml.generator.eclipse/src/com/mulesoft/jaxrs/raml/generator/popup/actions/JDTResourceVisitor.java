package com.mulesoft.jaxrs.raml.generator.popup.actions;

import java.io.File;
import java.net.MalformedURLException;

import com.mulesoft.jaxrs.raml.annotation.model.ResourceVisitor;
import com.mulesoft.jaxrs.raml.annotation.model.reflection.RuntimeResourceVisitor;

public class JDTResourceVisitor extends RuntimeResourceVisitor {

	
	public JDTResourceVisitor(File outputFile, ClassLoader classLoader) {
		super(outputFile, classLoader);
	}

	protected void generateExamle(File schemaFile, String content) {
		DummyXMLGenerator m=new DummyXMLGenerator();
		try {
			File parentDir = schemaFile.getParentFile().getParentFile();
			File examplesDir=new File(parentDir,"examples");
			if (!examplesDir.exists()){
				examplesDir.mkdir();
			}
			String dummyXml = m.generateDummyXmlFor(schemaFile.toURL().toExternalForm());
			doGenerateAndSave(schemaFile, parentDir, examplesDir, dummyXml);
		} catch (MalformedURLException e) {
			throw new IllegalStateException(e);			
		}
	}

	@Override
	protected ResourceVisitor createResourceVisitor() {
		return new JDTResourceVisitor(outputFile, classLoader);
	}
}
