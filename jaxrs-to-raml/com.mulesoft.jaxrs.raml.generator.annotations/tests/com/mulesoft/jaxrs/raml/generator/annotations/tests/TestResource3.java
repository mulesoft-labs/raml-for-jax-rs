package com.mulesoft.jaxrs.raml.generator.annotations.tests;


import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/")
@Consumes(MediaType.MULTIPART_FORM_DATA)
public class TestResource3 {

	@QueryParam(value = "ttt") int ttt;
	
	@Path("")
	public HelloWorldRest getRoot(){
		return null;		
	}
}
