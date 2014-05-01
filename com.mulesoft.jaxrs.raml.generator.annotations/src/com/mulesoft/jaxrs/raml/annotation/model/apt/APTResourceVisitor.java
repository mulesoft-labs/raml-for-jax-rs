
package com.mulesoft.jaxrs.raml.annotation.model.apt;

import java.io.File;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import org.dynvocation.lib.xsd4j.XSDUtil;
import com.mulesoft.jaxrs.raml.annotation.model.ITypeModel;
import com.mulesoft.jaxrs.raml.annotation.model.ResourceVisitor;

public class APTResourceVisitor extends ResourceVisitor {

	private final ProcessingEnvironment processingEnv;
	public APTResourceVisitor(File outputFile, ProcessingEnvironment processingEnv, ClassLoader classLoader) {
		super(outputFile, classLoader);
		this.processingEnv = processingEnv;
	}
	
	
	protected void generateXMLSchema(ITypeModel t) {
		APTType type = (APTType) t;
		TypeElement element = (TypeElement) type.element();
		//try just loading this class
		Class<?> clazz;
		try {
			clazz = Class.forName(processingEnv.getElementUtils().getBinaryName(element).toString());
			generateXSDForClass(clazz);
			return;
		} catch (ClassNotFoundException e1) {
			// Ignore; try some of further approaches
		}
		if (classLoader != null) {
			try {
				clazz = classLoader.loadClass(processingEnv.getElementUtils().getBinaryName(element).toString());
				generateXSDForClass(clazz);
			} catch (ClassNotFoundException e) {
				//TODO log it
			}
		} 
	}

	@Override
	protected ResourceVisitor createResourceVisitor() {
		return new APTResourceVisitor(outputFile, processingEnv, classLoader);
	}
	
	protected void generateExamle(File schemaFile, String content) {

		File examplesDir = schemaFile.getParentFile();
		if (examplesDir.getName().endsWith(SCHEMAS_FOLDER)) { 
			examplesDir = new File(examplesDir.getParent(),EXAMPLES_FOLDER); 
			examplesDir.mkdirs();
		}
		String dummyXml = new XSDUtil().instantiateToString(schemaFile.getAbsolutePath(),null);
		doGenerateAndSave(schemaFile, examplesDir.getParentFile(), examplesDir, dummyXml);
	}
	
}
