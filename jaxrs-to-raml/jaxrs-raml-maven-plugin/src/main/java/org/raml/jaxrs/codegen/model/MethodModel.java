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

import com.mulesoft.jaxrs.raml.annotation.model.IDocInfo;
import com.mulesoft.jaxrs.raml.annotation.model.IMethodModel;
import com.mulesoft.jaxrs.raml.annotation.model.IParameterModel;
import com.mulesoft.jaxrs.raml.annotation.model.ITypeModel;

public class MethodModel extends BasicModel implements IMethodModel {

	public MethodModel() {
	}
	
	private ArrayList<IParameterModel> parameters = new ArrayList<IParameterModel>();
	
	private ITypeModel returnedType;
	
	
	private ITypeModel bodyType;

	
	public IParameterModel[] getParameters() {
		return parameters.toArray(new IParameterModel[parameters.size()]);
	}

	public void addParameter(IParameterModel param){
		parameters.add(param);
	}
	
	public IDocInfo getBasicDocInfo() {
		return new IDocInfo() {
			
			
			public String getReturnInfo() {
				return ""; //$NON-NLS-1$
			}
			
			
			public String getDocumentation(String pName) {
				return ""; //$NON-NLS-1$
			}
			
			
			public String getDocumentation() {
				return ""; //$NON-NLS-1$
			}
		};
	}
	
	public ITypeModel getReturnedType() {
		return returnedType;
	}
	
	
	public void setReturnedType(ITypeModel returnType) {
		this.returnedType = returnType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		if(parameters != null && !parameters.isEmpty()){
			for(IParameterModel param : parameters){
				result = prime * result + param.getType().hashCode();
				result = prime * result + param.getName().hashCode();
			}
		}
		if(returnedType != null){
			result = prime * result + returnedType.getFullyQualifiedName().hashCode();			
		}
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
		MethodModel other = (MethodModel) obj;
		if (parameters == null) {
			if (other.parameters != null)
				return false;
		} else if (!parameters.equals(other.parameters))
			return false;
		if (returnedType == null) {
			if (other.returnedType != null)
				return false;
		} else if (!returnedType.equals(other.returnedType))
			return false;
		return true;
	}

	public ITypeModel getBodyType() {		
		return bodyType;
	}

	public void setBodyType(ITypeModel bodyType) {
		this.bodyType = bodyType;
	}

	@Override
	public ITypeModel getType() {
		throw new UnsupportedOperationException();
	}
	
}
