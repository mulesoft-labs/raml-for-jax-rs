package com.mulesoft.jaxrs.raml.generator.annotations.tests;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

public class ItemContentResource {
 
    @GET
    public Response get() { 
    	return null;
    }
 
    @PUT
    @Path("{version}")
    
    public void put(@PathParam("version") int version,
                    @Context HttpHeaders headers,
                    byte[] in) {
        
    }
}