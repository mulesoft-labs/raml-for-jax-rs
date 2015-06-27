package com.mulesoft.jaxrs.raml.annotation.model.jdt;

import com.mulesoft.jaxrs.raml.annotation.model.ITypeParameter;

public class JDTTypeParameter implements ITypeParameter {

	public JDTTypeParameter(org.eclipse.jdt.core.ITypeParameter element) {
		super();
		this.element = element;
	}
	
	protected org.eclipse.jdt.core.ITypeParameter element;

	@Override
	public String getName() {
		return element.getElementName();
	}

}
