package com.mulesoft.jaxrs.raml.annotation.model;

/**
 * <p>IFieldModel interface.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public interface IFieldModel extends IBasicModel,IMember{

	/**
	 * <p>getType.</p>
	 *
	 * @return a {@link com.mulesoft.jaxrs.raml.annotation.model.ITypeModel} object.
	 */
	ITypeModel getType();

	/**
	 * <p>isStatic.</p>
	 *
	 * @return a boolean.
	 */
	boolean isStatic();
	
	/**
	 * <p>isPublic.</p>
	 *
	 * @return a boolean.
	 */
	boolean isPublic();
	/**
	 * <p>isPublic.</p>
	 *
	 * @return a boolean.
	 */
	boolean isGeneric();
}
