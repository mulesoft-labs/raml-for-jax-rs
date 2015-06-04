package org.raml.schema.model;

import java.util.List;

public interface ISchemaType {
	
	String getName();
	
	boolean isSimple();
	
	boolean isComplex();
	
	List<ISchemaProperty> getProperties();
}
