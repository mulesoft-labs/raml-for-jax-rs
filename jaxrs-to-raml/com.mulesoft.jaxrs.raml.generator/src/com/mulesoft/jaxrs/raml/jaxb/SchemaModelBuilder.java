package com.mulesoft.jaxrs.raml.jaxb;

import java.util.ArrayList;
import java.util.Arrays;
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
import com.mulesoft.jaxrs.raml.annotation.model.StructureType;
import com.mulesoft.jaxrs.raml.annotation.model.reflection.ReflectionType;

public class SchemaModelBuilder {
	
	public SchemaModelBuilder(JAXBRegistry registry) {
		super();
		this.registry = registry;
	}
	
	private JAXBRegistry registry;
	
	private HashMap<String,TypeModelImpl> javaTypeMap = new HashMap<String, TypeModelImpl>();
	
	private HashMap<String,TypeModelImpl> jaxbTypeMap = new HashMap<String, TypeModelImpl>();

	public ISchemaType buildSchemaModel(JAXBType jaxbType, StructureType st){
		ISchemaType typeModel = generateType(jaxbType,st);
		return typeModel;
	}

	HashSet<JAXBType>onStack=new HashSet<JAXBType>();
	
	private ISchemaType generateType(JAXBType jaxbType, StructureType structureType) {
		
		String qualifiedName = ((ITypeModel)jaxbType.originalModel).getFullyQualifiedName();
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
		ISchemaType schemaType = getType(jaxbType,structureType,namespaces);
		if(!(schemaType instanceof TypeModelImpl)){
			return schemaType;
		}
		TypeModelImpl typeModel = (TypeModelImpl) schemaType;
		this.jaxbTypeMap.put(xmlName, typeModel);
		HashMap<String,String>prefixes=jaxbType.gatherNamespaces();
	
		for (JAXBProperty p:jaxbType.getAllProperties()){
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
			if(((JAXBAttributeProperty)p).isAnyAttribute()){
				ISchemaType strType = generateType(
						registry.getJAXBModel(new ReflectionType(String.class)),
						StructureType.COMMON);
				List<ISchemaType> list = Arrays.asList(strType,strType);
				prop = new MapPropertyImpl(name, list, p.required, true, namespace);
			}
			else{
				prop = new PropertyModelImpl(name, getType(p), p.required, true, st,namespace);
			}
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
					prop = new MapPropertyImpl(name, list, p.required, false, namespace);
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

		JAXBType propertyType = p.getType();
		StructureType st = p.getStructureType();
		return getType(propertyType, st, null);
	}

	private ISchemaType getType(JAXBType propertyType, StructureType st, HashMap<String, String> namespaces) {
		String canonicalName = propertyType!=null ? propertyType.getClassName() : "java.lang.Object";
		
		ISchemaType primitive = getPrimitiveType(canonicalName);
		if(primitive!=null){
			return primitive;
		}
		
		TypeModelImpl type = this.javaTypeMap.get(canonicalName);
		if(type==null){
			String xmlName = propertyType.getXMLName();			
			type = new TypeModelImpl(xmlName,canonicalName,namespaces,st);
			this.javaTypeMap.put(canonicalName, type);
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
		if(name.equals("INT")){
			name = "INTEGER";				
		}
		
		try{
			ISchemaType type = SimpleType.valueOf(name);
			return type;
		}
		catch(Exception e){}
		return null;
	}
}
