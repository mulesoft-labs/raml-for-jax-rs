package org.raml.schema.model;

import java.util.List;

import com.mulesoft.jaxrs.raml.annotation.model.IAnnotationModel;
import com.mulesoft.jaxrs.raml.annotation.model.StructureType;

public interface ISchemaProperty {
	
	String getName();
	
	ISchemaType getType();
	
	boolean isAttribute();
	
	boolean isRequired();
	
	StructureType getStructureType();
	
	String getNamespace();
	
	boolean isGeneric();
	
	List<IAnnotationModel> getAnnotations();
	
	String getDefaultValue();
}
