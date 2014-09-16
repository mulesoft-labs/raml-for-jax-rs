package com.mulesoft.jaxrs.raml.jsonschema;

import java.util.Collection;

public interface ISchemaNode {
	
	Collection<? extends ISchemaNode> getChildren();

}
