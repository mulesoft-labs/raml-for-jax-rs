package com.mulesoft.jaxrs.raml.generator.popup.actions;

import java.io.File;
import java.net.MalformedURLException;

import com.mulesoft.jaxrs.raml.annotation.model.ITypeModel;
import com.mulesoft.jaxrs.raml.annotation.model.ResourceVisitor;
import com.mulesoft.jaxrs.raml.annotation.model.ResourceVisitor.CustomSchemaOutputResolver;
import com.mulesoft.jaxrs.raml.annotation.model.reflection.RuntimeResourceVisitor;

public class JDTResourceVisitor extends RuntimeResourceVisitor {

	
	public JDTResourceVisitor(File outputFile, ClassLoader classLoader) {
		super(outputFile, classLoader);
	}

	protected void generateExamle(File schemaFile, String content) {
		DummyXMLGenerator generator=new DummyXMLGenerator();
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
	}

	@Override
	protected void generateXMLSchema(ITypeModel t) {
		super.generateXMLSchema(t);
		String generateXMLExampleJAXB = generateXMLExampleJAXB(t);
		if (generateXMLExampleJAXB!=null){
			
				File file =outputFile;
				File parentDir = file.getParentFile();
				File examplesDir=new File(parentDir,"examples"); //$NON-NLS-1$
				if (!examplesDir.exists()){
					examplesDir.mkdir();
				}
				//String dummyXml = generator.generateDummyXmlFor(schemaFile.toURL().toExternalForm());
				writeString(generateXMLExampleJAXB, new File(examplesDir,t.getName()+"2.xml"));
			
		}
	}
	
	protected ResourceVisitor createResourceVisitor() {
		return new JDTResourceVisitor(outputFile, classLoader);
	}
}
