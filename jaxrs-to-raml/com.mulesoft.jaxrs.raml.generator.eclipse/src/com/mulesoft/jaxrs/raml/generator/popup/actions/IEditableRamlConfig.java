package com.mulesoft.jaxrs.raml.generator.popup.actions;

import java.util.Set;

import org.raml.model.ActionType;
import org.raml.model.Protocol;

import com.mulesoft.jaxrs.raml.annotation.model.IRamlConfig;

public interface IEditableRamlConfig extends IRamlConfig{

	public void setTitle(String title);
	
	public void setBaseUrl(String baseUrl);
	
	public void setProtocols(Set<Protocol>ps);

	public void setVersion(String value);

	public void setSorted(boolean selection);
	
	public void setDoFullTree(boolean selection);

	public void setDefaultResponseCode(ActionType a, String text);

	

	
}
