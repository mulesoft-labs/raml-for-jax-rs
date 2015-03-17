package com.mulesoft.jaxrs.raml.annotation.model;

/**
 * <p>IDocInfo interface.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public interface IDocInfo {

	/**
	 * <p>getDocumentation.</p>
	 *
	 * @return documentation content
	 */
	String getDocumentation();

	/**
	 * <p>getDocumentation.</p>
	 *
	 * @param pName parameter name
	 * @return documentation for parameter
	 */
	String getDocumentation(String pName);
	
	/**
	 * <p>getReturnInfo.</p>
	 *
	 * @return information about return values
	 */
	String getReturnInfo();
}
