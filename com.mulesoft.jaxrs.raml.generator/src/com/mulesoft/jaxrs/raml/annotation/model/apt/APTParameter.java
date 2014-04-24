package com.mulesoft.jaxrs.raml.annotation.model.apt;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import com.mulesoft.jaxrs.raml.annotation.model.IParameterModel;

public class APTParameter extends APTModel implements IParameterModel{

	private VariableElement element;

	public APTParameter(VariableElement q) {
		this.element=q;
	}

	
	public String getType() {
		TypeMirror asType = element.asType();
		return asType.toString();
	}

	
	public boolean required() {
		return false;
	}

	
	public Element element() {
		return element;
	}

	
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((element == null) ? 0 : element.hashCode());
		return result;
	}

	
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		APTParameter other = (APTParameter) obj;
		if (element == null) {
			if (other.element != null)
				return false;
		} else if (!element.equals(other.element))
			return false;
		return true;
	}

}
