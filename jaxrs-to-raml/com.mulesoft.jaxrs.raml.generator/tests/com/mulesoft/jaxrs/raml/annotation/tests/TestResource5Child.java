package com.mulesoft.jaxrs.raml.annotation.tests;


import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

@Path("/root")
public class TestResource5Child extends TestResource5Parent {

	public String postForm(
		boolean enabled,
		@DefaultValue("true")	
		@FormParam("visible") boolean visible) {		
		return "";
	}
}
