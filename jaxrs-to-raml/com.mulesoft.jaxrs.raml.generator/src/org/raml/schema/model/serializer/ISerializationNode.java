package org.raml.schema.model.serializer;

import java.util.Set;

import org.raml.schema.model.ISchemaProperty;
import org.raml.schema.model.ISchemaType;

public interface ISerializationNode {
	
	void processProperty(ISchemaType type, ISchemaProperty prop, ISerializationNode childNode, Set<String> processedTypes);
	
	String getStringValue();

}
