/*
 * Copyright 2014, Genuitec, LLC
 * All Rights Reserved.
 */
package com.mulesoft.jaxrs.raml.annotation.model.reflection;

import com.mulesoft.jaxrs.raml.annotation.model.IResourceVisitorFactory;
import com.mulesoft.jaxrs.raml.annotation.model.ResourceVisitor;

public class RuntimeResourceVisitorFactory implements IResourceVisitorFactory {

	@Override
	public ResourceVisitor createResourceVisitor() {
		return new ResourceVisitor(this);
	}

}
