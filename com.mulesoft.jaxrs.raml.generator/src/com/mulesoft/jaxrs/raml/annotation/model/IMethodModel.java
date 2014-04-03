package com.mulesoft.jaxrs.raml.annotation.model;

public interface IMethodModel extends IBasicModel {
	
	public abstract IParameterModel[] getParameters();
	
	IDocInfo getBasicDocInfo();
}
