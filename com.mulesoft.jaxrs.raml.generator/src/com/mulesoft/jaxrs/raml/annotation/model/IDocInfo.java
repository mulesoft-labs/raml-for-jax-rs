package com.mulesoft.jaxrs.raml.annotation.model;

public interface IDocInfo {

	String getDocumentation();

	String getDocumentation(String pName);
	
	String getReturnInfo();
}
