package org.raml.schema.model;

public interface ISchemaProperty {
	
	String getName();
	
	ISchemaType getType();
	
	boolean isAttribute();
	
	boolean isRequired();
	
	boolean isCollection();
	
	String getNamespace();
}
