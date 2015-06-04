package org.raml.schema.model.serializer;

import org.raml.schema.model.ISchemaType;

public interface IModelSerializer {
	
	String serialize(ISchemaType type);

}
