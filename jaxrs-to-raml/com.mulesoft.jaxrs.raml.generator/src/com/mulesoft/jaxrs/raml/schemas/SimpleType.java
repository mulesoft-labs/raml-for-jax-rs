package com.mulesoft.jaxrs.raml.schemas;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.aml.typesystem.IAnnotationModel;

import com.mulesoft.jaxrs.raml.StructureType;

public enum SimpleType implements ISchemaType {
	
	INTEGER  ("Integer"),
	LONG     ("Long"),
	SHORT    ("Short"),
	BYTE     ("Byte"),
	DOUBLE   ("Double"),
	FLOAT    ("Float"),
	BOOLEAN  ("Boolean"),
	CHARACTER("Character"),
	STRING   ("String");

	private SimpleType(String name) {
		this.name = name;
	}
	
	final private String name;

	@Override
	public boolean isSimple() {
		return true;
	}

	@Override
	public boolean isComplex() {
		return false;
	}

	@Override
	public List<ISchemaProperty> getProperties() {
		return null;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public Map<String, String> getNamespaces() {
		return null;
	}

	@Override
	public String getQualifiedPropertyName(ISchemaProperty prop) {
		return prop.getName();
	}

	@Override
	public String getClassName() {
		return this.name;
	}

	@Override
	public String getClassQualifiedName() {
		return this.name;
	}

	@Override
	public StructureType getParentStructureType() {
		return StructureType.COMMON;
	}

	@Override
	public JAXBClassMapping getMapping() {
		return null;
	}

	@Override
	public List<IAnnotationModel> getAnnotations() {
		return new ArrayList<IAnnotationModel>();
	}
	
	@Override
	public IAnnotationModel getAnnotation(String name) {
		return null;
	}

	public void addProperty(ISchemaProperty property) {
	}
}
