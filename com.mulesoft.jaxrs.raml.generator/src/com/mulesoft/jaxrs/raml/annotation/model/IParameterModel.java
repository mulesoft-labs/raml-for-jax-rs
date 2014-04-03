package com.mulesoft.jaxrs.raml.annotation.model;

public interface IParameterModel extends IBasicModel{

	String getName();
	
	String getType();
	
	boolean required();
	
	IAnnotationModel[] getAnnotations();
}
