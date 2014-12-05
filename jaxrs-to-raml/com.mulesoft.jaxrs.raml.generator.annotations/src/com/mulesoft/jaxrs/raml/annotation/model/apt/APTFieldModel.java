package com.mulesoft.jaxrs.raml.annotation.model.apt;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import com.mulesoft.jaxrs.raml.annotation.model.IFieldModel;
import com.mulesoft.jaxrs.raml.annotation.model.ITypeModel;

public class APTFieldModel extends APTModel implements IFieldModel{

	private VariableElement element;

	public APTFieldModel(VariableElement q) {
		this.element=q;
	}
	public ITypeModel getType() {
		TypeMirror returnType = element.asType();
		if (returnType != null && returnType instanceof DeclaredType) {
			DeclaredType declaredType = (DeclaredType) returnType;
			TypeElement returnTypeElement = (TypeElement) declaredType.asElement();
			return new APTType(returnTypeElement);
		}
		return null;
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
		APTFieldModel other = (APTFieldModel) obj;
		if (element == null) {
			if (other.element != null)
				return false;
		} else if (!element.equals(other.element))
			return false;
		return true;
	}
	@Override
	public boolean isStatic() {
		return false;
	}
	@Override
	public boolean isPublic() {
		return false;
	}
	@Override
	public ITypeModel getJAXBType() {
		return null;
	}
	@Override
	public Class<?> getJavaType() {
		return null;
	}

}
