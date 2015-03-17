package com.mulesoft.jaxrs.raml.annotation.model;

/**
 *
 * model of method parameters
 *
 * @author kor
 * @version $Id: $Id
 */
public interface IParameterModel extends IBasicModel{

	/**
	 * <p>getType.</p>
	 *
	 * @return type name
	 */
	String getType();
	
	/**
	 * <p>required.</p>
	 *
	 * @return true if parameter is required
	 */
	boolean required();
	
	
}
