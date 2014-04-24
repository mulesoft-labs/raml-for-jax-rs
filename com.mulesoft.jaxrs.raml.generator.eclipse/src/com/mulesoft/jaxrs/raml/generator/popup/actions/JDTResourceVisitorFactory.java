package com.mulesoft.jaxrs.raml.generator.popup.actions;

import java.io.File;
import java.net.URLClassLoader;

import com.mulesoft.jaxrs.raml.annotation.model.IResourceVisitorFactory;
import com.mulesoft.jaxrs.raml.annotation.model.ResourceVisitor;

public class JDTResourceVisitorFactory implements IResourceVisitorFactory{

	private File file;
	private URLClassLoader loader;

	public JDTResourceVisitorFactory(File outputFile, URLClassLoader classLoader) {
		this.file=outputFile;
		this.loader=classLoader;
	}
	
	

	
	public ResourceVisitor createResourceVisitor() {
		JDTResourceVisitor jdtResourceVisitor = new JDTResourceVisitor(this,file);
		jdtResourceVisitor.setClassLoader(loader);
		return jdtResourceVisitor;
	}

}
