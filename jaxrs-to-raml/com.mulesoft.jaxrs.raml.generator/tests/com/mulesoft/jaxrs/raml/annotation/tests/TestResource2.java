package com.mulesoft.jaxrs.raml.annotation.tests;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/test2")
@Produces("application/json")
@Consumes("application/xml")
public class TestResource2 {

	@PUT
	@POST
	@Path("/qqq")
	
	public String getUser2(@FormParam("name") String name) {
		return null;
	}
}
