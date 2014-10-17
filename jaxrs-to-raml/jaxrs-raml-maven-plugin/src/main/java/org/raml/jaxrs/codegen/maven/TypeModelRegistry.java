package org.raml.jaxrs.codegen.maven;

import java.util.Collection;
import java.util.LinkedHashMap;

import com.mulesoft.jaxrs.raml.annotation.model.ITypeModel;

public class TypeModelRegistry {
	

	public TypeModelRegistry(){};
	
	
	private final LinkedHashMap<String,ITypeModel> typeMap = new LinkedHashMap<String, ITypeModel>();	
	
	
	public void registerType(ITypeModel type)
	{
		if(type==null){
			return;
		}
		String qualifiedName = type.getFullyQualifiedName();
		if(qualifiedName==null){
			return;
		}
		typeMap.put(qualifiedName, type);
	}
	
	
	public ITypeModel getType(String qualifiedName){
		return typeMap.get(qualifiedName);
	}
	
	
	public Collection<ITypeModel> getTypes(){
		return typeMap.values();
	}

}
