package org.raml.jaxrs.codegen.model;

import java.util.ArrayList;
import java.util.List;

import com.mulesoft.jaxrs.raml.annotation.model.IGenericElement;
import com.mulesoft.jaxrs.raml.annotation.model.ITypeParameter;

abstract public class GenericElementModel extends BasicModel implements IGenericElement {
	
	private List<ITypeParameter> typeParameters = new ArrayList<ITypeParameter>();

	public List<ITypeParameter> getTypeParameters() {
		return typeParameters;
	}

	public void setTypeParameters(List<ITypeParameter> typeParameters) {
		this.typeParameters = typeParameters;
	}

}
