package com.mulesoft.jaxrs.raml.annotation.model;

/**
 * <p>IMember interface.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public interface IMember extends IBasicModel{

	/**
	 * <p>isStatic.</p>
	 *
	 * @return a boolean.
	 */
	public abstract boolean isStatic();
	/**
	 * <p>isPublic.</p>
	 *
	 * @return a boolean.
	 */
	public abstract boolean isPublic();
	
	/**
	 * <p>getType.</p>
	 *
	 * @return a {@link com.mulesoft.jaxrs.raml.annotation.model.ITypeModel} object.
	 */
	ITypeModel getType();
	
	/**
	 * <p>getJAXBType.</p>
	 *
	 * @return a {@link com.mulesoft.jaxrs.raml.annotation.model.ITypeModel} object.
	 */
	ITypeModel getJAXBType();
	/**
	 * <p>getJavaType.</p>
	 *
	 * @return a {@link java.lang.Class} object.
	 */
	public abstract Class<?> getJavaType();
}
