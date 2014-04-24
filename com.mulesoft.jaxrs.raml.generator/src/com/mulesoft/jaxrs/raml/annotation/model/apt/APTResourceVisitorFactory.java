
package com.mulesoft.jaxrs.raml.annotation.model.apt;

import javax.annotation.processing.ProcessingEnvironment;

import com.mulesoft.jaxrs.raml.annotation.model.IResourceVisitorFactory;
import com.mulesoft.jaxrs.raml.annotation.model.ResourceVisitor;

public class APTResourceVisitorFactory implements IResourceVisitorFactory {

	private final ProcessingEnvironment processingEnv;

	public APTResourceVisitorFactory(ProcessingEnvironment processingEnv) {
		this.processingEnv = processingEnv;
	}

	@Override
	public ResourceVisitor createResourceVisitor() {
		return new APTResourceVisitor(this, processingEnv);
	}

}
