package org.raml.jaxrs.examples.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/somewherez/{parameter}")
public class ResourceWithPathParam {

    @GET
    @Consumes("text/*")
    public String getSomething(@PathParam("parameter") String parameter) {
        return "";
    }

}
