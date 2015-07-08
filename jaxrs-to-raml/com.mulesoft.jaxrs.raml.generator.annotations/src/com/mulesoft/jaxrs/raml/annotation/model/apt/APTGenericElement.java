package com.mulesoft.jaxrs.raml.annotation.model.apt;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;

import com.mulesoft.jaxrs.raml.annotation.model.IGenericElement;
import com.mulesoft.jaxrs.raml.annotation.model.ITypeParameter;

abstract public class APTGenericElement extends APTModel implements IGenericElement {

	
	public APTGenericElement(TypeElement type, ProcessingEnvironment environment) {
		super(environment);
		this.type = type;
	}
	
	public APTGenericElement(ExecutableElement x, ProcessingEnvironment environment) {
		super(environment);
		this.executable = x;
	}
	
	protected TypeElement type;
	
	protected ExecutableElement executable;

	@Override
	public List<ITypeParameter> getTypeParameters() {
		
		List<? extends TypeParameterElement> typeParameters = null;
		if(this.type!=null){
			typeParameters = this.type.getTypeParameters();
		}
		else if(this.executable!=null){
			typeParameters = this.executable.getTypeParameters();
		}
		ArrayList<ITypeParameter> list = new ArrayList<ITypeParameter>();
		if (typeParameters != null) {
			for (TypeParameterElement tpe : typeParameters) {
				APTTypeParameter model = new APTTypeParameter(tpe);
				list.add(model);
			}
		}
		return list;
	}
}
