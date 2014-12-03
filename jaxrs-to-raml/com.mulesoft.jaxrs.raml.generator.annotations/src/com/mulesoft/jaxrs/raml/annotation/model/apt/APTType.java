package com.mulesoft.jaxrs.raml.annotation.model.apt;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import com.mulesoft.jaxrs.raml.annotation.model.IFieldModel;
import com.mulesoft.jaxrs.raml.annotation.model.IMethodModel;
import com.mulesoft.jaxrs.raml.annotation.model.ITypeModel;

public class APTType extends APTModel implements ITypeModel{

	private TypeElement element;

	public APTType(TypeElement element) {
		this.element = element;
	}
	
	
	public IMethodModel[] getMethods() {
		List<? extends Element> enclosedElements = element.getEnclosedElements();
		ArrayList<IMethodModel>result=new ArrayList<IMethodModel>();
		for (Element r:enclosedElements){
			if (r instanceof ExecutableElement){
				ExecutableElement x=(ExecutableElement) r;
				result.add(new APTMethodModel(x));
			}
		}
		return result.toArray(new IMethodModel[result.size()]);
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
		APTType other = (APTType) obj;
		if (element == null) {
			if (other.element != null)
				return false;
		} else if (!element.equals(other.element))
			return false;
		return true;
	}

	
	public String getFullyQualifiedName() {
		return null; //Can't get it without utils
	}


	@Override
	public IFieldModel[] getFields() {
		List<? extends Element> enclosedElements = element.getEnclosedElements();
		ArrayList<IFieldModel>result=new ArrayList<IFieldModel>();
		for (Element r:enclosedElements){
			if (r instanceof VariableElement){
				VariableElement x=(VariableElement) r;
				result.add(new APTFieldModel(x));
			}
		}
		return result.toArray(new IFieldModel[result.size()]);
	}
}
