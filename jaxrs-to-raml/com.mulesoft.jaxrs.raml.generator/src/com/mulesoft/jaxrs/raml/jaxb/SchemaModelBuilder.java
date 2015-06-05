package com.mulesoft.jaxrs.raml.jaxb;

import java.util.HashMap;
import java.util.HashSet;

import org.raml.schema.model.ISchemaType;
import org.raml.schema.model.SimpleType;
import org.raml.schema.model.impl.PropertyModelImpl;
import org.raml.schema.model.impl.TypeModelImpl;

import com.mulesoft.jaxrs.raml.annotation.model.ITypeModel;

public class SchemaModelBuilder {
	
	public SchemaModelBuilder() {
		super();
	}
	
	private HashMap<String,TypeModelImpl> javaTypeMap = new HashMap<String, TypeModelImpl>();
	
	private HashMap<String,TypeModelImpl> jaxbTypeMap = new HashMap<String, TypeModelImpl>();

	public ISchemaType buildSchemaModel(JAXBType jaxbType){
		ISchemaType typeModel = generateType(jaxbType);
		return typeModel;
	}

	HashSet<JAXBType>onStack=new HashSet<JAXBType>();
	
	private ISchemaType generateType(JAXBType jaxbType) {
		
		String qualifiedName = ((ITypeModel)jaxbType.originalType).getFullyQualifiedName();
		ISchemaType primitive = getPrimitiveType(qualifiedName);
		if(primitive!=null){
			return primitive;
		}
		
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
				ISchemaType propertyType = generateType(jaxbType);
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
		
		ISchemaType primitive = getPrimitiveType(clazz.getCanonicalName());
		if(primitive!=null){
			return primitive;
		}
		
		String name = clazz.getCanonicalName();;
		TypeModelImpl type = this.javaTypeMap.get(name);
		if(type==null){
			type = new TypeModelImpl(name,null,false);
			this.javaTypeMap.put(name, type);
		}
		return type;
	}
	
	ISchemaType getPrimitiveType(String qualifiedName){
		
		String name = qualifiedName;
		if(qualifiedName.startsWith("java.lang.")){
			name = qualifiedName.substring("java.lang.".length());
		}
		name = name.toUpperCase();
		if(name.equals("CHAR")){
			name = "CHARACTER";				
		}
		
		try{
			ISchemaType type = SimpleType.valueOf(name);
			return type;
		}
		catch(Exception e){}
		return null;
	}
}
