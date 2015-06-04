package com.mulesoft.jaxrs.raml.jaxb;

import java.util.HashMap;
import java.util.HashSet;

import org.raml.schema.model.ISchemaType;
import org.raml.schema.model.SimpleType;
import org.raml.schema.model.impl.PropertyModelImpl;
import org.raml.schema.model.impl.TypeModelImpl;

public class SchemaModelBuilder {
	
	public SchemaModelBuilder() {
		super();
	}
	
	private HashMap<String,TypeModelImpl> javaTypeMap = new HashMap<String, TypeModelImpl>();
	
	private HashMap<String,TypeModelImpl> jaxbTypeMap = new HashMap<String, TypeModelImpl>();

	public ISchemaType buildSchemaModel(JAXBType jaxbType){
		TypeModelImpl typeModel = generateType(jaxbType);
		return typeModel;
	}

	HashSet<JAXBType>onStack=new HashSet<JAXBType>();
	
	private TypeModelImpl generateType(JAXBType jaxbType) {
		
		String xmlName = jaxbType.getXMLName();
		TypeModelImpl existing = this.jaxbTypeMap.get(xmlName);
		if(existing!=null){
			return existing;
		}
		HashMap<String,String>namespaces = jaxbType.gatherNamespaces();
		TypeModelImpl typeModel = new TypeModelImpl(xmlName,namespaces);
		this.jaxbTypeMap.put(xmlName, typeModel);
		HashMap<String,String>prefixes=jaxbType.gatherNamespaces();
	
		for (JAXBProperty p:jaxbType.properties){
			writeProperty(typeModel,p,prefixes);
		}
		return typeModel;
	}

	private void writeProperty(TypeModelImpl typeModel,JAXBProperty p, HashMap<String, String> prefixes) {
		String name=p.name();
		if (name==null||name.length()==0){
			return;
		}
		PropertyModelImpl prop = null;
		String namespace = p.namespace;
		if (p instanceof JAXBAttributeProperty){			
			prop = new PropertyModelImpl(name, getType(p.asJavaType()), p.required, true, p.isCollection(),namespace);
		}
		else if (p instanceof JAXBValueProperty){
			prop = new PropertyModelImpl(name, getType(p.asJavaType()), p.required, false, p.isCollection(),namespace);
		}
		else if (p instanceof JAXBElementProperty){
			JAXBElementProperty el=(JAXBElementProperty) p;
			JAXBType jaxbType = el.getJAXBType();
			if (jaxbType!=null){
				TypeModelImpl propertyType = generateType(jaxbType);
				prop = new PropertyModelImpl(name, propertyType, p.required, false, p.isCollection(),namespace);
			}
			else{
				prop = new PropertyModelImpl(name, getType(p.asJavaType()), p.required, false, p.isCollection(),namespace);
			}
		}		
		if(prop!=null){
			typeModel.addProperty(prop);
		}
	}

	private ISchemaType getType(Class<?> clazz) {
		
		String qName = clazz.getCanonicalName();
		String name = "";
		if(clazz.isPrimitive()){
			name = clazz.getName().toUpperCase();
			if(name.equals("CHAR")){
				name = "CHARACTER";				
			}			
		}
		if(qName.startsWith("java.lang.")){
			name = qName.substring("java.lang.".length()).toUpperCase();
		}
		try{
			ISchemaType type = SimpleType.valueOf(name);
			return type;
		}
		catch(Exception e){}
		
		name = qName;
		TypeModelImpl type = this.javaTypeMap.get(name);
		if(type==null){
			type = new TypeModelImpl(name,null,false);
			this.javaTypeMap.put(name, type);
		}
		return type;
	}
}
