package com.mulesoft.jaxrs.raml.annotation.model;

/**
 * Abstract super interface of models
 * contains methods that are required to be supported by all model elements
 *
 * @author kor
 * @version $Id: $Id
 */
public interface IBasicModel {

	/**
	 * <p>getName.</p>
	 *
	 * @return name of the element
	 */
	public abstract String getName();

	/**
	 * <p>getDocumentation.</p>
	 *
	 * @return documentation for an element
	 */
	public String getDocumentation();

	/**
	 * <p>getAnnotations.</p>
	 *
	 * @return all annotations known for a given element
	 */
	IAnnotationModel[] getAnnotations();

	/**
	 * <p>getAnnotationValue.</p>
	 *
	 * @param annotation a {@link java.lang.String} object.
	 * @return value for a annotation with a given class name
	 */
	String getAnnotationValue(String annotation);
	
	/**
	 * <p>getAnnotationValues.</p>
	 *
	 * @param annotation a {@link java.lang.String} object.
	 * @return values for a annotation with a given class name
	 */
	String[] getAnnotationValues(String annotation);
	
	/**
	 * <p>hasAnnotation.</p>
	 *
	 * @param name a {@link java.lang.String} object.
	 * @return true if element has annotation with a given name
	 */
	boolean hasAnnotation(String name);
	
	/**
	 * <p>getAnnotation.</p>
	 *
	 * @param name a {@link java.lang.String} object.
	 * @return {@link com.mulesoft.jaxrs.raml.annotation.model.IAnnotationModel} for annotation with given name if present, <code>null</code> otherwise
	 */
	IAnnotationModel getAnnotation(String name);
	
	/**
	 * <p>hasAnnotation.</p>
	 *
	 * @param name a {@link java.lang.String} object.
	 * @return true if element has annotation with a given canonical name
	 */
	boolean hasAnnotationWithCanonicalName(String name);
	
	/**
	 * <p>getAnnotation.</p>
	 *
	 * @param name a {@link java.lang.String} object.
	 * @return {@link com.mulesoft.jaxrs.raml.annotation.model.IAnnotationModel} for annotation with given canonical name if present, <code>null</code> otherwise
	 */
	IAnnotationModel getAnnotationByCanonicalName(String name);
}
