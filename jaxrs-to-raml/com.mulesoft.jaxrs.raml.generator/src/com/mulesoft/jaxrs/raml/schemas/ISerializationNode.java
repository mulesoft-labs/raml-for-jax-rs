package com.mulesoft.jaxrs.raml.schemas;

import java.util.Set;

public interface ISerializationNode {
	
	void processProperty(ISchemaType type, ISchemaProperty prop, ISerializationNode childNode, Set<String> processedTypes);
	
	String getStringValue();

}
