package com.mulesoft.jaxrs.raml.jaxb;

import java.util.HashMap;

/**
 * <p>IExampleWriter interface.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public interface IExampleWriter {

	/**
	 * <p>startEntity.</p>
	 *
	 * @param xmlName a {@link java.lang.String} object.
	 */
	void startEntity(String xmlName);
	/**
	 * <p>endEntity.</p>
	 *
	 * @param xmlName a {@link java.lang.String} object.
	 */
	void endEntity(String xmlName);
	
	/**
	 * <p>generateAttribute.</p>
	 *
	 * @param name a {@link java.lang.String} object.
	 * @param type a {@link java.lang.Class} object.
	 * @param required a boolean.
	 */
	void generateAttribute(String name,Class<?>type, boolean required);
	/**
	 * <p>generateElement.</p>
	 *
	 * @param name a {@link java.lang.String} object.
	 * @param type a {@link java.lang.Class} object.
	 * @param required a boolean.
	 */
	void generateElement(String name,Class<?>type, boolean required);
	/**
	 * <p>addValueSample.</p>
	 *
	 * @param type a {@link java.lang.Class} object.
	 * @param required a boolean.
	 */
	void addValueSample(Class<?>type, boolean required);
	/**
	 * <p>startEntityAndDeclareNamespaces.</p>
	 *
	 * @param xmlName a {@link java.lang.String} object.
	 * @param prefixes a {@link java.util.HashMap} object.
	 */
	void startEntityAndDeclareNamespaces(String xmlName,
			HashMap<String, String> prefixes);
}
