package org.raml.schema.model.impl;

import java.util.ArrayList;
import java.util.List;

import org.raml.schema.model.ISchemaProperty;
import org.raml.schema.model.ISchemaType;

public class TypeModelImpl implements ISchemaType{
	
	public TypeModelImpl(String name) {
		super();
		this.name = name;
	}
	
	public TypeModelImpl(String name, boolean isSimple) {
		super();
		this.name = name;
		this.isSimple = isSimple;
	}
	
	private String name;
	
	private boolean isSimple = false;

	private List<ISchemaProperty> properties;

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isSimple() {
		return this.isSimple;
	}

	@Override
	public boolean isComplex() {
		return !this.isSimple;
	}

	@Override
	public List<ISchemaProperty> getProperties() {
		return this.properties;
	}
	
	public void addProperty(ISchemaProperty property){
		if(this.properties==null){
			this.properties = new ArrayList<ISchemaProperty>();
		}
		this.properties.add(property);
	}

}
