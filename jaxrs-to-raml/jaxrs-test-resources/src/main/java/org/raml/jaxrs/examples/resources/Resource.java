package org.raml.jaxrs.examples.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/classWithMethods")
public class Resource {

    @GET
    public String get() {
        return "get";
    }

    @Consumes({"text/xml", "application/*"})
    @POST
    public void post() {

    }

    @Path("/head/")
    @HEAD
    public String head() {
        return "head";
    }
}
