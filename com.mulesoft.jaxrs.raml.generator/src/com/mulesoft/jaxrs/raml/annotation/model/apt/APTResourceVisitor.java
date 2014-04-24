
package com.mulesoft.jaxrs.raml.annotation.model.apt;

import java.io.File;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;

//import com.google.common.io.Files;
import com.mulesoft.jaxrs.raml.annotation.model.IResourceVisitorFactory;
import com.mulesoft.jaxrs.raml.annotation.model.ITypeModel;
import com.mulesoft.jaxrs.raml.annotation.model.ResourceVisitor;
//import com.sun.tools.jxc.SchemaGenerator;

public class APTResourceVisitor extends ResourceVisitor {

	private static final String DEFAULT_FILENAME = "schema1.xsd";
	private static final String SCHEMAS_FOLDER = "schemas";
	private final ProcessingEnvironment processingEnv;
	public APTResourceVisitor(IResourceVisitorFactory factory, ProcessingEnvironment processingEnv) {
		super(factory, null);
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
		} catch (ClassNotFoundException e1) {
			// Ignore; try some of further approaches
		}
		if (classLoader != null) {
			try {
				clazz = classLoader.loadClass(processingEnv.getElementUtils().getBinaryName(element).toString());
				generateXSDForClass(clazz);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			try {
				String classPath = System.getProperty("java.class.path"); //$NON-NLS-1$ 
				String outputPath = "";//processingEnv.getOptions().get(RAMLAnnotationProcessor.RAMLPATH_OPTION);
				File parentDir = new File(outputPath);
				if ((parentDir.exists() && parentDir.isFile()) || parentDir.getAbsolutePath().endsWith(".raml")) {
					parentDir = parentDir.getParentFile();
				} 
				parentDir = new File(parentDir, SCHEMAS_FOLDER);
				parentDir.mkdirs();
//				int code = SchemaGenerator.run(new String[]{"-d",parentDir.getAbsolutePath(),"-cp",classPath,processingEnv.getElementUtils().getBinaryName(element).toString()}); //$NON-NLS-1$ //$NON-NLS-2$
//				if (code == 0) {
//					File from = new File(parentDir, DEFAULT_FILENAME); //$NON-NLS-1$
//					File to = new File(parentDir, element.getSimpleName().toString().toLowerCase() + ".xsd"); //$NON-NLS-1$
//					Files.move(from,to);
//				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
