package com.mulesoft.jaxrs.raml.annotation.model;

/**
 *
 * Model for a rest method
 *
 * @author kor
 * @version $Id: $Id
 */
public interface IMethodModel extends IBasicModel,IMember {
	
	/**
	 * <p>getParameters.</p>
	 *
	 * @return information about parameters
	 */
	public abstract IParameterModel[] getParameters();
	
	/**
	 * <p>getBasicDocInfo.</p>
	 *
	 * @return documentation on the method
	 */
	IDocInfo getBasicDocInfo();
	
	/**
	 * <p>getReturnedType.</p>
	 *
	 * @return information about return type
	 */
	ITypeModel getReturnedType();
	
	/**
	 * <p>getBodyType.</p>
	 *
	 * @return information about body type
	 */
	ITypeModel getBodyType();

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
}
