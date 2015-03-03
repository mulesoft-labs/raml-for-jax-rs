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
package org.raml.jaxrs.codegen.model;

import java.util.ArrayList;

import com.mulesoft.jaxrs.raml.annotation.model.IFieldModel;
import com.mulesoft.jaxrs.raml.annotation.model.IMethodModel;
import com.mulesoft.jaxrs.raml.annotation.model.ITypeModel;

public class TypeModel extends BasicModel implements ITypeModel{

	public TypeModel() {
	}
	
	private String qualifiedName;
	
	private ArrayList<IMethodModel> methods = new ArrayList<IMethodModel>();
	private ArrayList<IFieldModel> fields = new ArrayList<IFieldModel>();
	
	public IMethodModel[] getMethods() {
		return methods.toArray(new IMethodModel[methods.size()]);
	}
	
	public void addMethod(IMethodModel method){
		methods.add(method);
	}

	
	public String getFullyQualifiedName() {
		return qualifiedName;
	}
	
	public void setFullyQualifiedName(String qualifiedName) {
		this.qualifiedName = qualifiedName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((methods == null) ? 0 : methods.hashCode());
		result = prime * result
				+ ((qualifiedName == null) ? 0 : qualifiedName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		TypeModel other = (TypeModel) obj;
		if (methods == null) {
			if (other.methods != null)
				return false;
		} else if (!methods.equals(other.methods))
			return false;
		if (qualifiedName == null) {
			if (other.qualifiedName != null)
				return false;
		} else if (!qualifiedName.equals(other.qualifiedName))
			return false;
		return true;
	}

	@Override
	public IFieldModel[] getFields() {
		return fields.toArray(new IFieldModel[fields.size()]);
	}

	public void addField(IFieldModel fieldModel) {
		fields.add(fieldModel);
	}
}
