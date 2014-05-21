package com.mulesoft.jaxrs.raml.annotation.model;

public interface IAnnotationModel {

	public String getName();
	
	public String getValue(String pairName);

	public String[] getValues(String value);
	
	IAnnotationModel[] getSubAnnotations(String pairName);
}
