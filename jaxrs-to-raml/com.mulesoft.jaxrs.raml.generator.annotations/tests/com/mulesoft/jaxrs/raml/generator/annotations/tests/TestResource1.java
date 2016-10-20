package com.mulesoft.jaxrs.raml.generator.annotations.tests;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/users/{username}")
public class TestResource1 {

	@GET
	@Produces("text/xml")
	public String getUser(@PathParam("username") String userName) {
		return null;
	}

	/**
	 * This is some method
	 * Which we are using for demo
	 * @param userName
	 * @param age
	 * @return
	 */
	@PUT
	@POST
	@Path("/qqq/{someBoolean}")
	@Produces({"application/json","application/xml"})	
	public String getUser2(
			@PathParam("username") String userName,
			/**
			 * some documentation
			 */
			@QueryParam("age") int age,
			@QueryParam("defaults")boolean jj,
			@PathParam("someBoolean")boolean zz,
			@HeaderParam("h")
			String headerParam) {
		return null;
	}
	
	/**
	 * This is another method
	 * Which we are using for demo
	 * @param userName names of the user
	 * @param age desired age
	 * @return information about user object 
	 */
	@PUT
	@POST
	@Path("/qqq2/{someBoolean}")
	@Produces({MediaType.TEXT_XML,"application/xml"})	
	public String getUser3(
			@PathParam("username") String userName,
			/**
			 * some documentation
			 */
			@QueryParam("age") Integer age,
			@QueryParam("defaults")Boolean jj,
			@PathParam("someBoolean")Boolean zz,
			@HeaderParam("h")
			String headerParam) {
		return null;
	}
}