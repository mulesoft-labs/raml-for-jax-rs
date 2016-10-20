package com.mulesoft.jaxrs.raml.schemas;

public interface IMapSchemaProperty extends ISchemaProperty {
	
	ISchemaType getKeyType();
	
	ISchemaType getValueType();

}
