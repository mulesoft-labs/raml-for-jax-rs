package org.raml.jaxrs.examples.path;

import javax.ws.rs.Path;

/**
 *
 */
@Path("classWithBoth")
public class ClassWithBoth {

    @Path("/itsMethod/")
    public void isHere() {}
}
