/*
 * Copyright 2013 (c) MuleSoft, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.raml.jaxrs.codegen.maven;

import java.util.Collection;
import java.util.LinkedHashMap;

import com.mulesoft.jaxrs.raml.annotation.model.ITypeModel;

public class TypeModelRegistry {
	

	public TypeModelRegistry(){};
	
	
	private final LinkedHashMap<String,ITypeModel> typeMap = new LinkedHashMap<String, ITypeModel>();	
	
	
	private final LinkedHashMap<String,ITypeModel> targetTypeMap = new LinkedHashMap<String, ITypeModel>();
	

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
	
	public void registerTargetType(ITypeModel type)
	{
		if(type==null){
			return;
		}
		String qualifiedName = type.getFullyQualifiedName();
		if(qualifiedName==null){
			return;
		}
		typeMap.put(qualifiedName, type);
		targetTypeMap.put(qualifiedName, type);
	}
	
	
	public ITypeModel getType(String qualifiedName){
		return typeMap.get(qualifiedName);
	}
	
	
	public Collection<ITypeModel> getTypes(){
		return typeMap.values();
	}
	
	public Collection<ITypeModel> getTargetTypes(){
		return targetTypeMap.values();
	}

}
