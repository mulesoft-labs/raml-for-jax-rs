package com.mulesoft.jaxrs.raml.annotation.model.apt;

import javax.lang.model.element.TypeParameterElement;

import com.mulesoft.jaxrs.raml.annotation.model.ITypeParameter;

public class APTTypeParameter implements ITypeParameter {
	
	public APTTypeParameter(TypeParameterElement element) {
		super();
		this.element = element;
	}

	protected TypeParameterElement element;

	@Override
	public String getName() {
		return this.element.getSimpleName().toString();
	}

}
