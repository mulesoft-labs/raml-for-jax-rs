package com.mulesoft.jaxrs.raml.schemas;

import java.util.List;
import java.util.Map;

import org.aml.typesystem.IAnnotationModel;

import com.mulesoft.jaxrs.raml.StructureType;

public interface ISchemaType {
	
	Map<String,String> getNamespaces();
	
	String getName();
	
	boolean isSimple();
	
	boolean isComplex();
	
	List<ISchemaProperty> getProperties();
	
	String getQualifiedPropertyName(ISchemaProperty prop);
	
	String getClassName();
	
	String getClassQualifiedName();
	
	StructureType getParentStructureType();
	
	JAXBClassMapping getMapping();
	
	List<IAnnotationModel> getAnnotations();
	
	IAnnotationModel getAnnotation(String name);

	void addProperty(ISchemaProperty property);
}
