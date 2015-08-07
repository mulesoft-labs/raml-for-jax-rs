package com.mulesoft.jaxrs.raml.annotation.model;

/**
 * <p>IAnnotationModel interface.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public interface IAnnotationModel {

	/**
	 * <p>getName.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getName();
	
	/**
	 * <p>getFullyQualifiedName.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	String getCanonicalName();
	
	/**
	 * <p>getValue.</p>
	 *
	 * @param pairName a {@link java.lang.String} object.
	 * @return a {@link java.lang.String} object.
	 */
	public String getValue(String pairName);

	/**
	 * <p>getValues.</p>
	 *
	 * @param value a {@link java.lang.String} object.
	 * @return an array of {@link java.lang.String} objects.
	 */
	public String[] getValues(String value);
	
	/**
	 * <p>getSubAnnotations.</p>
	 *
	 * @param pairName a {@link java.lang.String} object.
	 * @return an array of {@link com.mulesoft.jaxrs.raml.annotation.model.IAnnotationModel} objects.
	 */
	IAnnotationModel[] getSubAnnotations(String pairName);
}
