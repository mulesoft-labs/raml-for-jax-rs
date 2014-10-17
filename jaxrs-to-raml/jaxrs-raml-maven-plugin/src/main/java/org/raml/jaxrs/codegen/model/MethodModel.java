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
		result = prime * result
				+ ((parameters == null) ? 0 : parameters.hashCode());
		result = prime * result
				+ ((returnedType == null) ? 0 : returnedType.hashCode());
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
		return null;
	}

}
