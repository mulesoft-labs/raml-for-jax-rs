package com.mulesoft.jaxrs.raml.annotation.model.reflection;

import java.io.File;

import com.mulesoft.jaxrs.raml.annotation.model.IResourceVisitorFactory;
import com.mulesoft.jaxrs.raml.annotation.model.ResourceVisitor;

public class RuntimeResourceVisitorFactory implements IResourceVisitorFactory {

	private final File outputFile;
	private final ClassLoader classLoader;

	public RuntimeResourceVisitorFactory(File outputFile, ClassLoader classLoader) {
		this.outputFile = outputFile;
		this.classLoader = classLoader;
	}

	@Override
	public ResourceVisitor createResourceVisitor() {
		RuntimeResourceVisitor visitor = new RuntimeResourceVisitor(this, outputFile);
		visitor.setClassLoader(classLoader);
		return visitor;
	}

}
