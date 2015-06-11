package com.mulesoft.jaxrs.raml.annotation.model.apt;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import com.mulesoft.jaxrs.raml.annotation.model.IFieldModel;
import com.mulesoft.jaxrs.raml.annotation.model.ITypeModel;

/**
 * <p>APTFieldModel class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class APTFieldModel extends APTModel implements IFieldModel{

	private VariableElement element;
	
	private boolean isGeneric;

	/**
	 * <p>Constructor for APTFieldModel.</p>
	 *
	 * @param q a {@link javax.lang.model.element.VariableElement} object.
	 */
	public APTFieldModel(VariableElement q) {
		this.element=q;
	}
	/**
	 * <p>getType.</p>
	 *
	 * @return a {@link com.mulesoft.jaxrs.raml.annotation.model.ITypeModel} object.
	 */
	public ITypeModel getType() {
		TypeMirror returnType = element.asType();
		if (returnType != null && returnType instanceof DeclaredType) {
			DeclaredType declaredType = (DeclaredType) returnType;
			TypeElement returnTypeElement = (TypeElement) declaredType.asElement();
			return new APTType(returnTypeElement);
		}
		return null;
	}

	
	/**
	 * <p>required.</p>
	 *
	 * @return a boolean.
	 */
	public boolean required() {
		return false;
	}

	
	/**
	 * <p>element.</p>
	 *
	 * @return a {@link javax.lang.model.element.Element} object.
	 */
	public Element element() {
		return element;
	}

	
	/**
	 * <p>hashCode.</p>
	 *
	 * @return a int.
	 */
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((element == null) ? 0 : element.hashCode());
		return result;
	}

	
	/** {@inheritDoc} */
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
	/** {@inheritDoc} */
	@Override
	public boolean isStatic() {
		return false;
	}
	/** {@inheritDoc} */
	@Override
	public boolean isPublic() {
		return false;
	}
	/** {@inheritDoc} */
	@Override
	public ITypeModel getJAXBType() {
		return null;
	}
	/** {@inheritDoc} */
	@Override
	public Class<?> getJavaType() {
		return null;
	}
	public boolean isGeneric() {
		return isGeneric;
	}
	public void setGeneric(boolean isGeneric) {
		this.isGeneric = isGeneric;
	}

}
