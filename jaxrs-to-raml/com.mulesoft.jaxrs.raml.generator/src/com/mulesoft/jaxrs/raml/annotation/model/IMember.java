package com.mulesoft.jaxrs.raml.annotation.model;

public interface IMember extends IBasicModel{

	public abstract boolean isStatic();
	public abstract boolean isPublic();
	
	ITypeModel getType();
	
	ITypeModel getJAXBType();
}
