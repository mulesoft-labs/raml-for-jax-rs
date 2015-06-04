package org.raml.schema.model.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.raml.schema.model.ISchemaProperty;
import org.raml.schema.model.ISchemaType;

public class TypeModelImpl implements ISchemaType{
	
	public TypeModelImpl(String name, Map<String,String> namespaces) {
		super();
		this.name = name;
		this.namespaces = namespaces;
	}
	
	public TypeModelImpl(String name, Map<String,String> namespaces, boolean isSimple) {
		super();
		this.name = name;
		this.isSimple = isSimple;
		this.namespaces = namespaces;
	}
	
	private String name;
	
	private Map<String,String> namespaces;
	
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

	public Map<String, String> getNamespaces() {
		return namespaces;
	}

	@Override
	public String getQualifiedPropertyName(ISchemaProperty prop) {
		
		String namespace = prop.getNamespace();
		if(namespace!=null&&this.namespaces!=null){
			String pref = this.namespaces.get(namespace);
			if(pref!=null){
				return pref + ":" + prop.getName();
			}
		}
		return prop.getName();
	}

}
