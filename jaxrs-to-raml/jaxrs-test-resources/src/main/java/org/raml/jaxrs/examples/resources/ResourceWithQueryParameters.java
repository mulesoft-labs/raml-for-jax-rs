package org.raml.jaxrs.examples.resources;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.xml.ws.Response;

@Path("/left/right/left")
public class ResourceWithQueryParameters {

    @Path("step")
    @POST
    public Response postWithQueryParameters(@DefaultValue("military") @QueryParam("typeOfStep") String typeOfStep) {
        return null;
    }
}
