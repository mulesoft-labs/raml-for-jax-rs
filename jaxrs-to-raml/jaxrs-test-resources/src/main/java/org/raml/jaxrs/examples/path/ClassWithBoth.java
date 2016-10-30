package org.raml.jaxrs.examples.path;

import javax.ws.rs.Path;

/**
 *
 */
@Path("/theBigToto")
public class ClassWithBoth {

    @Path("/isHere")
    public void isHere() {}
}
