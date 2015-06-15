package com.mulesoft.jaxrs.raml.jaxb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.raml.schema.model.ISchemaProperty;
import org.raml.schema.model.ISchemaType;
import org.raml.schema.model.SimpleType;
import org.raml.schema.model.impl.MapPropertyImpl;
import org.raml.schema.model.impl.PropertyModelImpl;
import org.raml.schema.model.impl.TypeModelImpl;

import com.mulesoft.jaxrs.raml.annotation.model.ITypeModel;

public class SchemaModelBuilder {
	
	public SchemaModelBuilder() {
		super();
	}
	
	private HashMap<String,TypeModelImpl> javaTypeMap = new HashMap<String, TypeModelImpl>();
	
	private HashMap<String,TypeModelImpl> jaxbTypeMap = new HashMap<String, TypeModelImpl>();

	public ISchemaType buildSchemaModel(JAXBType jaxbType, StructureType st){
		ISchemaType typeModel = generateType(jaxbType,st);
		return typeModel;
	}

	HashSet<JAXBType>onStack=new HashSet<JAXBType>();
	
	private ISchemaType generateType(JAXBType jaxbType, StructureType structureType) {
		
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
		HashMap<String,String> namespaces = jaxbType.gatherNamespaces();
		TypeModelImpl typeModel = new TypeModelImpl(xmlName,jaxbType.getClassName(),namespaces,structureType);
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
		StructureType st = p.getStructureType();
		if (p instanceof JAXBAttributeProperty){			
			prop = new PropertyModelImpl(name, getType(p), p.required, true, st,namespace);
		}
		else if (p instanceof JAXBValueProperty){
			prop = new PropertyModelImpl(name, getType(p), p.required, false, st,namespace);
		}
		else if (p instanceof JAXBElementProperty){
			JAXBElementProperty el=(JAXBElementProperty) p;
			List<JAXBType> jaxbTypes = p.isGeneric() ? null : el.getJAXBTypes();
			if (jaxbTypes!=null&&!jaxbTypes.isEmpty()){
				if(st==StructureType.MAP){
					ArrayList<ISchemaType> list = new ArrayList<ISchemaType>();
					for(JAXBType t : jaxbTypes){
						list.add(generateType(t, StructureType.COMMON));
					}
					prop = new MapPropertyImpl(name, list, p.required, false, StructureType.MAP, namespace);
				}
				else{
					ISchemaType propertyType = generateType(jaxbTypes.get(0),st);
					prop = new PropertyModelImpl(name, propertyType, p.required, false, st,namespace);
				}
			}
			else{
				prop = new PropertyModelImpl(name, getType(p), p.required, false, st,namespace);
			}
		}		
		if(prop!=null){
			prop.setGeneric(p.isGeneric());
			typeModel.addProperty(prop);
		}
	}

	private ISchemaType getType(JAXBProperty p) {
		Class<?> clazz = p.asJavaType();
		ISchemaType primitive = getPrimitiveType(clazz.getCanonicalName());
		if(primitive!=null){
			return primitive;
		}
		
		String name = clazz.getCanonicalName();;
		TypeModelImpl type = this.javaTypeMap.get(name);
		if(type==null){
			type = new TypeModelImpl(name,clazz.getCanonicalName(),null,false,p.getStructureType());
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
