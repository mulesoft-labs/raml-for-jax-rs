package org.raml.schema.model;

public interface IMapSchemaProperty extends ISchemaProperty {
	
	ISchemaType getKeyType();
	
	ISchemaType getValueType();

}
