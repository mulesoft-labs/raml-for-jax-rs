package org.raml.schema.model.impl;

import org.raml.schema.model.ISchemaProperty;
import org.raml.schema.model.ISchemaType;

import com.mulesoft.jaxrs.raml.annotation.model.StructureType;

public class PropertyModelImpl implements ISchemaProperty {
	
	public PropertyModelImpl(String name, ISchemaType type, boolean required, boolean isAttribute, StructureType structureType, String namespace) {
		super();
		this.name = name;
		this.type = type;
		this.required = required;
		this.isAttribute = isAttribute;
		this.structureType = structureType;
		this.namespace = namespace;
	}
	
	private String namespace;
	
	private ISchemaType type;

	private boolean required;
	
	private String name;
	
	private boolean isAttribute;
	
	private StructureType structureType;
	
	private boolean isGeneric;
	
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public ISchemaType getType() {
		return this.type;
	}

	@Override
	public boolean isRequired() {
		return this.required;
	}

	@Override
	public boolean isAttribute() {
		return this.isAttribute;
	}
	
	public StructureType getStructureType() {
		return structureType;
	}

	public String getNamespace() {
		return namespace;
	}

	public boolean isGeneric() {
		return isGeneric;
	}

	public void setGeneric(boolean isGeneric) {
		this.isGeneric = isGeneric;
	}
}
