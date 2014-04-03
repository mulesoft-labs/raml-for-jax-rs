package com.mulesoft.jaxrs.raml.annotation.model;

public interface IBasicModel {

	public abstract String getName();

	public String getDocumentation();

	IAnnotationModel[] getAnnotations();

	String getAnnotationValue(String annotation);
	
	String[] getAnnotationValues(String annotation);
	
	boolean hasAnnotation(String name);
}