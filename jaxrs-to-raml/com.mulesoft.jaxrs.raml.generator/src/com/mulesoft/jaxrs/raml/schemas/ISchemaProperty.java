package com.mulesoft.jaxrs.raml.schemas;

import java.util.List;

import org.aml.typesystem.IAnnotationModel;

import com.mulesoft.jaxrs.raml.StructureType;

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
