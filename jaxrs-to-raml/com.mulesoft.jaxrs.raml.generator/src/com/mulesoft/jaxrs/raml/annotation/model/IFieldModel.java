package com.mulesoft.jaxrs.raml.annotation.model;

public interface IFieldModel extends IBasicModel,IMember{

	ITypeModel getType();

	boolean isStatic();
	
	boolean isPublic();
}
