package com.mulesoft.jaxrs.raml.jaxb;

import java.util.HashMap;

public interface IExampleWriter {

	void startEntity(String xmlName);
	void endEntity(String xmlName);
	
	void generateAttribute(String name,Class<?>type, boolean required);
	void generateElement(String name,Class<?>type, boolean required);
	void addValueSample(Class<?>type, boolean required);
	void startEntityAndDeclareNamespaces(String xmlName,
			HashMap<String, String> prefixes);
}
