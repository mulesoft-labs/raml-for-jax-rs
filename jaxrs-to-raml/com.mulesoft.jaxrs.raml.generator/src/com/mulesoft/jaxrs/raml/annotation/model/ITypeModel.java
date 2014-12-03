package com.mulesoft.jaxrs.raml.annotation.model;

/**
 * 
 * Model of the type
 *
 */
public interface ITypeModel extends IBasicModel{

	
	/**
	 * 
	 * @return methods declared in this type
	 */
	IMethodModel[] getMethods();
	
	IFieldModel[] getFields();
	
	String getFullyQualifiedName();
}
