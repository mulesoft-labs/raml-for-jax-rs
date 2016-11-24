package org.raml.jaxrs.examples.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/classWithMethods")
public class Resource {

    @Path("/path/to/get/")
    @GET
    @Produces("application/xml")
    public String getStuff() {
        return "get";
    }

    @Consumes({"text/xml", "application/*"})
    @POST
    public void postStuff() {

    }

    @Path("/head/")
    @HEAD
    public String head() {
        return "head";
    }
}
