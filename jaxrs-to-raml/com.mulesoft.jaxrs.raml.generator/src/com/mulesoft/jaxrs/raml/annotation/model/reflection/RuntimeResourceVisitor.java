
package com.mulesoft.jaxrs.raml.annotation.model.reflection;

import java.io.File;

import com.mulesoft.jaxrs.raml.annotation.model.ITypeModel;
import com.mulesoft.jaxrs.raml.annotation.model.ResourceVisitor;

public class RuntimeResourceVisitor extends ResourceVisitor {


	public RuntimeResourceVisitor(File outputFile, ClassLoader classLoader) {
		super(outputFile, classLoader);
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
	}


	
	protected ResourceVisitor createResourceVisitor() {
		return new RuntimeResourceVisitor(outputFile, classLoader);
	}

}
