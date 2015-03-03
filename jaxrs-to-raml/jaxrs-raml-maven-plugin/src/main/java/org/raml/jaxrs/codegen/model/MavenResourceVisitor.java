package org.raml.jaxrs.codegen.model;

import java.io.File;

import com.mulesoft.jaxrs.raml.annotation.model.IRamlConfig;
import com.mulesoft.jaxrs.raml.annotation.model.ITypeModel;
import com.mulesoft.jaxrs.raml.annotation.model.ResourceVisitor;
import com.mulesoft.jaxrs.raml.annotation.model.reflection.RuntimeResourceVisitor;

public class MavenResourceVisitor extends RuntimeResourceVisitor {

	
	private IRamlConfig config;

	public MavenResourceVisitor(File outputFile, ClassLoader classLoader,IRamlConfig config) {
		super(outputFile, classLoader,config);
		this.config=config;
	}

	@Override
	protected void generateXMLSchema(ITypeModel t) {
		super.generateXMLSchema(t);
	}

	protected ResourceVisitor createResourceVisitor() {
		return new MavenResourceVisitor(outputFile, classLoader,config);
	}
}