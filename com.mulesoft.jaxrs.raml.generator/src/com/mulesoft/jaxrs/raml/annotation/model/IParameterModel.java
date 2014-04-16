package com.mulesoft.jaxrs.raml.annotation.model;

/**
 * 
 * model of method parameters
 *
 */
public interface IParameterModel extends IBasicModel{

	/**
	 * 
	 * @return type name
	 */
	String getType();
	
	/**
	 * 
	 * @return true if parameter is required
	 */
	boolean required();
	
	
}
