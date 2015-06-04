package org.raml.schema.model.serializer;

import org.raml.schema.model.ISchemaProperty;

public interface ISerializationNode {
	
	void processProperty(ISchemaProperty prop, ISerializationNode childNode);
	
	String getStringValue();

}
