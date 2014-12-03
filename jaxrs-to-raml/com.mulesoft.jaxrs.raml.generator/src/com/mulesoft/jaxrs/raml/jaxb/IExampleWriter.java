package com.mulesoft.jaxrs.raml.jaxb;

public interface IExampleWriter {

	void startEntity(String xmlName);
	void endEntity(String xmlName);
	
	void generateAttribute(String name,Class<?>type);
	void generateElement(String name,Class<?>type);
	void addValueSample(Class<?>type);
}
