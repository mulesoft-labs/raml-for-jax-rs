package com.mulesoft.jaxrs.raml.annotation.tests;


import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

@Path("/forms2")
@Consumes(MediaType.MULTIPART_FORM_DATA)
public class TestResource4 {

	
	@POST		
	public String postForm(
		@DefaultValue("true")	
		@FormParam("enabled") boolean enabled) {		
		return "";
	}
}
