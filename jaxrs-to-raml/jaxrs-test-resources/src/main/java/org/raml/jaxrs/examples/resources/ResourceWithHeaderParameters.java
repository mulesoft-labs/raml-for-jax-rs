package org.raml.jaxrs.examples.resources;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.xml.ws.Response;

@Path("started/from")
public class ResourceWithHeaderParameters {

    @Path("theBottom")
    @POST
    public Response postDrakeSong(@HeaderParam("title") String theSongTitle) {
        return null;
    }
}
