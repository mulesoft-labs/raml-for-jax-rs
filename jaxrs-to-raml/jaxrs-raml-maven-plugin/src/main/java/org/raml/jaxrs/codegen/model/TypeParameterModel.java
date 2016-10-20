package org.raml.jaxrs.codegen.model;

import org.aml.typesystem.ITypeParameter;

public class TypeParameterModel implements ITypeParameter{
	
	protected String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
