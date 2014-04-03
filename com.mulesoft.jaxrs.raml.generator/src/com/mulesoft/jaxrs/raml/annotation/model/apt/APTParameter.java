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

	@Override
	public String getType() {
		TypeMirror asType = element.asType();
		return asType.toString();
	}

	@Override
	public boolean required() {
		return false;
	}

	@Override
	public Element element() {
		return element;
	}

}
