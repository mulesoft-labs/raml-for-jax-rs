package com.mulesoft.jaxrs.raml.annotation.model;

/**
 *
 * Model of the type
 *
 * @author kor
 * @version $Id: $Id
 */
public interface ITypeModel extends IBasicModel, IGenericElement{

	
	/**
	 * <p>getMethods.</p>
	 *
	 * @return methods declared in this type
	 */
	IMethodModel[] getMethods();
	
	/**
	 * <p>getFields.</p>
	 *
	 * @return an array of {@link com.mulesoft.jaxrs.raml.annotation.model.IFieldModel} objects.
	 */
	IFieldModel[] getFields();
	
	/**
	 * <p>getFullyQualifiedName.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	String getFullyQualifiedName();
}
