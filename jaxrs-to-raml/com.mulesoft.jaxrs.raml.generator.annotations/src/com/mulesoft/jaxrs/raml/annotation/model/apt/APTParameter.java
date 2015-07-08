package com.mulesoft.jaxrs.raml.annotation.model.apt;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import com.mulesoft.jaxrs.raml.annotation.model.IParameterModel;

/**
 * <p>APTParameter class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class APTParameter extends APTModel implements IParameterModel{

	private VariableElement element;

	/**
	 * <p>Constructor for APTParameter.</p>
	 *
	 * @param q a {@link javax.lang.model.element.VariableElement} object.
	 */
	public APTParameter(VariableElement q, ProcessingEnvironment environment) {
		super(environment);
		this.element=q;
	}

	
	/**
	 * <p>getType.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getParameterType() {
		TypeMirror asType = element.asType();
		return asType.toString();
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
		APTParameter other = (APTParameter) obj;
		if (element == null) {
			if (other.element != null)
				return false;
		} else if (!element.equals(other.element))
			return false;
		return true;
	}

}
