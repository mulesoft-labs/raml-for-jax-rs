package org.raml.jaxrs.codegen.model;

import com.mulesoft.jaxrs.raml.annotation.model.IParameterModel;

public class ParameterModel extends BasicModel implements IParameterModel{

	public ParameterModel() {
	}
	
	
	String type;
	
	boolean required;
	
	
	public String getType() {
		return type;
	}

	
	public boolean required() {
		return required;
	}


	public void setType(String type) {
		this.type = type;
	}


	public void setRequired(boolean required) {
		this.required = required;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (required ? 1231 : 1237);
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		ParameterModel other = (ParameterModel) obj;
		if (required != other.required)
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

}
