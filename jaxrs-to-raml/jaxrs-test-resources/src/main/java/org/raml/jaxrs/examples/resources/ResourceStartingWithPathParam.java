package org.raml.jaxrs.examples.resources;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/{starting_parameter}/")
public class ResourceStartingWithPathParam {

    @POST
    @Produces("application/xml")
    public String getSomeXmlGarbage() {
        return "";
    }
}
