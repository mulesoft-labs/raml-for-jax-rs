package com.mulesoft.jaxrs.raml.annotation.model.reflection;

import java.lang.reflect.TypeVariable;

import com.mulesoft.jaxrs.raml.annotation.model.ITypeParameter;

public class ReflectionTypeParameter implements ITypeParameter {
	
	public ReflectionTypeParameter(TypeVariable<?> element) {
		super();
		this.element = element;
	}

	protected TypeVariable<?> element;
	
	@Override
	public String getName() {
		return element.getName();
	}

}
