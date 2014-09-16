package com.mulesoft.jaxrs.raml.annotation.model;

import java.util.Set;

import org.raml.model.ActionType;
import org.raml.model.Protocol;

public interface IRamlConfig {

	String getTitle();
	
	String getBaseUrl();
	
	public String getVersion();
	
	public Set<Protocol>getProtocols();
	
	String getResponseCode(ActionType type);

	boolean isSingle();

	void setSingle(boolean selection);

	boolean isSorted();

	boolean doFullTree();
	
	

}
