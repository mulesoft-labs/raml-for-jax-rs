package org.raml.schema.model.impl;

import org.raml.schema.model.ISchemaProperty;
import org.raml.schema.model.ISchemaType;

public class PropertyModelImpl implements ISchemaProperty {
	
	public PropertyModelImpl(String name, ISchemaType type, boolean required, boolean isAttribute, boolean isCollection) {
		super();
		this.name = name;
		this.type = type;
		this.required = required;
		this.isAttribute = isAttribute;
		this.isCollection = isCollection;
	}
	
	private ISchemaType type;

	private boolean required;
	
	private String name;
	
	private boolean isAttribute;
	
	private boolean isCollection;
	
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

	@Override
	public boolean isCollection() {
		return this.isCollection;
	}
}
