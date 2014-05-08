package com.mulesoft.jaxrs.raml.annotation.model;

/**
 * Abstract super interface of models
 * contains methods that are required to be supported by all model elements
 *
 */
public interface IBasicModel {

	/**
	 * 
	 * @return name of the element
	 */
	public abstract String getName();

	/**
	 * 
	 * @return documentation for an element
	 */
	public String getDocumentation();

	/**
	 * 
	 * @return all annotations known for a given element
	 */
	IAnnotationModel[] getAnnotations();

	/**
	 * 
	 * @param annotation
	 * @return value for a annotation with a given class name
	 */
	String getAnnotationValue(String annotation);
	
	/**
	 * 
	 * @param annotation
	 * @return values for a annotation with a given class name
	 */
	String[] getAnnotationValues(String annotation);
	
	/**
	 * 
	 * @param name
	 * @return true if element has annotation with a given name
	 */
	boolean hasAnnotation(String name);
	
	/**
	 * 
	 * @param name
	 * @return {@link IAnnotationModel} for annotation with given name if present, <code>null</code> otherwise
	 */
	IAnnotationModel getAnnotation(String name);
}