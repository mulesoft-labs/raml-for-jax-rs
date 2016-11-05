package org.raml.jaxrs.examples.resources;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/here")
public class Resource {

    @GET
    public String get() {
        return "toto";
    }

    @POST
    public void post() {

    }
}
