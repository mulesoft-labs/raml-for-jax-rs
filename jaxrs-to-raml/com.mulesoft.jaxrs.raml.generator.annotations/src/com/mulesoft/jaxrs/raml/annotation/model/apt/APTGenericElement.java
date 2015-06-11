package com.mulesoft.jaxrs.raml.annotation.model.apt;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Parameterizable;
import javax.lang.model.element.TypeParameterElement;

import com.mulesoft.jaxrs.raml.annotation.model.IGenericElement;
import com.mulesoft.jaxrs.raml.annotation.model.ITypeParameter;

abstract public class APTGenericElement<T extends Parameterizable> extends APTModel implements IGenericElement {

	public APTGenericElement(T element) {
		super();
		this.element = element;
	}
	
	protected T element;

	@Override
	public List<ITypeParameter> getTypeParameters() {
		
		List<? extends TypeParameterElement> typeParameters = this.element.getTypeParameters();
		ArrayList<ITypeParameter> list = new ArrayList<ITypeParameter>();
		for(TypeParameterElement tpe : typeParameters){
			APTTypeParameter model = new APTTypeParameter(tpe);
			list.add(model);
		}
		return list;
	}
}
