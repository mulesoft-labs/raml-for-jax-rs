
package com.mulesoft.jaxrs.raml.annotation.model.apt;

import java.io.File;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;

import com.mulesoft.jaxrs.raml.annotation.model.ITypeModel;
import com.mulesoft.jaxrs.raml.annotation.model.ResourceVisitor;
import com.mulesoft.jaxrs.raml.annotation.model.StructureType;

/**
 * <p>APTResourceVisitor class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class APTResourceVisitor extends ResourceVisitor {

	private final ProcessingEnvironment processingEnv;
	/**
	 * <p>Constructor for APTResourceVisitor.</p>
	 *
	 * @param outputFile a {@link java.io.File} object.
	 * @param processingEnv a {@link javax.annotation.processing.ProcessingEnvironment} object.
	 * @param classLoader a {@link java.lang.ClassLoader} object.
	 */
	public APTResourceVisitor(File outputFile, ProcessingEnvironment processingEnv, ClassLoader classLoader) {
		super(outputFile, classLoader);
		this.processingEnv = processingEnv;
	}
	
	
	/** {@inheritDoc} */
	@Override
	protected boolean generateXMLSchema(ITypeModel t,StructureType st) {
		String name = t.getFullyQualifiedName();
		if(name.equals("void")||name.equals("java.lang.Void")){
			return false;
		}
		APTType type = (APTType) t;		
		TypeElement element = (TypeElement) type.element();
		//try just loading this class
		Class<?> clazz=null;
		try {
			clazz = Class.forName(processingEnv.getElementUtils().getBinaryName(element).toString());			
		} catch (ClassNotFoundException e1) {
			// Ignore; try some of further approaches
		}
		if (classLoader != null) {
			try {
				clazz = classLoader.loadClass(processingEnv.getElementUtils().getBinaryName(element).toString());
			} catch (ClassNotFoundException e) {
				//TODO log it
			}
		}
		if(clazz==null){
			return false;
		}
		if(st == null || st == StructureType.COMMON){
			generateXSDForClass(clazz);
		}
		afterSchemaGen(type, st);
		return true;
	}

	/** {@inheritDoc} */
	@Override
	protected ResourceVisitor createResourceVisitor() {
		return new APTResourceVisitor(outputFile, processingEnv, classLoader);
	}
	
}
