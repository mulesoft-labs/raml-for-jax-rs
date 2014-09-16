package com.mulesoft.jaxrs.raml.annotation.model;

public interface IDocInfo {

	/**
	 * 
	 * @return documentation content
	 */
	String getDocumentation();

	/**
	 * 
	 * @param pName parameter name
	 * @return documentation for parameter
	 */
	String getDocumentation(String pName);
	
	/**
	 * 
	 * @return information about return values
	 */
	String getReturnInfo();
}
