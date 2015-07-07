package com.mulesoft.jaxrs.raml.annotation.tests;


import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

public class TestResource5Parent {

	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public String postForm(
		@DefaultValue("true")	
		@QueryParam("enabled") boolean enabled,
		boolean visible) {		
		return "";
	}
	
	@GET
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public String getForm() {		
		return "";
	}
}
