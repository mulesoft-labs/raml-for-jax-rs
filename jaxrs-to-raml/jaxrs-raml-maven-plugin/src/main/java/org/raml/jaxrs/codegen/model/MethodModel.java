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
import com.mulesoft.jaxrs.raml.annotation.model.reflection.Utils;

/**
 * <p>MethodModel class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class MethodModel extends GenericElementModel implements IMethodModel {

	/**
	 * <p>Constructor for MethodModel.</p>
	 */
	public MethodModel() {
	}
	
	private ArrayList<IParameterModel> parameters = new ArrayList<IParameterModel>();
	
	private ITypeModel returnedType;
	
	
	private ITypeModel bodyType;
	
	protected boolean hasGenericReturnType;
	
	protected boolean hasGenericBodyType;
	
	/**
	 * <p>Getter for the field <code>parameters</code>.</p>
	 *
	 * @return an array of {@link com.mulesoft.jaxrs.raml.annotation.model.IParameterModel} objects.
	 */
	public IParameterModel[] getParameters() {
		return parameters.toArray(new IParameterModel[parameters.size()]);
	}

	/**
	 * <p>addParameter.</p>
	 *
	 * @param param a {@link com.mulesoft.jaxrs.raml.annotation.model.IParameterModel} object.
	 */
	public void addParameter(IParameterModel param){
		parameters.add(param);
	}
	
	/**
	 * <p>getBasicDocInfo.</p>
	 *
	 * @return a {@link com.mulesoft.jaxrs.raml.annotation.model.IDocInfo} object.
	 */
	public IDocInfo getBasicDocInfo() {
		return new IDocInfo() {
			
			public String getReturnInfo() {
				return Utils.extractReturnJavadoc(MethodModel.this.getDocumentation());
			}
			
			
			public String getDocumentation(String pName) {
				return Utils.extractParamJavadoc(MethodModel.this.getDocumentation(), pName);
			}
			
			public String getDocumentation() {
				return Utils.extractMethodJavadoc(MethodModel.this.getDocumentation());
			}
		};
	}
	
	/**
	 * <p>Getter for the field <code>returnedType</code>.</p>
	 *
	 * @return a {@link com.mulesoft.jaxrs.raml.annotation.model.ITypeModel} object.
	 */
	public ITypeModel getReturnedType() {
		return returnedType;
	}
	
	
	/**
	 * <p>Setter for the field <code>returnedType</code>.</p>
	 *
	 * @param returnType a {@link com.mulesoft.jaxrs.raml.annotation.model.ITypeModel} object.
	 */
	public void setReturnedType(ITypeModel returnType) {
		this.returnedType = returnType;
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		if(parameters != null && !parameters.isEmpty()){
			for(IParameterModel param : parameters){
				result = prime * result + param.getParameterType().hashCode();
				result = prime * result + param.getName().hashCode();
			}
		}
		if(returnedType != null){
			result = prime * result + returnedType.getFullyQualifiedName().hashCode();			
		}
		return result;
	}

	/** {@inheritDoc} */
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

	/**
	 * <p>Getter for the field <code>bodyType</code>.</p>
	 *
	 * @return a {@link com.mulesoft.jaxrs.raml.annotation.model.ITypeModel} object.
	 */
	public ITypeModel getBodyType() {		
		return bodyType;
	}

	/**
	 * <p>Setter for the field <code>bodyType</code>.</p>
	 *
	 * @param bodyType a {@link com.mulesoft.jaxrs.raml.annotation.model.ITypeModel} object.
	 */
	public void setBodyType(ITypeModel bodyType) {
		this.bodyType = bodyType;
	}

	public boolean hasGenericReturnType() {
		return hasGenericReturnType;
	}

	public void setHasGenericReturnType(boolean isGeneric) {
		this.hasGenericReturnType = isGeneric;
	}
	
	public boolean hasGenericBodyType() {
		return hasGenericReturnType;
	}

	public void setHasGenericBodyType(boolean isGeneric) {
		this.hasGenericReturnType = isGeneric;
	}
	
}
