package com.mulesoft.jaxrs.raml.generator.annotations.tests;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

@Path("/item")
public class ItemResource {
	@Context
	UriInfo uriInfo;

	@Path("/a")
	public ItemContentResource getItemContentResource() {
		return new ItemContentResource();
	}

	@GET
	@Produces("application/xml")
	public Item get() {
		return null;
	}

}
