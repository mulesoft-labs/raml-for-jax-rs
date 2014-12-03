package com.mulesoft.jaxrs.raml.annotation.model;

/**
 * 
 * Model for a rest method
 *
 */
public interface IMethodModel extends IBasicModel,IMember {
	
	/**
	 * 
	 * @return information about parameters
	 */
	public abstract IParameterModel[] getParameters();
	
	/**
	 * 
	 * @return documentation on the method
	 */
	IDocInfo getBasicDocInfo();
	
	/**
	 * 
	 * @return information about return type
	 */
	ITypeModel getReturnedType();
	
	/**
	 * 
	 * @return information about body type
	 */
	ITypeModel getBodyType();

	public abstract boolean isStatic();
	public abstract boolean isPublic();	
}
